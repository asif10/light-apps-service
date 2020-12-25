package uk.co.lightapps.app.forex.transactions.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.transactions.domain.Transaction;
import uk.co.lightapps.app.forex.transactions.repository.TransactionRepository;

import static uk.co.lightapps.app.forex.transactions.domain.Transaction.TransactionType.*;

/**
 * @author Asif Akhtar
 * 21/12/2020 21:10
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public double getBalance() {
        return repository.findAll().stream().filter(e -> e.getType() == OPENING).mapToDouble(Transaction::getAmount).sum();
    }

    public double getDepositBalance() {
        return repository.findAll().stream().filter(e -> e.getType() == DEPOSIT).mapToDouble(Transaction::getAmount).sum();
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void save(Transaction tr) {
        repository.save(tr);
    }
}
