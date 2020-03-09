package com.wangyi.customrxjava;

public interface ObservableOnSubscribe<T> {//衔接器
    // ? super  代表可写的   observableEmitter == 观察者

    /**
     * 方法中的范型就是上限下限
     * 使用该方法就是读写模式 super时方法中的参数可写，extend方法中的参数不可写
     * @param observableEmitter
     */
    public void subscribe(Observer<? super T> observableEmitter);//observableEmitter可写 不可读
}
