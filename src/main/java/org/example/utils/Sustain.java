package org.example.utils;

import java.time.Duration;

/**
 * Keep logic running
 *
 * @author 唐家林 on 2021-01-27.
 */
public class Sustain {
    /**
     * Keep the specified logic running for a given number of times, then stop.
     *
     * @param count    number of times to run
     * @param runnable logic to execute
     */
    public static void run(long count, Runnable runnable) {
        for (int i = 0; i < count; i++) {
            runnable.run();
        }
    }

    /**
     * Keep the specified logic running for a given duration, then stop.
     *
     * @param duration duration to run
     * @param runnable logic to execute
     */
    public static void run(Duration duration, Runnable runnable) {
        long timeMillis = System.currentTimeMillis() + duration.toMillis();
        while (System.currentTimeMillis() < timeMillis) {
            runnable.run();
        }
    }
}
