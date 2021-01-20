package uk.co.lightapps.app.forex.history.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lightapps.app.forex.history.domain.Snapshot;
import uk.co.lightapps.app.forex.history.service.SnapshotService;

/**
 * @author Asif Akhtar
 * 18/01/2021 22:27
 */
@RestController
@RequestMapping("/snapshots")
@RequiredArgsConstructor
@Slf4j
public class SnapshotController {
    private final SnapshotService service;

    @GetMapping(value = "/last-week")
    public Snapshot lastWeek() {
        return service.calculateLastWeek();
    }
}
