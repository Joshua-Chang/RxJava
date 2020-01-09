package com.example.rxjava.observer_pattern;

import java.util.ArrayList;
import java.util.List;

public class ObservableImpl implements Observable {
    private List<Observer> observerList = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observerList) {
            // 在被观察者实现类中，通知所有注册好的观察者
            observer.changeAction("被观察者 发生了改变...");
        }
    }
}
