package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    /**
     * 全部的价格计划
     */
    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    /**
     * 针对一个电表，呈现在每个价格计划下，此电表的花费
     * @param smartMeterId
     * @return
     */
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
//        return Optional.of(pricePlans.stream().collect(
//                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost1(electricityReadings.get(), t))));
    }

    /**
     * 基于一组读数，价格计划，计算出总共花费
     * @param electricityReadings
     * @param pricePlan
     * @return
     */
    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);//FIXME  计算平均耗能，应该是乘法吧？平均功率乘以用电时长
        return averagedCost.multiply(pricePlan.getUnitRate());
    }

    /**
     * 基于一组读数，价格计划，计算出总共花费（考虑高峰定价）
     *
     * @param electricityReadings
     * @param pricePlan
     * @return
     */
    private BigDecimal calculateCost1(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        /** 分组 by 日 */
        Map<LocalDateTime, List<ElectricityReading>> electricityReadingsByDay = electricityReadings.stream()
                .collect(Collectors.groupingBy(electricityReading ->
                        LocalDateTime.ofInstant(electricityReading.getTime(), ZoneId.systemDefault())
                ));

        /** 按日计算钱，再加总 */
        BigDecimal totalMoney = electricityReadingsByDay.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal average = calculateAverageReading(entry.getValue());
                    BigDecimal timeElapsed = calculateTimeElapsed(entry.getValue());

                    BigDecimal averagedCost = average.multiply(timeElapsed);
                    return averagedCost.multiply(pricePlan.getPrice(entry.getKey()));
                })
                .reduce(new BigDecimal(0), (moneyday1, moneyday2) -> moneyday1.add(moneyday2));
        return totalMoney;
    }

    /**
     * 计算一组读数的平均值，单位是 功！率！（智能电表嘛，可以读出功率...）
     * @param electricityReadings
     * @return
     */
    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    /**
     * 计算得到这组读数总共耗费的时间（in hour，以便进一步计算kwh）
     * @param electricityReadings
     * @return
     */
    private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::getTime))
                .get();
        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::getTime))
                .get();

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / 3600.0);
    }

}
