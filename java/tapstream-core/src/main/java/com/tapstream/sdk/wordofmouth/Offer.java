package com.tapstream.sdk.wordofmouth;

import org.json.JSONObject;

/**
 * Represents an Offer retrieved from Tapstream's word-of-mouth API.
 */
public class Offer extends DelegatedJSONObject{
    public static Offer fromApiResponse(JSONObject delegate){
        return new Offer(delegate);
    }

    public Offer(JSONObject delegate){
        super(delegate);
    }

    public String prepareMessage(String sessionId){
        return getMessage()
            .replace("OFFER_URL", getOfferUrl())
            .replace("SDK_SESSION_ID", sessionId);

    }

    public String getMessage(){ return getOrDefault("message", ""); }
    public String getOfferButtonText(){ return getOrDefault("offer_button_text", ""); }
    public String getOfferText(){ return getOrDefault("offer_text", ""); }
    public String getOfferTitle(){ return getOrDefault("offer_title", ""); }
    public String getOfferUrl(){ return getOrDefault("offer_url", ""); }
    public String getMarkup(){ return getOrDefault("markup", ""); }

}
