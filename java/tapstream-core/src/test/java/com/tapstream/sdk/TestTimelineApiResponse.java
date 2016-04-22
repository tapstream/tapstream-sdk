package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class TestTimelineApiResponse {


    String validBody = "{\"hits\": [\"hitsGoHere\"], \"events\": [\"eventsGoHere\"]}";

    TimelineApiResponse legacyEmptyResponse = new TimelineApiResponse(new HttpResponse(200, "OK", "[]".getBytes()));
    TimelineApiResponse emptyStringResponse = new TimelineApiResponse(new HttpResponse(200, "OK", "".getBytes()));
    TimelineApiResponse nullBodyResponse = new TimelineApiResponse(new HttpResponse(200, "OK", null));
    TimelineApiResponse validResponse = new TimelineApiResponse(new HttpResponse(200, "OK", validBody.getBytes()));

    @Test
    public void testEmptyTimeline() {
        assertThat(legacyEmptyResponse.isEmpty(), is(true));
        assertThat(emptyStringResponse.isEmpty(), is(true));
        assertThat(nullBodyResponse.isEmpty(), is(true));
        assertThat(validResponse.isEmpty(), is(false));
    }

    @Test
    public void testTimelineParsing() {
        assertThat(legacyEmptyResponse.parse(), is(nullValue()));
        assertThat(emptyStringResponse.parse(), is(nullValue()));
        assertThat(nullBodyResponse.parse(), is(nullValue()));
        JSONObject json = validResponse.parse();
        assertThat(json, is(not(nullValue())));
        assertThat((Collection<String>)json.keySet(), containsInAnyOrder("hits", "events"));
    }

}
