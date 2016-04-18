package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class TestHttpApiClient {

    Platform platform;
    Config config;
    HttpClient httpClient;
    HttpApiClient apiClient;
    ScheduledExecutorService executor;

    @Before
    public void setup() throws Exception{
        platform = mock(Platform.class);
        httpClient = mock(HttpClient.class);
        config = new Config("accountName", "secret");
        executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        apiClient = new HttpApiClient(platform, config, httpClient, executor);
        apiClient.start();
    }

    @After
    public void teardown() throws Exception {
        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Test
    public void testFireEvent(){
        Event event = new Event("eventName", false);
        apiClient.fireEvent(event);

    }



}
