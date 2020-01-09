package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.rxjava.Cons.TAG;

public class MainActivity extends AppCompatActivity {

    private Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void r01(View view) {
        // 起点 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribe( // 订阅 == registerObserver
                // 终点 一个 观察者
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 拆分来写
     *
     * @param view
     */
    public void r02(View view) {
        // TODO 上游 Observable 被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            // ObservableEmitter<Integer> emitter 发射器 发射事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "上游subscribe: 发射事件");//1
                // 发射事件
                e.onNext(1);
                Log.d(TAG, "上游subscribe: 发射完成");//3
            }
        });

        // TODO 下游 Observer 观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游 接收处理 onNext: " + integer);//2
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        // TODO 被观察者(上游)  订阅  观察者（下游）
        observable.subscribe(observer);
    }

    /**
     * 流程整理 1
     *
     * @param view
     */
    public void r04(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 发射
                Log.d(TAG, "上游 subscribe: 开始发射...");
                e.onNext("RxJavaStudy");// todo 2

                e.onComplete(); // 发射完成  // todo 4

                // 上游的最后log才会打印
                Log.d(TAG, "上游 subscribe: 发射完成");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 弹出 加载框 ....
                Log.d(TAG, "上游和下游订阅成功 onSubscribe"); // todo 1
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext: " + s); // todo 3
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                // 隐藏加载框
                Log.d(TAG, "下游接收完成 onComplete"); // todo 5  只有接收完成之后，上游的最后log才会打印

            }
        });
    }
    /**
     * 流程整理2
     * @param view
     */
    public void r05(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 发射
                Log.d(TAG, "上游 subscribe: 开始发射..."); // todo 2
                e.onNext("RxJavaStudy");

//                 e.onComplete(); // 发射完成  // todo 4

//                 上游的最后log才会打印
                 Log.d(TAG, "上游 subscribe: 发射完成");// todo 5

                 e.onError(new IllegalAccessException("error rxJava"));

                // TODO 结论：在 onComplete();/onError 发射完成 之后 再发射事件  下游不再接收上游的事件
                // TODO 结论：已经发射了onComplete();， 再发射onError RxJava会报错，不允许
                e.onNext("a");
                e.onNext("b");
                e.onNext("c");
                // 发一百个事件

                e.onError(new IllegalAccessException("error rxJava")); // 发射错误事件
                e.onComplete(); // 发射完成
                // TODO 结论：先发射onError，再onComplete();，不会报错， 有问题（onComplete不会接收到了）
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 弹出 加载框 ....
                Log.d(TAG, "上游和下游订阅成功 onSubscribe 1"); // todo 1
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext: " + s); // todo 3
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "下游接收 onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                // 隐藏加载框
                Log.d(TAG, "下游接收完成 onComplete"); // todo 5  只有接收完成之后，上游的最后log才会打印
            }
        });
    }

    public void r06(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                MainActivity.this.d = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游接收 onNext: " + integer);

                // 接收上游的一个事件之后，就切断下游，让下游不再接收
//                 d.dispose();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void cut(View view) {
        if (d != null) {
            d.dispose();
        }
    }
}
