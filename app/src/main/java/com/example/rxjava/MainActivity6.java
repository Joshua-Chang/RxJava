package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

import static com.example.rxjava.Cons.TAG;

/**
 * TODO 合并型操作符
 */
public class MainActivity6 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * startWith 合并操作符, 被观察者1.startWith(被观察者2) 先执行 被观察者2 里面发射的事件，然后再执行 被观察者1 发射的事件
     *
     * @param view
     */
    public void r01(View view) {
        Observable.just("Tom", "Jim", "Emily")

                .startWith(Observable.just("A", "B", "c"))//先执行
                .startWith(Observable.just("X", "Y", "Z"))//先执行
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 2
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
                .startWith(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        // todo 1
                        e.onNext(10000);
                        e.onNext(20000);
                        e.onNext(30000);
                        e.onComplete();
                    }
                }))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
    }

    /**
     * concatWith往后拼接，可调用拼接多次
     * concatWith 和 startWith 的区别，是相反的
     * concatWith 合并操作符, 被观察者1.concatWith(被观察者2) 先执行 被观察者1 里面发射的事件，然后再执行 被观察者2 发射的事件
     *
     * @param view
     */
    public void r02(View view) {
        Observable.just("Tom", "Jim", "Emily")//先执行

                .concatWith(Observable.just("A", "B", "c"))//后执行
                .concatWith(Observable.just("x", "y", "z"))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });


        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 1
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
                .concatWith(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        // todo 2
                        e.onNext(10000);
                        e.onNext(20000);
                        e.onNext(30000);
                        e.onComplete();
                    }
                }))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
    }

    /**
     * concat 合并操作符 的特性：最多能够合并四个，按照我们存入的顺序执行
     *
     * @param view
     */
    public void r04(View view) {
        // 上游 被观察者
        Observable.concat(

                Observable.just("1") // todo 1
                ,
                Observable.just("2") // todo 2
                ,
                Observable.just("3") // todo 3
                , Observable.just("4") // todo 3
//                ,
//                Observable.just(1)

        )

                .subscribe(new Consumer<String>() { // 下游 观察者
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });

//                .subscribe(new Consumer<Serializable>() {
//                    @Override
//                    public void accept(Serializable serializable) throws Exception {
//                        Log.d(TAG, "accept: " + serializable);            }
//                });
    }

    /**
     * merge 并列操作符 的特性：最多能够合并四个，并列执行 并发
     *
     * @param view
     */
    public void r05(View view) {
        // 为了体现并列执行 并发，所以要新学一个操作符(intervalRange)
        // 被观察者  start开始累计， count累计多个个数量， initDelay开始等待时间，  period 每隔多久执行， TimeUnit 时间单位
        Observable<Long> observable1 = Observable.intervalRange(1, 5, 1, 2, TimeUnit.SECONDS);//12345
        Observable<Long> observable2 = Observable.intervalRange(6, 5, 1, 2, TimeUnit.SECONDS);//678910
        Observable<Long> observable3 = Observable.intervalRange(11, 5, 1, 2, TimeUnit.SECONDS);
        Observable<Long> observable4 = Observable.intervalRange(16, 5, 1, 2, TimeUnit.SECONDS);
//        observable1.subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                Log.d(TAG, "accept: " + aLong);//12345
//            }
//        });
        Observable.merge(observable1, observable2, observable3, observable4).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                // 被观察者1  1
                // 被观察者2  6
                // 被观察者3  11
                Log.d(TAG, "accept: " + aLong);
            }
        });
    }

    /**
     * zip 合并操作符：合并的被观察者发射的不同类型事件
     * 需求：考试 课程 == 分数
     * ！！！时间发射数需要对应，不匹配的被忽略
     * @param view
     */
    public void r06(View view) {
        // 为了体现并列执行 并发，所以要新学一个操作符(intervalRange)
        // 被观察者  start开始累计， count累计多个个数量， initDelay开始等待时间，  period 每隔多久执行， TimeUnit 时间单位
        Observable<Long> observable1 = Observable.intervalRange(1, 5, 1, 2, TimeUnit.SECONDS);//12345
        Observable<Long> observable2 = Observable.intervalRange(6, 5, 1, 2, TimeUnit.SECONDS);//678910
        Observable<Long> observable3 = Observable.intervalRange(11, 5, 1, 2, TimeUnit.SECONDS);
        Observable<Long> observable4 = Observable.intervalRange(16, 5, 1, 2, TimeUnit.SECONDS);
        Observable.zip(observable1, observable2, observable3, observable4,
                new Function4<Long, Long, Long, Long, String>() {
                    @Override
                    public String apply(Long aLong, Long aLong2, Long aLong3, Long aLong4) throws Exception {
                        return aLong + "-" + aLong2 + "-" + aLong3 + "-" + aLong4;
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
            }
        });
    }

    /**
     * zip 合并操作符：合并的被观察者发射的不同类型事件
     * 需求：考试 课程 == 分数
     * ！！！时间发射数需要对应，不匹配的被忽略
     * @param view
     */
    public void cut(View view) {
        // 课程 被观察者
        Observable observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("英语"); // String
                e.onNext("数学");
                e.onNext("政治");
                e.onNext("物理");  // 被忽略 ！！！数据需要对应，不匹配的被忽略
                e.onComplete();
            }
        });

        // 分数 被观察者
        Observable observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(85); // Integer
                e.onNext(90);
                e.onNext(96);
                e.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<String,Integer,StringBuffer>() {//最后为返回泛型
            @Override
            public StringBuffer apply(String o, Integer o2) throws Exception {
                return new StringBuffer().append("科目：").append(o).append("|：成绩").append(o2);
            }
        }).subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: 准备进入考场，考试了....");
            }

            @Override
            public void onNext(Object o) {
                Log.d(TAG, "onNext: 考试结果输出 " + o);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: 考试全部完毕");
            }
        });
    }

}
