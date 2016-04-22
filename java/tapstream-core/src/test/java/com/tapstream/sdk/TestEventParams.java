package com.tapstream.sdk;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;


public class TestEventParams {

    @Test
    public void testSimplePut() throws Exception {
        Event.Params params = new Event.Params();
        params.put("string", "value");
        params.put("int", 5);
        params.put("long", 10L);

        assertThat(params.toMap().size(), is(3));
        assertThat(params.toMap(), allOf(
                hasEntry("string", "value"),
                hasEntry("int", "5"),
                hasEntry("long", "10")
        ));
    }

    @Test
    public void testTruncatedPut() throws Exception {
        Event.Params params = new Event.Params();

        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        for (int x = 0; x < 512; x++){
            keyBuilder.append((char)(x % 127));
            valueBuilder.append((char)(127 - (x % 127)));
        }

        String key = keyBuilder.toString();
        String value = valueBuilder.toString();
        params.put(key, "value");
        params.put("key", value);
        assertThat(params.toMap().size(), is(0));
    }

    @Test
    public void testNullPut() throws Exception{
        Event.Params params = new Event.Params();
        params.put("name", null);
        params.put(null, "value");
        assertThat(params.toMap().size(), is(0));
    }

    @Test
    public void testPutWithoutValueTruncation() throws Exception {
        Event.Params params = new Event.Params();
        StringBuilder valueBuilder = new StringBuilder();

        for (int x = 0; x < 512; x++){
            valueBuilder.append((char)(127 - (x % 127)));
        }

        String value = valueBuilder.toString();

        params.putWithoutTruncation("name", value);
        assertThat(params.toMap().size(), is(1));
        assertThat(params.toMap(), hasEntry("name", value));
    }

}