package com.wangyi.customrxjava;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @版本号：
 * @需求编号：
 * @功能描述：
 * @创建时间：2020-03-09 12:56
 * @创建人：常守达
 */
public class Test {
    interface Observer<T> {//下游观察者

        void onSubscribe();

        void onNext(T t);
    }

    interface ObservableOnSubscribe<T> {//衔接器

        //衔接器接口方法：订阅，内部是下游观察者的回调
        void subscribe(Observer<? super T> emitter);//订阅  T作为参数被消费 可写
    }

    static class ObservableIO<T> implements ObservableOnSubscribe<T> {
        ExecutorService mExecutorService = Executors.newCachedThreadPool();
        ObservableOnSubscribe source;

        public ObservableIO(ObservableOnSubscribe source) {
            this.source = source;
        }

        @Override
        public void subscribe(final Observer<? super T> emitter) {
            mExecutorService.execute(new Runnable() {//上游在线程池执行
                @Override
                public void run() {
                    source.subscribe(emitter);
                }
            });
        }
    }

    static class ObserverAndroidMain<T> implements ObservableOnSubscribe<T> {
        ObservableOnSubscribe source;

        public ObserverAndroidMain(ObservableOnSubscribe source) {
            this.source = source;
        }

        @Override
        public void subscribe(final Observer<? super T> emitter) {
            source.subscribe(new Observer<T>() {
                @Override
                public void onSubscribe() {

                }

                @Override
                public void onNext(final T t) {//下游在主线程
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // 变成 Android main线程
                            emitter.onNext(t);
                        }
                    });
                }
            });
        }
    }

    interface Function<T, R> {
        R apply(T t);// 变换的行为标准
    }

    static class Observable<T> {//上游被观察者
        ObservableOnSubscribe source;//持有衔接器引用

        Observable(ObservableOnSubscribe source) {
            this.source = source;
        }

        static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
            return new Observable<T>(source);
        }

        void subscribe(Observer<? extends T> observer) {
            observer.onSubscribe();//建立订阅
            source.subscribe(observer);//衔接器回调
        }

        static <T> Observable<T> just(final T... t) {
            return new Observable<T>(new ObservableOnSubscribe() {
                @Override
                public void subscribe(Observer emitter) {
                    for (T mT : t) {
                        emitter.onNext(t);
                    }
                }
            });
        }

        <R> Observable<R> map(final Function<? super T, ? extends R> function) {
            return new Observable<R>(new ObservableOnSubscribe<R>() {
                @Override
                public void subscribe(final Observer<? super R> emitter) {
                    source.subscribe(new Observer<T>() {//改变原始衔接器的回调
                        @Override
                        public void onSubscribe() {
                        }

                        @Override
                        public void onNext(T t) {
                            R r = function.apply(t);
                            emitter.onNext(r);//更改发射类型
                        }
                    });
                }
            });
        }

        Observable<T> ObserverAndroidMain() {
            return create(new ObserverAndroidMain<T>(source));
        }

        Observable<T> ObservableIO() {
            return create(new ObservableIO<T>(source));
        }
    }

    public static void main(String[] args) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> emitter) {
                //如果是Observer<? extends Integer> 则生产T 可读 不可写 emitter.onNext(1)报错
                emitter.onNext(1);//在subscribe时，回调该方法
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                System.out.println(integer +" : "+Thread.currentThread());
                return integer + "qqq";
            }
        }).ObservableIO().ObserverAndroidMain()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe() {
                        System.out.println("建立连接");
                    }

                    @Override
                    public void onNext(String integer) {
                        System.out.println(integer + " : "+Thread.currentThread());
                    }
                });
    }
}
