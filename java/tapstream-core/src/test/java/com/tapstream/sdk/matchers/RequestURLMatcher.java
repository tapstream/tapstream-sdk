package com.tapstream.sdk.matchers;

import com.tapstream.sdk.http.HttpRequest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.net.URL;

import static org.mockito.Matchers.argThat;

/**
 * Created by adam on 2016-04-19.
 */


public class RequestURLMatcher extends TypeSafeMatcher<HttpRequest> {
    URL url;
    RequestURLMatcher(URL url){
        this.url = url;
    }

    public static HttpRequest urlEq(URL expected){
        return argThat(new RequestURLMatcher(expected));
    }
    @Override
    protected boolean matchesSafely(HttpRequest other) {
        return other.getURL().equals(url);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("RequestUrlMatches(");
        description.appendText(url.toExternalForm());
        description.appendText(")");

    }
}