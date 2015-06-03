package com.tapstream.sdk;

/**
* Date: 15-04-30
* Time: 10:31 AM
*/
public class Maybe<T> {
    final T it;

    public static <T> Maybe<T> nope(){return new Maybe<T>(); }
    public static <T> Maybe<T> yup(T it){return new Maybe<T>(it); }

    Maybe() {
        it = null;
    }
    Maybe(T it){
        this.it = it;
    }
    public T get(){
        return it;
    }
    public boolean isPresent(){
        return it != null;
    }
}
