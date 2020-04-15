package cz.kkovarik.redis.journey;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


/**
 * @author Karel Kovarik
 */
@Slf4j
@Component
public class JourneyService {

    public static final String PREFIX_PLACEHOLDER = "__PREFIX__";
    public static final String SESSION_PLACEHOLDER = "__SESSION_ID__";

    @Value("${journey.file}")
    private String journeyFile;

    @Autowired
    private RedisTemplate redisTemplate;

    public String executeJourney() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("journey");
        log.debug("Start journey");

        // configuration
        String prefix = RandomStringUtils.randomAlphanumeric(10);
        String sessionId = RandomStringUtils.randomAlphanumeric(40);

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(journeyFile))) {
            stream.forEach(it -> {
                String line = it.replace(PREFIX_PLACEHOLDER, prefix);
                line = line.replace(SESSION_PLACEHOLDER, sessionId);

                // just split and do some replacement
                String[] cmdsRaw = line.split("\" ");
                List<String> cmds = Arrays.stream(cmdsRaw)
                        .map(str -> str.replace("\"", ""))
                        .map(String::trim)
                        .collect(Collectors.toList());
                byte[][] args = cmds.stream()
                        .skip(1)
                        .map(ar -> ar.getBytes(StandardCharsets.UTF_8))
                        .toArray(byte[][]::new);
                // use redisTemplate and execute raw command + arguments
                Object ret = redisTemplate.execute((RedisCallback)cb -> cb.execute(cmds.get(0), args));

                if (log.isDebugEnabled()) {
                    log.debug(cmds.get(0));
                    log.debug("Return value: {}", ret);
                }
            });
        } catch (IOException e) {
            log.error("Unable to read:", e);
            throw new RuntimeException(e);
        }

        stopWatch.stop();

        return "" + stopWatch.getTotalTimeMillis() + " ms";
    }
}
