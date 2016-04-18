package com.tapstream.sdk.http;


import com.tapstream.sdk.Retry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final URL url;
    private final HttpMethod method;
    private final RequestBody body;

    public HttpRequest(URL url, HttpMethod method, RequestBody body){
        this.url = url;
        this.method = method;
        this.body = body;
    }

    public URL getURL(){
        return url;
    }
    public HttpMethod getMethod(){
        return method;
    }

    public RequestBody getBody(){
        return body;
    }

    public Retry.Retryable<HttpRequest> makeRetryable(Retry.Strategy strategy){
        return new Retry.Retryable<HttpRequest>(this, strategy);
    }

    public static class Builder {
        private HttpMethod method;
        private String scheme;
        private String host;
        private String path;
        private String fragment;
        private Map<String, String> qs = new LinkedHashMap<String, String>();
        private RequestBody body;

        public Builder method(HttpMethod method){
            this.method = method;
            return this;
        }

        public Builder scheme(String scheme){
            this.scheme = scheme;
            return this;
        }

        public Builder host(String host){
            this.host = host;
            return this;
        }

        public Builder path(String path){
            this.path = path;
            return this;
        }

        public Builder fragment(String fragment){
            this.fragment = fragment;
            return this;
        }

        public Builder addQueryParameter(String name, String value){
            this.qs.put(name, value);
            return this;
        }

        public Builder addQueryParameters(Map<String, String> params){
            this.qs.putAll(params);
            return this;
        }

        public Builder postBody(RequestBody body){
            this.body = body;
            return this;
        }

        public HttpRequest build() throws MalformedURLException{
            if (scheme == null)
                throw new NullPointerException("Scheme must not be null");
            if (host == null)
                throw new NullPointerException("Host must not be null");
            if (method == null)
                throw new NullPointerException("Method must not be null");

            // Encode the parameters
            StringBuilder urlBuilder = new StringBuilder(scheme + "://" + host);

            if (path != null){
                if (!path.startsWith("/"))
                    urlBuilder.append("/");
                urlBuilder.append(path);
            }

            if (qs != null){
                urlBuilder.append("?");
                urlBuilder.append(URLEncoding.buildQueryString(qs));
            }

            if (fragment != null){
                urlBuilder.append("#");
                urlBuilder.append(fragment);
            }

            return new HttpRequest(new URL(urlBuilder.toString()), method, body);
        }

    }

}