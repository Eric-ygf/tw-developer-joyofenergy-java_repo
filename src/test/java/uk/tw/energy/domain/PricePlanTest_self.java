package uk.tw.energy.domain;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class PricePlanTest_self {

    private final String ENERGY_SUPPLIER_NAME = "EVIL WHATEVER";

    @Test
    public void test1() {
        List<PricePlan.PeakTimeMultiplier> peakTimeMultiplierList = new ArrayList<>();
        PricePlan.PeakTimeMultiplier thursdayPeakTimeMultiplier1 = new PricePlan.PeakTimeMultiplier(DayOfWeek.THURSDAY, BigDecimal.valueOf(2L));
        PricePlan.PeakTimeMultiplier thursdayPeakTimeMultiplier2 = new PricePlan.PeakTimeMultiplier(DayOfWeek.THURSDAY, BigDecimal.valueOf(4L));
        peakTimeMultiplierList.add(thursdayPeakTimeMultiplier1);
        peakTimeMultiplierList.add(thursdayPeakTimeMultiplier2);

        PricePlan pricePlan = new PricePlan("ygf_zhuanshu", ENERGY_SUPPLIER_NAME, BigDecimal.valueOf(10L), peakTimeMultiplierList);

        BigDecimal price = pricePlan.getPrice(LocalDateTime.parse("2023-01-26T00:00:00"));//好巧不巧，这一天恰好是周四
        System.out.println(price.doubleValue());

        System.out.println(pricePlan.getPrice(LocalDateTime.parse("2023-01-25T00:00:00")));//周三的费率就很正常，没有什么峰值乘数带来的额外收费
        System.out.println(pricePlan.getPrice(LocalDateTime.parse("2023-01-27T00:00:00")));//周五的费率就很正常，没有什么峰值乘数带来的额外收费
    }

    @Test
    public void test2() {
        List<PricePlan.PeakTimeMultiplier> peakTimeMultiplierList = new ArrayList<>();
        PricePlan.PeakTimeMultiplier thursdayPeakTimeMultiplier1 = new PricePlan.PeakTimeMultiplier(DayOfWeek.THURSDAY, BigDecimal.valueOf(2L));
        PricePlan.PeakTimeMultiplier thursdayPeakTimeMultiplier2 = new PricePlan.PeakTimeMultiplier(DayOfWeek.THURSDAY, BigDecimal.valueOf(4L));
        peakTimeMultiplierList.add(thursdayPeakTimeMultiplier2);
        peakTimeMultiplierList.add(thursdayPeakTimeMultiplier1);

        PricePlan pricePlan = new PricePlan("ygf_zhuanshu", ENERGY_SUPPLIER_NAME, BigDecimal.valueOf(10L), peakTimeMultiplierList);

        BigDecimal price = pricePlan.getPrice(LocalDateTime.now());
        System.out.println(price.doubleValue());
    }

}
