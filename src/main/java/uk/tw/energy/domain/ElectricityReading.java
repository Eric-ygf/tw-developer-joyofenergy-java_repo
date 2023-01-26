package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class ElectricityReading {

    private Instant time;
    /** 功率！！！*/
    private BigDecimal reading; // kW  耗电量似乎应该是kwh，可是如果是kwh的话，计算平均耗能就没法算了，然而，电表能显示功率么？智能电表好像是可以的...

    public ElectricityReading() { }

    public ElectricityReading(Instant time, BigDecimal reading) {
        this.time = time;
        this.reading = reading;
    }

    public BigDecimal getReading() {
        return reading;
    }

    public Instant getTime() {
        return time;
    }
}
