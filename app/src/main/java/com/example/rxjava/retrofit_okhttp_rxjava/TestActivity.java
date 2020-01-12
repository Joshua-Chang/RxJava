package com.example.rxjava.retrofit_okhttp_rxjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.rxjava.Cons;
import com.example.rxjava.R;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.IRequestNetwork;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.LoginRequest;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.LoginResponse;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.MyRetrofit;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.RegisterRequest;
import com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp.RegisterResponse;

/**
 * Retrofit + RxJava
 * 需求：
 * 1.请求服务器注册操作
 * 2.注册完成之后，更新注册UI
 * 3.马上去登录服务器操作
 * 4.登录完成之后，更新登录的UI
 */
public class TestActivity extends AppCompatActivity {


    private TextView tv_register_ui;
    private TextView tv_login_ui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        tv_register_ui = findViewById(R.id.tv_register_ui);
        tv_login_ui = findViewById(R.id.tv_login_ui);
    }
    public void request(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在执行中...");

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e(Cons.TAG,"2:register..."+Thread.currentThread());
                Thread.sleep(5000);
                e.onNext(1);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())// TODO: 1 指定上游的一次请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//TODO: 2 指定下游的完成更新UI在主线程
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(Cons.TAG,"3:register success"+Thread.currentThread());
                        tv_register_ui.setText("register success");
                    }
                })
                .observeOn(Schedulers.io())// TODO: 3 指定下游的二次请求在子线程
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final Integer integer) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> e) throws Exception {
                                Log.e(Cons.TAG,"4:login..."+Thread.currentThread());
                                Thread.sleep(5000);
                                e.onNext(integer+"");
                                e.onComplete();
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())// TODO: 4 指定下游的完成更新UI在主线程
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(Cons.TAG,"1:onSubscribe"+Thread.currentThread());
                progressDialog.show();
            }

            @Override
            public void onNext(String s) {
                Log.e(Cons.TAG,"5:login success"+Thread.currentThread());
                tv_login_ui.setText("login success");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    Log.e(Cons.TAG,"6:onComplete"+Thread.currentThread());
                }
            }
        });

    }


    public void request1(View view) {
        // 分开写
        /**
         * 1.请求服务器注册操作
         * 2.注册完成之后，更新注册UI
         */
        // IRequestNetwork iRequestNetwork = MyRetrofit.createRetrofit().create(IRequestNetwork.class);

        // 1.请求服务器注册操作
        MyRetrofit.createMyRetrofit().create(IRequestNetwork.class) // IRequestNetwork
                // IRequestNetwork.registerAction
                .registerAction(new RegisterRequest())  // Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程
                // 2.注册完成之后，更新注册UI
                .subscribe(new Consumer<RegisterResponse>() { // 下游 简化版
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        // 更新注册相关的所有UI
                        // .....
                    }
                });

        // 3.马上去登录服务器操作
        MyRetrofit.createMyRetrofit().create(IRequestNetwork.class)
                .loginAction(new LoginRequest())  // Observable<LoginResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程

                // 4.登录完成之后，更新登录的UI
                .subscribe(new Consumer<LoginResponse>() { // 下游 简化版
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        // 更新登录相关的所有UI
                        // .....
                    }
                });
    }

    private ProgressDialog progressDialog;

    public void request2(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在执行中...");

        /**
         * 一行代码 实现需求
         * 需求：
         *  * 1.请求服务器注册操作
         *  * 2.注册完成之后，更新注册UI
         *  * 3.马上去登录服务器操作
         *  * 4.登录完成之后，更新登录的UI
         */
        MyRetrofit.createMyRetrofit().create(IRequestNetwork.class)
                //  1.请求服务器注册操作  // todo 第二步 请求服务器 注册操作
                .registerAction(new RegisterRequest()) // Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程
                // 2.注册完成之后，更新注册UI

                /**
                 *  这样不能订阅，如果订阅了，就无法执行
                 *      3 马上去登录服务器操作
                 *      4.登录完成之后，更新登录的UI
                 *
                 *  所以我们要去学习一个 .doOnNext()，可以在不订阅的情况下，更新UI
                 */
                .doOnNext(new Consumer<RegisterResponse>() { // 简单版本的下游
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        // todo 第三步 更新注册相关的所有UI
                        // 更新注册相关的所有UI
                        tv_register_ui.setText("xxx");
                        // .......
                    }
                })
                // 3.马上去登录服务器操作 -- 耗时操作
                .observeOn(Schedulers.io()) // todo 分配异步线程
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        // 还可以拿到 注册后的响应对象RegisterResponse
                        // 执行耗时操作
                        // 马上去登录服务器操作 -- 耗时操作
                        Observable<LoginResponse> observable = MyRetrofit.createMyRetrofit().create(IRequestNetwork.class)
                                .loginAction(new LoginRequest());  // todo 第四步 马上去登录服务器操作 -- 耗时操作
                        return observable;
                    }
                })
                // 4.登录完成之后，更新登录的UI
                .observeOn(AndroidSchedulers.mainThread()) // // todo 给下游切换 主线程
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // todo 第一步
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        // 更新登录相关的所有UI
                        // todo 第五步 更新登录相关的所有UI
                        tv_login_ui.setText("xxxx");
                        // ...........
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // todo 第六步
                        progressDialog.dismiss(); // 结束对话框 ，整个流程完成
                    }
                });

    }



}
