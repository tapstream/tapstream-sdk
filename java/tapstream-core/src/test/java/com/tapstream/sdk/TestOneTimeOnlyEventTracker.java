package com.tapstream.sdk;


import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.*;

public class TestOneTimeOnlyEventTracker {

    Platform platform;

    @Before
    public void setup() throws Exception {
        platform = mock(Platform.class);
    }

    @Test
    public void testHasBeenAlreadySent() throws Exception {
        Set<String> alreadyFired = new HashSet<String>();
        alreadyFired.add("persisted1");
        alreadyFired.add("persisted2");
        when(platform.loadFiredEvents()).thenReturn(alreadyFired);

        OneTimeOnlyEventTracker tracker = new OneTimeOnlyEventTracker(platform);
        verify(platform).loadFiredEvents();

        // Check that the persisted events return as having already been fired
        assertThat(tracker.hasBeenAlreadySent(new Event("persisted1", true)), is(true));
        assertThat(tracker.hasBeenAlreadySent(new Event("persisted2", true)), is(true));

        Event freshEvent = new Event("fresh", true);

        // First invocation should be false
        assertThat(tracker.hasBeenAlreadySent(freshEvent), is(false));

        tracker.sent(freshEvent);

        // Second invocation after sending should be true
        assertThat(tracker.hasBeenAlreadySent(freshEvent), is(true));

        assertThat(alreadyFired, containsInAnyOrder("persisted1", "persisted2", "fresh"));
        verify(platform).saveFiredEvents(alreadyFired);
    }
}
