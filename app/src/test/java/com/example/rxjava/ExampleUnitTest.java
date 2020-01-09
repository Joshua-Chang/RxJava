package com.example.rxjava;

import com.example.rxjava.observer_pattern.Observable;
import com.example.rxjava.observer_pattern.ObservableImpl;
import com.example.rxjava.observer_pattern.Observer;
import com.example.rxjava.observer_pattern.ObserverImpl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void ObserverPattern(){
        Observer observer1=new ObserverImpl();
        Observer observer2=new ObserverImpl();
        Observer observer3=new ObserverImpl();
        Observer observer4=new ObserverImpl();
        Observable observable=new ObservableImpl();
        observable.registerObserver(observer1);
        observable.registerObserver(observer2);
        observable.registerObserver(observer3);
        observable.registerObserver(observer4);
        observable.notifyObservers();
    }
}