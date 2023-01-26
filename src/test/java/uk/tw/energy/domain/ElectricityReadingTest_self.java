package uk.tw.energy.domain;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.util.List;

public class ElectricityReadingTest_self {

    @Test
    public void test01() {
        List<ElectricityReading> list = new ElectricityReadingsGenerator().generate(3);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
        System.out.println(list.get(0).getTime().toString());

        Double average = list.stream()
                .mapToDouble(reading -> reading.getReading().doubleValue())
                .average().getAsDouble();
        System.out.println(average);//这么干也行，但是double的话，可能有精度误差
    }

}
