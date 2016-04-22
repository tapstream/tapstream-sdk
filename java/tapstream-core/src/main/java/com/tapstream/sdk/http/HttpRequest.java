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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequest that = (HttpRequest) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (method != that.method) return false;
        return body != null ? body.equals(that.body) : that.body == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url=" + url +
                ", method=" + method +
                ", body=" + body +
                '}';
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

            if (qs != null && !qs.isEmpty()){
                urlBuilder.append("?");
                urlBuilder.append(URLEncoding.buildQueryString(qs));
            }

            if (fragment != null && !fragment.isEmpty()){
                urlBuilder.append("#");
                urlBuilder.append(fragment);
            }

            return new HttpRequest(new URL(urlBuilder.toString()), method, body);
        }

    }

}