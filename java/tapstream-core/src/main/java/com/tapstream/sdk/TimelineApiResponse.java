package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONObject;

import java.util.regex.Pattern;


public class TimelineApiResponse implements ApiResponse{

    private static final Pattern legacyEmptyTimelinePattern = Pattern.compile("^\\s*\\[\\s*\\]\\s*$");

    private final HttpResponse httpResponse;
    private final String rawResponse;
    private final boolean isEmpty;

    public TimelineApiResponse(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
        this.rawResponse = httpResponse.getBodyAsString();
        this.isEmpty = rawResponse == null
                || rawResponse.isEmpty()
                || legacyEmptyTimelinePattern.matcher(rawResponse).matches();
    }

    @Override
    public HttpResponse getHttpResponse(){
        return httpResponse;
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
