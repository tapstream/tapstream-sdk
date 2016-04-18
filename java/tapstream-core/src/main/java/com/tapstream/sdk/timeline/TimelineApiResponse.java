package com.tapstream.sdk.timeline;

import org.json.JSONObject;

import java.util.regex.Pattern;


public class TimelineApiResponse {

    private static final Pattern legacyEmptyTimelinePattern = Pattern.compile("^\\s*\\[\\s*\\]\\s*$");

    private final String rawResponse;
    private final boolean isEmpty;

    public TimelineApiResponse(String rawResponse){
        this.rawResponse = rawResponse;
        this.isEmpty = legacyEmptyTimelinePattern.matcher(rawResponse).matches();
    }

    /**
     * @return true if the timeline response is empty
     */
    public boolean isEmpty(){
        return isEmpty;
    }

    /**
     * Get the raw response body returned by the Tapstream API.
     * @return the raw response body.
     */
    public String getRawResponse(){
        return rawResponse;
    }

    /**
     * Build the JSON root object for this API response.
     *
     * @return the root JSON object or null if the response was empty.
     */
    public JSONObject parse(){
        if (isEmpty){
            return null;
        } else {
            return new JSONObject(rawResponse);
        }
    }


}
