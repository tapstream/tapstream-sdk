package com.tapstream.sdk;

import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.OfferApiResponse;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.RewardApiResponse;

import java.io.Closeable;
import java.util.List;

public interface ApiClient extends Closeable {
	ApiFuture<EventApiResponse> fireEvent(Event e);
	ApiFuture<TimelineApiResponse> lookupTimeline();
	ApiFuture<OfferApiResponse> getWordOfMouthOffer(final String insertionPoint);

	/**
	 * TODO
	 * @return
	 */
	ApiFuture<RewardApiResponse> getWordOfMouthRewardList();
}
