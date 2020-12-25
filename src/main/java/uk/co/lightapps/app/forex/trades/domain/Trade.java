package uk.co.lightapps.app.forex.trades.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Asif Akhtar
 * 18/10/2020 22:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Trades")
@Builder
public class Trade {
    @Id
    private String tradeId;
    private LocalDateTime date;
    private Client client;
    private Pair pair;
    private String strategy;
    private TradeType type;
    private double account;
    private double invested;
    private double accountP;
    private double open;
    private double lot;
    private double close;
    private double pips;
    private double fees;
    private String status;
    private double rr;
    private double profit;
    private double profitP;

//    public static Trade opened(Client client, Pair pair, String strategy, TradeType type, double account, double invested, double open, double lot, double rr) {
//        return opened(LocalDateTime.now(), client, pair, strategy, type, account, invested, open, lot, rr);
//    }
//
//    public static Trade opened(LocalDateTime date, Client client, Pair pair, String strategy, TradeType type, double account, double invested, double open, double lot, double rr) {
//        return new Trade(UUID.randomUUID().toString(), date, client, pair, strategy, type, account, invested, 0, open, lot, open, 0, 0, null, rr, 0, 0);
//    }

    public void closeTrade(String status, double close, double pips, double rr) {
        this.status = status;
        this.close = close;
        this.pips = pips;
        this.rr = rr;
        this.profit = close - open;
        this.profitP = profit / open;
        this.accountP = open / account;
        this.invested = profit / account;
    }

    public boolean isActive() {
        return Objects.isNull(status) || status.length() == 0;
    }

    @Override public String toString() {
        return "Trade{" +
                "tradeId='" + tradeId + '\'' +
                ", date=" + date +
                ", client=" + client +
                ", pair=" + pair +
                ", strategy='" + strategy + '\'' +
                ", type=" + type +
                ", account=" + account +
                ", accountP=" + accountP +
                ", open=" + open +
                ", lot=" + lot +
                ", close=" + close +
                ", pips=" + pips +
                ", fees=" + fees +
                ", status='" + status + '\'' +
                ", rr=" + rr +
                ", profit=" + profit +
                ", profitP=" + profitP +
                '}';
    }
}
