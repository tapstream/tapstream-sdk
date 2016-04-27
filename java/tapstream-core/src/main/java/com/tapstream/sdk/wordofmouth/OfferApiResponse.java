package com.tapstream.sdk.wordofmouth;

import com.tapstream.sdk.ApiResponse;
import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONObject;

/**
 * Created by adam on 2016-04-20.
 */
public class OfferApiResponse implements ApiResponse {
    private HttpResponse response;
    private Offer offer;

    public OfferApiResponse(HttpResponse response, Offer offer){
        this.response = response;
        this.offer = offer;
    }

    /**
     * @return The Offer returned by the server.
     */
    public Offer getOffer(){
        return offer;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return response;
    }
}
