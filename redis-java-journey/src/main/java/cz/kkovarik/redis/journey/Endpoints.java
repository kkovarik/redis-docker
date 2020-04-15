package cz.kkovarik.redis.journey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Testing endpoints.
 *
 * @author Karel Kovarik
 */
@RestController
public class Endpoints {

    @Autowired
    private JourneyService journeyService;

    @GetMapping("/journey")
    String journey() {
        // just dummy endpoint to execute journey
        return journeyService.executeJourney();
    }
}
