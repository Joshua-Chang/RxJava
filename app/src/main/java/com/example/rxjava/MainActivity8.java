package com.example.rxjava;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.rxjava.Cons.TAG;

/**
 * todo  RxJava线程切换的学习
 */
public class MainActivity8 extends AppCompatActivity {


    private ProgressDialog progressDialog;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);

        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());  // 主线程main  安卓主线程
    }

    /**
     * todo 异步线程区域
     * Schedulers.io() ：代表io流操作，网络操作，文件流，耗时操作
     * Schedulers.newThread()    ： 比较常规的，普普通通
     * Schedulers.computation()  ： 代表CPU 大量计算 所需要的线程
     * <p>
     * todo main线程 主线程
     * AndroidSchedulers.mainThread()  ： 专门为Android main线程量身定做的
     * <p>
     * todo 给上游分配多次，只会在第一次切换，后面的不切换了（忽略）
     * todo 给下游分配多次，每次都会去切换
     *
     * @param view
     */
    public void r01(View view) {
        // RxJava如果不配置，默认就是主线程main
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "上游 : " + Thread.currentThread().getName());  // 主线程main  安卓主线程
                e.onNext("1");

            }
        })
                .subscribeOn(Schedulers.io()) // todo 给上游配置异步线程    // 给上游分配多次，只会在第一次切换，后面的不切换了
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                // result: io 异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游配置 安卓主线程    // 给下游分配多次，每次都会去切换
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(Schedulers.io()) // 切换一次线程
                // result: io 异步线程

                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "下游 : " + Thread.currentThread().getName());
                    }
                });

    }

    /**
     * todo 同步 和 异步 执行流程
     * 同步：（上游发送-》下游接收）*n次
     * 异步： 上游发送*n次-》下游接收*n次
     *
     * @param view
     */
    public void r02(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: 上游发送了一次 1 ");
                e.onNext(1);

                Log.d(TAG, "subscribe: 上游发送了一次 2 ");
                e.onNext(2);

                Log.d(TAG, "subscribe: 上游发送了一次 3 ");
                e.onNext(3);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "下游 accept: " + integer);

                    }
                });


        /**
         * 默认情况下 同步的想象
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 1
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 1
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 2
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 2
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 3
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 3
         */

        /**
         * 配置了 异步后
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 1
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 2
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 3
         * 09-05 16:26:03.574 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 1
         * 09-05 16:26:03.574 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 2
         * 09-05 16:26:03.575 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 3
         */
    }

    private final String PATH = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";

    /**
     * todo 不使用RxJava去下载图片
     *
     * @param view
     */
    public void r04(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    URL url = new URL(PATH);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        Message msg = new Message();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            imageView.setImageBitmap(bitmap);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            return false;
        }
    });

    /**
     * todo 使用RxJava去下载图片
     *
     * @param view
     */
    public void r05(View view) {

        Observable.just(PATH)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        try {
                            Thread.sleep(2000);
                            URL url = new URL(PATH);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(5000);
                            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                Log.d(TAG, "map1 : " + Thread.currentThread().getName());
                                return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                      Paint paint=new Paint();
                        paint.setColor(Color.RED);
                        paint.setTextSize(30);
                        return drawText(bitmap,"水印水印水印",paint,60,60);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {//订阅时弹出加载
                        progressDialog = new ProgressDialog(MainActivity8.this);
                        progressDialog.setMessage("loading...");
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.d(TAG, "Observer : " + Thread.currentThread().getName());
                        if (imageView != null)
                            imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (imageView != null)
                            imageView.setImageResource(R.mipmap.ic_launcher); // 下载错误的图片
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });
    }

    private Bitmap drawText(Bitmap bitmap, String s, Paint paint, int pl, int pt) {
        Log.d(TAG, "map2 : " + Thread.currentThread().getName());
        paint.setDither(true);
        paint.setFilterBitmap(true);
        bitmap = bitmap.copy(bitmap.getConfig()==null? Bitmap.Config.ARGB_8888:bitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(s, pl, pt, paint);
        return bitmap;
    }



    public void r06(View view) {


    }

}
