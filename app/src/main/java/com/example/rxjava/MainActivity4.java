package com.example.rxjava;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

import static com.example.rxjava.Cons.TAG;
/**
 * TODO 过滤操作符
 */
public class MainActivity4 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * filter 过滤
     * 需求：过滤掉 哪些不合格的奶粉，输出哪些合格的奶粉
     *
     * @param view
     */
    public void r01(View view) {
        Observable.just("A", "B", "C").filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                // return true; // 不去过滤，默认全部都会打印
                // return false; // 如果是false 就全部都不会打印
                if ("A".equalsIgnoreCase(s)) {
                    return false;
                } else {
                    return true;
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
            }
        });

    }

    /**
     * take过滤操作符
     *
     * @param view
     */
    public void r02(View view) {
        Observable.interval(2, TimeUnit.SECONDS)
                // 定时器 运行   只有再定时器运行基础上 加入take过滤操作符，才有take过滤操作符的价值

                // 上游
                // 增加过滤操作符，停止定时器
                .take(8) // 执行次数达到8 停止下来
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "accept: " + aLong);
                    }
                });
    }

    /**
     * distinct过滤重复事件
     *
     * @param view
     */
    public void r04(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(4);
                e.onComplete();
            }
        });
        Observable.just(1, 1, 2, 2)


                .distinct() // 过滤重复 发射的事件

                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer); // 事件不重复
                    }
                });
    }

    /**
     * elementAt 指定过滤的内容
     *
     * @param view
     */
    public void r05(View view) {
        Observable.just(1, 2, 3, 4, 5)
                // 过滤操作符
//                .elementAt(1, 0) // 指定下标输出 事件
                .elementAt(10, 0) // 指定下标输出 事件，找不到该下标返回默认值
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
    }


    public void r06(View view) {


    }

}
