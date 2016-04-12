package com.tapstream.sdk;

public class AdvertisingID {

    private final String id;
    private final boolean limitAdTracking;

    public AdvertisingID(String id, boolean limitAdTracking) {
        this.id = id;
        this.limitAdTracking = limitAdTracking;
    }

    public String getId() {
        return id;
    }

    public boolean isLimitAdTracking() {
        return limitAdTracking;
    }

    public boolean isValid(){
        return id != null && id.length() > 0;
    }
}
