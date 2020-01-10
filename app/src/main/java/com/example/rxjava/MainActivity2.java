package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.rxjava.Cons.TAG;
/**
 * TODO 创建型操作符
 */
public class MainActivity2 extends AppCompatActivity {

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
     * just 操作符 创建 Observable
     * @param view
     */
    public void r02(View view) {
        Observable.just("A", "B")  // 内部会去发射 A B
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"just  "+s);
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
     * fromArray 操作符 创建 Observable
     * @param view
     */
    public void r04(View view) {
        Observable.fromArray(new String[]{"A","B"}).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"fromArray  "+s);
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
     * empty 操作符 创建 Observable
     * 上游没有发射有值得事件，下游无法确定类型，默认Object，RxJava泛型 泛型默认类型==Object
     * 做一个耗时操作，不需要任何数据来刷新UI， empty的使用场景之一
     * @param view
     */
    public void r05(View view) {
        Observable.empty().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                // 没有事件可以接受
                Log.d(TAG, "empty: " + o);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "empty onComplete: ");
                // 隐藏 加载框...
            }
        });


                        /*// 简化版 观察者
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                // 接受不到
                                // 没有事件可以接受
                                Log.d(TAG, "accept: " + o);
                            }
                        }*/
    }

    public void r06(View view) {
        Observable.interval(2, TimeUnit.SECONDS);
        Observable.intervalRange(1,5,1,2, TimeUnit.SECONDS);

        Observable.range(1,8)
                .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                MainActivity2.this.d = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "range onNext: " + integer);

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
