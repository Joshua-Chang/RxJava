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
import io.reactivex.functions.Function;
import static com.example.rxjava.Cons.TAG;

public class MainActivity11 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onErrorReturn
     *     onError(Error  Exception）
     *
     * todo 通用点 onErrorReturn异常操作符：能够接收e.onError(Error/Exception) / throw new xxxException，
     * todo  [onErrorReturn] 此异常操作符特点：能够打印异常详情，会给下游一个标记
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r01(View view) {
        // 上游 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了"); // 抛出异常，可以接收

                        // RxJava标准的
                        // e.onError(new IllegalAccessError("我要报错了")); // 发射此事件，可以接收
                        // e.onError(new IllegalAccessException("我要报废了")); // 发射此事件，可以接收

                        // 错误
                        throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        })

                // 在上游 和 下游之间 添加异常操作符
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        // 处理，纪录，异常，通知给下一层
                        Log.d(TAG, "onErrorReturn: " + throwable.getMessage());
                        return 400; // 400代表有错误，给下一层，目前 下游 观察者    -----> 下游 onNext方法
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
    // onErrorResumeNext 异常操作符：能够接收e.onError(xxx)/throw new xxxException， 无法处理throw new xxxError
    // onErrorReturn可以返回标识400    对比   onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
    // 特点：不能打印异常详情，可以多少发射事件
    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onErrorResumeNext
     *     onError(Error  Exception）
     *
     * todo 通用点 onErrorReturn异常操作符：能够接收e.onError(Error/Exception) / throw new xxxException，
     * todo  [onErrorResumeNext] 此异常操作符特点：onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r02(View view) {
// 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了");

                        // RxJava标准的
                        e.onError(new IllegalAccessError("我要报错了"));  // 发射此事件，能够接收
                        // e.onError(new IllegalAccessError("我要报错了"));  // 发射此事件，能够接收

                        // 程序奔溃
                        // throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        })

                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {

                        // onErrorResumeNext 返回的是 被观察者，所以再多次发射给 下游 给 观察者接收
                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onNext(400);
                                e.onComplete();
                            }
                        });
                    }
                })

                .subscribe(new Observer<Integer>() { // 下游
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
                })    ;

    }

    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onExceptionResumeNext
     *      onError(Exception）
     *
     * todo 通用点 onExceptionResumeNext异常操作符：只能接收e.onError(Exception) / throw new xxxException，
     * todo  [onExceptionResumeNext] 此异常操作符特点： 专门处理Exception，不能处理Error
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r04(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了");

                        // RxJava标准的

                        // 异常操作符 无法无法处理  直接就到了  下游的onError了
                        e.onError(new IllegalAccessError("我要报错了")); // 发射此事件，无法处理到此操作，直接到下游的onError了

                        // e.onError(new IllegalAccessException("我要报错了")); // 发射此事件，可以处理到此操作，会onNext(404) -->

                        // throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    } else {
                        e.onNext(i);
                    }

                }
                e.onComplete(); // 一定要最后执行
            }
        })

                // 在上游和下游中间 增加 异常操作符
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        observer.onNext(404); // 可以让程序 不崩溃的
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        observer.onNext(4);
                        // ...
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


    public void r05(View view) {

    }


    public void r06(View view) {


    }

}
