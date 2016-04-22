package com.tapstream.sdk;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class TestRetry {

    @Test
    public void testRetryable(){

        Retry.Strategy strategy = new Retry.Strategy(){

            @Override
            public int getDelayMs(int attempt) {
                return attempt * 5;
            }

            @Override
            public boolean shouldRetry(int attempt) {
                return attempt < 3;
            }
        };

        Object inner = new Object();
        Retry.Retryable<Object> retryable = new Retry.Retryable<Object>(inner, strategy);
        assertThat(retryable.get(), is(inner));

        // Initial attempt
        assertThat(retryable.shouldRetry(), is(true));
        assertThat(retryable.getAttempt(), is(1));
        assertThat(retryable.getDelayMs(), is(5));

        // Second attempt
        assertThat(retryable.incrementAttempt(), is(2));
        assertThat(retryable.getAttempt(), is(2));
        assertThat(retryable.shouldRetry(), is(true));
        assertThat(retryable.getDelayMs(), is(10));

        // third attempt should not be allowed
        assertThat(retryable.incrementAttempt(), is(3));
        assertThat(retryable.getAttempt(), is(3));
        assertThat(retryable.shouldRetry(), is(false));
        assertThat(retryable.getDelayMs(), is(15));
    }

    @Test
    public void testExponentialStrategy() throws Exception {
        Retry.Strategy strategy = new Retry.Exponential(1000, 2, 3);
        assertThat(strategy.getDelayMs(1), is(0));
        assertThat(strategy.getDelayMs(2), is(1000));
        assertThat(strategy.getDelayMs(3), is(2000));
        assertThat(strategy.shouldRetry(1), is(true));
        assertThat(strategy.shouldRetry(2), is(true));
        assertThat(strategy.shouldRetry(3), is(false));
    }

    @Test
    public void testFixedDelayStrategy() throws Exception {
        Retry.Strategy strategy = new Retry.FixedDelay(1000, 3);
        assertThat(strategy.getDelayMs(1), is(1000));
        assertThat(strategy.getDelayMs(2), is(1000));
        assertThat(strategy.getDelayMs(3), is(1000));
        assertThat(strategy.shouldRetry(1), is(true));
        assertThat(strategy.shouldRetry(2), is(true));
        assertThat(strategy.shouldRetry(3), is(false));
    }

}
