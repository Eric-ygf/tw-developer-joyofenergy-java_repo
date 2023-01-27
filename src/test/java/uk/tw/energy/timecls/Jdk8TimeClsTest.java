package uk.tw.energy.timecls;

import org.junit.jupiter.api.Test;

import java.time.*;

public class Jdk8TimeClsTest {
    /**
     * build.gradle中，记得把test task打开....
     */

    /**使用LocalDateTime 算 Duration*/
    @Test
    public void test1() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 27, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 29, 0, 0, 0);
        long dayCount = Duration.between(start, end).getSeconds() / (24 * 3600);
        System.out.println(dayCount + "天（using LocalDateTime）");
    }

    /**使用Instant 算 Duration*/
    @Test
    public void test2() {
        Instant start = Instant.parse("1969-09-26T03:06:17.000Z");//还是精确的，多给一个毫秒，Duration就不够一天了
        //P.S. 结尾的Z不能省略
        Instant end = Instant.parse("1969-09-28T03:06:17Z");
        long dayCount = Duration.between(start, end).getSeconds() / (24 * 3600);
        System.out.println(dayCount + "天(using Instant)");
    }

    /** Instance -> LocalDatetime -> Dayofweek */
    @Test
    public void test3() {
        Instant instant = Instant.parse("2023-01-27T03:06:17Z");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        System.out.println(localDateTime + " is " + dayOfWeek);
    }

    /**  LocalDatetime -> Instance */
    @Test
    public void test4() {
        LocalDateTime localDateTime = LocalDateTime.of(2023,1,27,3,4,5);
        Instant instantWithNegativeOffset = localDateTime.toInstant(ZoneOffset.ofTotalSeconds(-1));
        Instant instantWithPositiveOffset = localDateTime.toInstant(ZoneOffset.ofTotalSeconds(1));
        System.out.println("ori instant： " + localDateTime.toInstant(ZoneOffset.ofTotalSeconds(0)));
        System.out.println("instant With Negative Offset： " + instantWithNegativeOffset);
        System.out.println("instant With Positive Offset： " + instantWithPositiveOffset);
    }

}
