package com.app.kh.camnextgen.shared.ratelimit;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {
    private boolean enabled = true;
    private long capacity = 10;
    private long refillTokens = 10;
    private Duration refillDuration = Duration.ofMinutes(1);
}
