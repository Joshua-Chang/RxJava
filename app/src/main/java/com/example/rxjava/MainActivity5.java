package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static com.example.rxjava.Cons.TAG;

/**
 * todo 条件 操作符
 * 最后都返回是否符合条件的bool值
 */
public class MainActivity5 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * all，如同(if1&&if2&&if3)那样的功能 ：全部为true，才是true，只要有一个为false，就是false
     *
     * @param view
     */
    public void r01(View view) {
        Observable.just("A", "B", "C", "hahha")
                .all(new Predicate<String>() {//判定
                    @Override
                    public boolean test(String s) throws Exception {
                        return !s.equalsIgnoreCase("xxx");
                    }
                }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.e(TAG, "accept:" + aBoolean);
            }
        });
    }

    /**
     * contains 是否包含
     *
     * @param view
     */
    public void r02(View view) {
        Observable.just("A", "B", "C", "D")
                .contains("A")// 是否包含了 F，条件是否满足
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.e(TAG, "accept:" + aBoolean);
            }
        });
    }
    /**
     * Any 和 All相反的，如同(if1||if2||if3) All全部为true，才是true，只要有一个为false，就是false
     * any 全部为 false，才是false， 只要有一个为true，就是true
     * @param view
     */
    public void r04(View view) {
        Observable.just("A", "B", "C", "D")
                .any(new Predicate<String>() {//判定
                    @Override
                    public boolean test(String s) throws Exception {
                        return "C".equalsIgnoreCase(s);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept:" + aBoolean);
                    }
                });
    }

    /**
     * isEmpty判空
     * @param view
     */
    public void r05(View view) {
        Observable.just("A", "B", "C", "D").isEmpty()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept:" + aBoolean);
                    }
                });
    }


    public void r06(View view) {


    }

}
