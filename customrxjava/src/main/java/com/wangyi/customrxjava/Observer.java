package com.wangyi.customrxjava;

public interface Observer<T> {//下游
    public void onSubscribe();

    public void onNext(T t);

    public void onError(Throwable e);

    public void onComplete();
}
