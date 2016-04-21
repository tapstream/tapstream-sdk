package com.tapstream.sdk.wordofmouth;

import com.tapstream.sdk.ApiResponse;
import com.tapstream.sdk.Event;
import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 2016-04-20.
 */
public class RewardApiResponse implements ApiResponse{
    private final HttpResponse response;
    private final List<Reward> rewards;

    public RewardApiResponse(HttpResponse response, List<Reward> rewards) {
        this.response = response;
        this.rewards = rewards;
    }

    public List<Reward> getRewards(){
        return rewards;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return response;
    }
}
