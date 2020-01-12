package com.wangyi.customrxjava;

// todo 被观察者 上游
public class Observable<T> { // 类声明的泛型T  3Int

    ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    // 静态方法声明的<T>泛型        ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) { // 1int
        return new Observable<T>(source); // 静态方法声明的<T>泛型 2int
    }

    // new Observable<T>(source).subscribe(Observer<Int>)
    public void subscribe(Observer<? extends T> observer) {//4int
        observer.onSubscribe();//TODO：B
        source.subscribe(observer); // 这个source就有了  观察者 Observer

//        source.subscribe(observer)等于以下传递
//        source.subscribe(new Observer<T>() {//TODO：C
//            @Override
//            public void onSubscribe() {
//            }
//
//            @Override
//            public void onNext(T t) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

    // 1 2 3 4 可变参数
    public static <T> Observable<T> just(final T... t) { // just 内部发射了
        // 想办法让 source 是不为null的，  而我们的create操作符是，使用者自己传递进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) { // observableEmitter == Observer
                for (T t1 : t) {

                    // Observer.onNext(1);

                    // 发射用户传递的参数数据 去发射事件
                    observableEmitter.onNext(t1);
                }

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 单一
    public static <T> Observable<T> just(final T t) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }


    /**
     * map变换操作符
     * 目的：返回新的Observable，
     * 原理：使用source.subscribe（Observer）
     * 1、替换新的ObservableOnSubscribe内的
     * 2、新的Observer
     * 3、并用source执行
     *
     * T == 上一层传递过来的类型  Integer  变换前的类型
     * R == 给一层的类型         String   变换后的类型
     *
     */
    public <R> Observable<R> map(final Function<? super T, ? extends R> function) { // ? super T 可写模式 因为要拿T去改成R
        /**
         * 根据function返回一个新的Observable（1。需要传入source所用的Observer）
         */
        return new Observable<R>(new ObservableOnSubscribe<R>() {
            @Override
            public void subscribe(final Observer<? super R> observableEmitter) {
                /**
                 * 1.1 ObservableOnSubscribe原始设置observer的方法
                 * 重新设置另一个转换后的observer
                 * 替换其中的onNext为新的
                 */
                source.subscribe(new Observer<T>() {//TODO：C Observer的重新指派
                    @Override
                    public void onSubscribe() {
//                        observableEmitter.onSubscribe();
                    }

                    @Override
                    public void onNext(T t) {
                        R apply = function.apply(t);
                        observableEmitter.onNext(apply);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observableEmitter.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observableEmitter.onComplete();
                    }
                });
            }
        });
    }

    /**
     * map变换操作符
     *
     * T == 上一层传递过来的类型  Integer  变换前的类型
     * R == 给一层的类型         String   变换后的类型
     *
     */

    // TODO: 2020-01-11 繁琐写法
//    public <R> Observable<R> map(Function<? super T, ? extends R> function) { // ? super T 可写模式
//
//        ObservableMap<T, R> observableMap = new ObservableMap(source, function); // source 上一层的能力
//
//        return new Observable<R>(observableMap); // source  该怎么来？     observableMap是source的实现类
//    }
}