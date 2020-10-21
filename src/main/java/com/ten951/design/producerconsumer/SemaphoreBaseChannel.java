package com.ten951.design.producerconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 为了避免管道积压  添加信号量限制流量
 *
 * @author 王永天
 * @date 2020-10-21 11:16
 */
public class SemaphoreBaseChannel<P> implements Channel<P> {
    private final BlockingQueue<P> queue;
    private final Semaphore semaphore;

    public SemaphoreBaseChannel(BlockingQueue<P> queue, Semaphore semaphore) {
        this.queue = queue;
        this.semaphore = semaphore;
    }

    @Override
    public P take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void put(P product) throws InterruptedException {
        semaphore.acquire();
        try {
            queue.put(product);
        } finally {
            semaphore.release();
        }
    }
}
