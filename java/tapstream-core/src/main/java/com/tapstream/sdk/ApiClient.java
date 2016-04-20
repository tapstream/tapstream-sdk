package com.tapstream.sdk;

import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.Reward;

import java.io.Closeable;
import java.util.List;

public interface ApiClient extends Closeable {
	ApiFuture<EventApiResponse> fireEvent(Event e);
	ApiFuture<TimelineApiResponse> lookupTimeline();
	ApiFuture<Offer> getWordOfMouthOffer(final String insertionPoint);

	/**
	 * TODO
	 * @return
	 */
	ApiFuture<List<Reward>> getWordOfMouthRewardList();
}
