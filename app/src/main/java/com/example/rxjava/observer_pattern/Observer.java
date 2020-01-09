package com.example.rxjava.observer_pattern;
// 观察者标准
public interface Observer {
    /**
     * 收到 被观察者 发生改变
     * @param observableInfo
     * @param <T>
     */
    <T>void changeAction(T observableInfo);
}
