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

    protected volatile boolean toShutdown = false;
    public final AtomicInteger reservations = new AtomicInteger(0);
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

    protected void notifyThreadTermination(Terminatable thread) {
        WeakReference<Terminatable> wrThread;
        Terminatable otherThread;
        while (null != (wrThread = coordinatedThread.poll())) {
            otherThread = wrThread.get();
            if (null != otherThread && otherThread != thread) {
                otherThread.terminate();
            }
        }

    }
}
