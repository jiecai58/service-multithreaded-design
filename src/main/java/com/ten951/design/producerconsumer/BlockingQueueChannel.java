package com.ten951.design.producerconsumer;

import java.util.concurrent.BlockingQueue;

/**
 * 存在管道积压风险
 *
 * @author 王永天
 * @date 2020-10-21 10:53
 */
public class BlockingQueueChannel<P> implements Channel<P> {

    private final BlockingQueue<P> queue;

    public BlockingQueueChannel(BlockingQueue<P> queue) {
        this.queue = queue;
    }

    @Override
    public P take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void put(P product) throws InterruptedException {
        queue.put(product);
    }
}
