package enterprise.test.common;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ZoneDateTimeClock implements CustomClock<ZonedDateTime> {

    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
