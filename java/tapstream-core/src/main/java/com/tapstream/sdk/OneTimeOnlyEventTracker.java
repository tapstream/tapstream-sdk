package com.tapstream.sdk;

import java.util.HashSet;
import java.util.Set;

public class OneTimeOnlyEventTracker {

    private final Platform platform;
    private final Set<String> eventsAlreadyFired;
    private final Set<String> eventsInProgress;

    public OneTimeOnlyEventTracker(Platform platform){
        this.platform = platform;
        this.eventsAlreadyFired = platform.loadFiredEvents();
        this.eventsInProgress = new HashSet<String>();
    }

    synchronized public boolean hasBeenAlreadySent(Event event){
        if (!event.isOneTimeOnly())
            return false;
        return eventsAlreadyFired.contains(event) || eventsInProgress.contains(event);
    }

    synchronized public void sent(Event event){
        if (!event.isOneTimeOnly())
            return;
        eventsInProgress.remove(event.getName());
        eventsAlreadyFired.add(event.getName());
        platform.saveFiredEvents(eventsAlreadyFired);
    }

    synchronized public void failed(Event event){
        sent(event);
    }

    synchronized public void inProgress(Event event){
        if (!event.isOneTimeOnly())
            return;
        eventsInProgress.add(event.getName());
    }

}
