package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Configuration
public class SeedingApplicationDataConfiguration {

    private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
    private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
    private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";

    /**
     * 全部的价格计划
     * @return
     */
    @Bean
    public List<PricePlan> pricePlans() {
        final List<PricePlan> pricePlans = new ArrayList<>();
        List<PricePlan.PeakTimeMultiplier> peakTimeMultipliers = new ArrayList<>();
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, new BigDecimal(1)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, new BigDecimal(1)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, new BigDecimal(1)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.THURSDAY, new BigDecimal(2)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.FRIDAY, new BigDecimal(1)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.SATURDAY, new BigDecimal(1)));
        peakTimeMultipliers.add(new PricePlan.PeakTimeMultiplier(DayOfWeek.SUNDAY, new BigDecimal(1)));

        pricePlans.add(new PricePlan(MOST_EVIL_PRICE_PLAN_ID, "Dr Evil's Dark Energy", BigDecimal.TEN, peakTimeMultipliers));
        pricePlans.add(new PricePlan(RENEWABLES_PRICE_PLAN_ID, "The Green Eco", BigDecimal.valueOf(2), peakTimeMultipliers));
        pricePlans.add(new PricePlan(STANDARD_PRICE_PLAN_ID, "Power for Everyone", BigDecimal.ONE, peakTimeMultipliers));
        return pricePlans;
    }

    /**
     * 每个电表生成20个读数
     * @return
     */
    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {
        final Map<String, List<ElectricityReading>> readings = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        smartMeterToPricePlanAccounts()
                .keySet()
                .forEach(smartMeterId -> readings.put(smartMeterId, electricityReadingsGenerator.generate(20)));
        return readings;
    }

    /**
     * 各个电表所使用的价格计划
     * @return
     */
    @Bean
    public Map<String, String> smartMeterToPricePlanAccounts() {
        final Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
        smartMeterToPricePlanAccounts.put("smart-meter-0", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-1", RENEWABLES_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-2", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-3", STANDARD_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-4", RENEWABLES_PRICE_PLAN_ID);
        return smartMeterToPricePlanAccounts;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
