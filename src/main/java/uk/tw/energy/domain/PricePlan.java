package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PricePlan {

    private final String energySupplier;
    private final String planName;
    /** 单位用电量的费率，基础费率 */
    private final BigDecimal unitRate; // unit price per kWh
    /** 峰值乘数（每周几，对应一个乘数）*/
    private final List<PeakTimeMultiplier> peakTimeMultipliers;

    public PricePlan(String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return peakTimeMultipliers.stream()
                //基于dateTime是周几，找出那一天的峰值乘数
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
                .findFirst()
                //基于dateTime是周几，找出那一天的峰值乘数
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);
    }

    public BigDecimal getPrice(LocalDate localdate) {
        return peakTimeMultipliers.stream()
                //基于dateTime是周几，找出那一天的峰值乘数
                .filter(multiplier -> multiplier.dayOfWeek.equals(localdate.getDayOfWeek()))
                .findFirst()
                //基于dateTime是周几，找出那一天的峰值乘数
                .map(multiplier -> {
                    return unitRate.multiply(multiplier.multiplier);
                })
                .orElse(unitRate);
    }


    /**
     * 峰值乘数
     */
    public static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }

}
