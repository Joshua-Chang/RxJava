package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.example.rxjava.Cons.TAG;

/**
 * TODO 异常处理操作符，在RxJava使用的时候 的异常处理，提供操作符
 */
public class MainActivity7 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 中断异常 onError返回修改过的值
     * onErrorReturn异常操作符：1.能够接收e.onError，  2.如果接收到异常，会中断上游后续发射的所有事件
     * error
     * ！！！RxJava中是不标准的异常，抛出时要发射该异常onError（）
     *
     * @param view
     */
    public void r01(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的异常，抛出时要发射该异常onError（）
//                         throw new IllegalAccessError("我要报错了");

                        e.onError(new IllegalAccessError("我要报错了")); // 发射异常事件
                    }
                    e.onNext(i);
                }
//                e.onComplete();
            }
        })
//        不处理error不会onComplete，处理过就能正常onComplete

                .onErrorReturn(new Function<Throwable, Integer>() {//处理过就能正常onComplete
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        return 400;
                    }
                })

                .subscribe(new Observer<Integer>() { // 完整版 下游 观察者
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    // 如果使用了 异常操作符 onNext: 400
                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer); // 400
                    }

                    // 如果不使用 异常操作符 onError
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * onErrorResumeNext 异常操作符：1.能够接收e.onError，
     * onErrorReturn可以返回标识400    对比   onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
     * error
     * 中断异常 onError返回修改过的新事件
     *
     * @param view
     */
    public void r02(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的异常，抛出时要发射该异常onError（）

                        e.onError(new IllegalAccessError("我要报错了")); // 发射异常事件
                    }
                    e.onNext(i);
                }
//                e.onComplete();
            }
        })
//        不处理error不会onComplete，处理过就能正常onComplete
                // onErrorResumeNext 返回的是 被观察者，所以再多次发射给 下游 给 观察者接收

                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
//                        return Observable.just(400);

                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                e.onNext(6);
                                e.onNext(7);
                                e.onNext(8);
                            }
                        });
                    }
                })

                .subscribe(new Observer<Integer>() { // 完整版 下游 观察者
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    // 如果使用了 异常操作符 onNext: 400
                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer); // 400
                    }

                    // 如果不使用 异常操作符 onError
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * exception
     * onExceptionResumeNext 操作符，能在发生异常的时候，扭转乾坤，（这种错误一定是可以接受的，才这样使用）
     * 慎用：自己去考虑，是否该使用
     * @param view
     */
    public void r04(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                       /* throw new 其他Exception("错了");
                        throw new IllegalAccessException("错了");*/
                        throw new Exception("错了");
                        // e.onError(new IllegalAccessException("错了")); // 异常事件
                    } else {
                        e.onNext(i);
                    }

                }
                e.onComplete(); // 一定要最后执行

                /**
                 * e.onComplete();
                 * e.onError
                 * 会报错
                 */
            }
        })

                // 在上游和下游中间 增加 异常操作符

                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        observer.onNext(400);
                    }
                })

                // 下游
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * retry 重试操作符 异常处理操作符中
     *
     * @param view
     */
    public void r05(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的异常，抛出时要发射该异常onError（）

                        e.onError(new IllegalAccessError("我要报错了")); // 发射异常事件
                    } else {
                        e.onNext(i);
                    }
                }
//                e.onComplete();
            }
        })
//        不处理error不会onComplete，处理过就能正常onComplete


                // todo 演示一
                /*.retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d(TAG, "retry: " + throwable.getMessage());
                        // return false; // 代表不去重试
                        return true; // 一直重试，不停的重试
                    }
                })*/

                // todo 演示二 重试次数
                /*.retry(3, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d(TAG, "retry: " + throwable.getMessage());
                        return true;
                    }
                })*/

                // todo 演示三 打印重试了多少次，计数     Throwable  +  count
                .retry(new BiPredicate<Integer, Throwable>() {
                    @Override
                    public boolean test(Integer integer, Throwable throwable) throws Exception {
                        Thread.sleep(2000);
                        Log.d(TAG, "retry: 已经重试了:" + integer + "次  e：" + throwable.getMessage());
                        return true; // 重试
                    }
                })

                .subscribe(new Observer<Integer>() { // 完整版 下游 观察者
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    // 如果使用了 异常操作符 onNext: 400
                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer); // 400
                    }

                    // 如果不使用 异常操作符 onError
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }


    public void r06(View view) {


    }

}
