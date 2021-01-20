package uk.co.lightapps.app.forex.stats.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lightapps.app.forex.stats.domain.ForexStats;
import uk.co.lightapps.app.forex.stats.service.ForexStatsService;

/**
 * @author Asif Akhtar
 * 20/01/2021 23:20
 */
@RestController
@RequestMapping("/forex/stats")
@RequiredArgsConstructor
@Slf4j
public class ForexStatsController {
    private final ForexStatsService service;

    @GetMapping(value = "/all")
    public ForexStats stats() {
        return service.calculateStats();
    }
}
