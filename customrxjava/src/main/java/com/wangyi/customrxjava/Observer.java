package com.wangyi.customrxjava;

public interface Observer<T> {
    public void onSubscribe();

    public void onNext(T t);

    public void onError(Throwable e);

    public void onComplete();

}
