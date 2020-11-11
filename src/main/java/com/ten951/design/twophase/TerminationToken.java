package com.ten951.design.twophase;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 终止凭证
 *
 * @author 王永天
 * @date 2020-10-13 15:36
 */
public class TerminationToken {

    // 停止标记位. 设置volatile 为了多线程情况下的可见性
    protected volatile boolean toShutdown = false;
    // 记录次数
    public final AtomicInteger reservations = new AtomicInteger(0);
    // WeakReference 当发生GC时就会被回收
    // SoftReference 软引用. 当内存不足时. 会被GC回收
    private final Queue<WeakReference<Terminatable>> coordinatedThread;

    public TerminationToken() {
        this.coordinatedThread = new ConcurrentLinkedQueue<>();
    }

    public boolean isToShutdown() {
        return toShutdown;
    }


    protected void setToShutdown(boolean toShutdown) {
        this.toShutdown = toShutdown;
    }

    protected void register(Terminatable thread) {
        coordinatedThread.add(new WeakReference<>(thread));
    }

    protected void notifyThreadTermination(Terminatable termin) {
        WeakReference<Terminatable> wrThread;
        Terminatable terminatable;
        while (null != (wrThread = coordinatedThread.poll())) {
            terminatable = wrThread.get();
            if (null != terminatable && terminatable != termin) {
                terminatable.terminate();
            }
        }

    }
}
