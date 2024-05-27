package dev.mmieckowski.blockchain.core.blockchain.utilities;

import java.time.Clock;
import java.time.Duration;

public class Timer {
    private final Clock clock;
    private long lastTimeMillis;

    public Timer() {
        this(Clock.systemDefaultZone());
    }

    public Timer(Clock clock) {
        this.clock = clock;
        lastTimeMillis = clock.millis();
    }

    public Duration restart() {
        long currTime = clock.millis();
        Duration delta = Duration.ofMillis(currTime - lastTimeMillis);
        lastTimeMillis = currTime;
        return delta;
    }
}