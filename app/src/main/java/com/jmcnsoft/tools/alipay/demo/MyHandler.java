package com.jmcnsoft.tools.alipay.demo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 公用Handler，防止内存泄露，声明静态内部类继承此类
 */

public abstract class MyHandler<T> extends Handler {

    private WeakReference<T> mTargets;

    public MyHandler(T target) {
        this.mTargets = new WeakReference<>(target);
    }

    public MyHandler(T target, Looper looper) {
        super(looper);
        mTargets = new WeakReference<>(target);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T target = mTargets.get();
        if (target != null) {
            handle(target, msg);
        }
    }

    public abstract void handle(T target, Message msg);
}
