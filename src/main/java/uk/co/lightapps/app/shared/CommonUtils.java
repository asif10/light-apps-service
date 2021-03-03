package uk.co.lightapps.app.shared;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * @author Asif Akhtar
 * 04/02/2021 23:39
 */
public final class CommonUtils {
    public final static int TRADING_DAYS = 5;
    public final static int TRADE_AMOUNT = 6;
    public final static int TRADES_PER_DAY = 3;

    public static long calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long days = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter(isWeekend.negate()).count();
        if (startDate.getMonthValue() == 1) {
            days -= 1;
        }
        return days;
    }

//    public int getTradeAmount(){
//    }


}
