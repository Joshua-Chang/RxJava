package com.example.rxjava.observer_pattern;
// 观察者标准
public class ObserverImpl implements Observer{

    @Override
    public <T> void changeAction(T observableInfo) {
        System.out.println(observableInfo);
    }
}
