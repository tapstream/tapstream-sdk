package com.tapstream.sdk;

import com.tapstream.sdk.wordofmouth.WordOfMouth;

public interface AndroidApiClient extends ApiClient {
    /**
     * @return A WordOfMouth object used for working with Tapstream's feature of the same name.
     * @see WordOfMouth
     */
    WordOfMouth getWordOfMouth();
}
