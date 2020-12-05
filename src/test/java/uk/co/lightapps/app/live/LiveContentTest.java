package uk.co.lightapps.app.live;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import uk.co.lightapps.app.forex.Pair;
import uk.co.lightapps.app.forex.Trade;
import uk.co.lightapps.app.forex.TradeService;
import uk.co.lightapps.app.forex.TradeType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.lang.Double.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.forex.Client.IQ;
import static uk.co.lightapps.app.forex.Pair.EURUSD;
import static uk.co.lightapps.app.forex.TradeType.LONG;
import static uk.co.lightapps.app.trades.TradesTest.rounded;

/**
 * @author Asif Akhtar
 * 24/11/2020 16:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LiveContentTest {
    @Autowired
    private TradeService service;

    @Test
    @Ignore
    public void clearAll() {
        service.deleteAll();
    }

    @Test
    public void addTrades() {
    }

    @Test
    public void exportTrade() throws Exception {
        service.deleteAll();
        String file = "src/test/resources/trades251120.txt";

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            String[] split = currentLine.split("\t");
            Trade trade = Trade.opened(formatDate(split[0], split[1]), IQ, Pair.valueOf(split[2]), split[4], TradeType.valueOf(split[3]), db(split[5]), db(split[8]), db(split[7]), 0.0);
            trade.setFees(db(split[15]));
            System.out.println(trade);
            if(split[16].trim().length()==0){
                System.out.println("OPEN");
            }else{
                System.out.println("CLOSED");
                service.closeTrade(split[16], trade, db(split[9]), Integer.parseInt(split[12]), db(split[14]));
            }
        }
    }

    double db(String value) {
        if (value.equals("-")) {
            return 0;
        } else {
            return parseDouble(value);
        }
    }

    private LocalDateTime formatDate(String date, String time) {
        String tradeDate = date + " 2020 " + time;
        return LocalDateTime.parse(tradeDate, DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm"));
    }
}
