package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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

        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);//FIXME 计算平均耗能，应该是乘法吧？
        return averagedCost.multiply(pricePlan.getUnitRate());
    }

    /**
     * 计算一组读数的平均值
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
     * 计算得到这组读数总共耗费的时间（in hour）
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
