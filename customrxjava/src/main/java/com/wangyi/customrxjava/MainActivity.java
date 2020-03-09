package com.wangyi.customrxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Observable.create(
//                new ObservableOnSubscribe<Integer>() {//TODO：A
//            @Override
//            public void subscribe(Observer<? super Integer> observableEmitter) {// 使用到了，就产生了读写模式 可写
//                Log.e(">>>","开始发射");
//                // TODO: 2020-01-11 2
//                observableEmitter.onNext(1);//TODO：D //  <? extends Integer> 不可写了   <? super Integer>可写
//                observableEmitter.onComplete();
//            }
//        }
////                new ObservableOnSubscribe<Integer>() {
////                    @Override
////                    public void subscribe(Observer<? extends Integer> observableEmitter) {
////                        observableEmitter.onNext(1);//不可写
////                    }
////                }
//
//        )


//                Observable.just(1,2,3)
                Observable.just("A")
                        .map(new Function<String, Integer>() {
                            @Override
                            public Integer apply(String s) {
                                return 1;
                            }
                        })
                .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe() {
                // TODO: 2020-01-11 1
                Log.e(">>>","订阅成功");
            }

            @Override
            public void onNext(Integer integer) {
                // TODO: 2020-01-11 3
                Log.e(">>>","开始接收"+integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                // TODO: 2020-01-11 4
                Log.e(">>>","接受完成");
            }
        });
    }

    public void r01(View view) {

    }

    public void r02(View view) {

    }

    public void r04(View view) {

    }
}
