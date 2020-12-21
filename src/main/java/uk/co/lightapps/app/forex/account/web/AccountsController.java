package uk.co.lightapps.app.forex.account.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.trades.domain.Trade;

import java.util.Collections;
import java.util.List;

/**
 * @author Asif Akhtar
 * 10/12/2020 18:29
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {
    private final AccountService accountService;

    @GetMapping(value = "/account")
    public Account getAccountInfo() {
        return accountService.getAccountInfo();
    }
}
