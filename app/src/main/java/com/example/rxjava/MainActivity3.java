package com.example.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import io.reactivex.observables.GroupedObservable;

import static com.example.rxjava.Cons.TAG;
/**
 * TODO 变换型操作符
 */
public class MainActivity3 extends AppCompatActivity {

    private Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * map 变换 操作符
     *
     * @param view
     */
    public void r01(View view) {
        Observable.just(1)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        Log.d(TAG, "map1 apply: " + integer);

                        return "【" + integer + "】";
                    }
                })
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        Log.d(TAG, "map2 apply: " + s);
                        return Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
                    }
                })
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.d(TAG, "下游 onNext: " + bitmap.getAllocationByteCount());

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
     * flatMap 变换 操作符
     *
     * @param view
     */
    public void r02(View view) {
        Observable.just(1111)  // 内部会去发射 A B
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    // TODO: 2020/1/10 0010 返回值ObservableSource是Observable的父类
                    public ObservableSource<?> apply(final Integer integer) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> e) throws Exception {
                                e.onNext(integer + "flatMap变换操作符");
                                e.onNext(integer + "flatMap变换操作符");
                                e.onNext(integer + "flatMap变换操作符");
                            }
                        });
                    }
                }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "下游接收 变换操作符 发射的事件 accept: " + o.toString());
            }
        });
    }

    /**
     * 体现 flatMap 变换 操作符 是不排序的
     * concatMap排序
     *
     * @param view
     */
    public void r04(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
                e.onNext("B");
                e.onNext("C");
                e.onComplete();
            }
        }).concatMap(new Function<String, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(s + " index: " + (i + 1));
                }
                return Observable.fromIterable(list).delay(5, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "下游 accept: " + o);
            }
        });
    }

    /**
     * 很多的数据，不想全部一起发射出去，分批次，先缓存到Buffer
     *
     * @param view
     */
    public void r05(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).buffer(20)        // 变换 buffer

                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.d(TAG, "accept: " + integers);
                    }
                });
    }

    /**
     * 分组变换 groupBy
     *
     * @param view
     */
    public void r06(View view) {
        Observable.range(1, 8)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer % 2 == 0 ? integer + "偶数" : integer + "奇数";
                    }
                }).subscribe(//分组类别
                // 使用groupBy下游是 有标准的
                new Consumer<GroupedObservable<String, Integer>>() {//组名
                    @Override
                    public void accept(final GroupedObservable<String, Integer> stringIntegerGroupedObservable) throws Exception {
                        Log.d(TAG, "accept: " + stringIntegerGroupedObservable.getKey());
                        // 以上还不能把信息给打印全面，只是拿到了，分组的key

                        // 输出细节，还需要再包裹一层
                        // 细节 GroupedObservable 被观察者
                        stringIntegerGroupedObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d(TAG, "accept: 类别：" + stringIntegerGroupedObservable.getKey() + "  价格：" + integer);
                            }
                        });
                    }
                });

    }

    public void cut(View view) {
        if (d != null) {
            d.dispose();
        }
    }
}
