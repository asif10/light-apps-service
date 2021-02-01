package uk.co.lightapps.app.live;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Pair;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.domain.TradeType;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.transactions.services.TransactionService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Double.parseDouble;
import static java.nio.charset.StandardCharsets.UTF_8;
import static uk.co.lightapps.app.forex.trades.domain.Client.IQ;
import static uk.co.lightapps.app.forex.transactions.domain.Transaction.deposit;
import static uk.co.lightapps.app.forex.transactions.domain.Transaction.opening;

/**
 * @author Asif Akhtar
 * 24/11/2020 16:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("live")
//@Ignore
public class LiveContentTest {
    @Autowired
    private TradeService service;
    @Autowired
    private PositionsService positionsService;
    @Autowired
    private TransactionService transactionService;

    @Test
    @Ignore
    public void clearAll() {
        service.deleteAll();
    }

    @Test
    public void addTrades() {
    }

//    @Test
//    public void exportTrades() throws Exception {
//        service.deleteAll();
//        String file = "src/test/resources/trades251120.txt";
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
//        String currentLine;
//        while ((currentLine = reader.readLine()) != null) {
//            String[] split = currentLine.split("\t");
//            Trade trade = Trade.opened(formatDate(split[0], split[1]), IQ, Pair.valueOf(split[2]), split[4], TradeType.valueOf(split[3]), db(split[5]), db(split[8]), db(split[7]), 0.0);
//            trade.setFees(db(split[15]));
//            System.out.println(trade);
//            if (split[16].trim().length() == 0) {
//                System.out.println("OPEN");
//            } else {
//                System.out.println("CLOSED");
//                service.closeTrade(split[16], trade, db(split[9]), Integer.parseInt(split[12]), db(split[14]));
//            }
//        }
//    }

    @Test
    public void newTrades() throws Exception {
        service.deleteAll();
        String file = "src/test/resources/trades2021.txt";

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            if (currentLine.trim().length() == 0) {
                continue;
            }
            String[] split = currentLine.split("\t");
//            Thu 24 Dec	17:41	USDCHF	LONG	301	600.00	1.00%	 0.01 	6.00	5.67	-0.33	-5.5%	-0.1%	0	-1	2.2	-	-

            Trade trade = Trade.builder()
                    .client(IQ)
                    .date(formatDate(split[0], split[1]))
                    .pair(Pair.valueOf(split[2]))
                    .type(TradeType.valueOf(split[3]))
                    .strategy(split[4])
                    .account(db(split[5]))
                    .lot(db(split[7]))
                    .open(db(split[8]))
                    .close(db(split[9]))
                    .pips(db(split[13]))
                    .rr(db(split[15]))
                    .fees(db(split[16]))
                    .status(split[17])
                    .build();

            System.out.println(trade);
            if (trade.getStatus().equals("-")) {
                System.out.println("OPEN");
                service.closeTrade(trade.getStatus(), trade);
            } else {
                System.out.println("CLOSED");
                service.closeTrade(trade.getStatus(), trade);
            }
        }
    }

//    @Test
//    public void exportDailyPositions() throws Exception {
//        positionsService.deleteAllDaily();
//        String file = "src/test/resources/dailyPositions131220.txt";
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
//        String currentLine;
//        while ((currentLine = reader.readLine()) != null) {
//            String[] split = currentLine.split("\t");
//            DailyPosition position = DailyPosition.logged(formatDate(split[0]), db(split[1]), db(split[2]), db(split[3]), db(split[4]), db(split[5]), db(split[6]), dbP(split[7]), db(split[8]), dbP(split[9]), 0);
//            System.out.println(position);
////            trade.setFees(db(split[15]));
////            System.out.println(trade);
////            if (split[16].trim().length() == 0) {
////                System.out.println("OPEN");
////            } else {
////                System.out.println("CLOSED");
//            positionsService.add(position);
////            }
//        }
//    }

    @Test
    public void addTransactions() throws Exception {
        transactionService.deleteAll();
        transactionService.save(deposit(LocalDate.of(2020, 8, 1), 200));
        transactionService.save(deposit(LocalDate.of(2020, 10, 15), 100));
        transactionService.save(deposit(LocalDate.of(2020, 10, 23), 300 - 232 - 0.7));
        transactionService.save(deposit(LocalDate.of(2020, 12, 23), 538.51));

        transactionService.save(opening(LocalDate.of(2021, 1, 1), 600.7));
    }

    public static double db(String value) {
        if (value.equals("-")) {
            return 0;
        } else {
            return parseDouble(value);
        }
    }

    public static double dbP(String value) {
        return db(value.substring(0, value.length() - 1)) / 100;
    }

    public static LocalDateTime formatDate(String date, String time) {
        String tradeDate = date + " 2021 " + time;
        return LocalDateTime.parse(tradeDate, DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm"));
    }

    public static LocalDate formatDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
