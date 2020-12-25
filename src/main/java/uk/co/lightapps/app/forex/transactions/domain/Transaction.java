package uk.co.lightapps.app.forex.transactions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static uk.co.lightapps.app.forex.transactions.domain.Transaction.TransactionType.*;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Document("Transactions")
@Data
public class Transaction {
    @Id
    private String transactionId;
    private LocalDate date;
    private TransactionType type;
    private double amount;

    public enum TransactionType {
        DEPOSIT, LOSS, OPENING, PROFIT
    }

    public static Transaction deposit(LocalDate date, double amount) {
        return new Transaction(date, DEPOSIT, amount);
    }

    public static Transaction opening(LocalDate date, double amount) {
        return new Transaction(date, OPENING, amount);
    }

    public static Transaction profit(LocalDate date, double amount) {
        return new Transaction(date, PROFIT, amount);
    }

    public static Transaction loss(LocalDate date, double amount) {
        if (amount > 0) {
            return new Transaction(date, DEPOSIT, amount * -1);
        } else {
            return new Transaction(date, DEPOSIT, amount);
        }
    }

    public Transaction(LocalDate date, TransactionType type, double amount) {
        this.date = date;
        this.type = type;
        this.amount = amount;
    }
}
