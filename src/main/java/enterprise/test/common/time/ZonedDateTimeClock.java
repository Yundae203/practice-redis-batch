package enterprise.test.common.time;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ZonedDateTimeClock implements CustomClock<ZonedDateTime> {

    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
