package com.tapstream.sdk;

import com.tapstream.sdk.wordofmouth.WordOfMouth;

public interface AndroidApiClient extends ApiClient {
    WordOfMouth getWordOfMouth();
}
