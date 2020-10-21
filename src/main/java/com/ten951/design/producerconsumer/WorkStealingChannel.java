package com.ten951.design.producerconsumer;

import java.util.concurrent.BlockingDeque;

/**
 * 工作窃取算法
 *
 * @author 王永天
 * @date 2020-10-21 11:53
 */
public class WorkStealingChannel<P> implements WorkStealingEnableChannel<P> {
    private final BlockingDeque<P>[] manageQueues;

    public WorkStealingChannel(BlockingDeque<P>[] manageQueues) {
        this.manageQueues = manageQueues;
    }

    @Override
    public P take() throws InterruptedException {
        return take(null);
    }

    @Override
    public void put(P product) throws InterruptedException {
        int index = product.hashCode() % manageQueues.length;
        BlockingDeque<P> manageQueue = manageQueues[index];
        manageQueue.put(product);
    }

    @Override
    public P take(BlockingDeque<P> preferredQueue) throws InterruptedException {
        BlockingDeque<P> targetQueue = preferredQueue;
        P product = null;
        if (null != targetQueue) {
            product = targetQueue.poll();
        }
        int queueIndex = -1;
        while (null == product) {
            queueIndex = (queueIndex + 1) % manageQueues.length;
            targetQueue = manageQueues[queueIndex];
            product = targetQueue.pollLast();
            if (preferredQueue == targetQueue) {
                break;
            }
        }

        if (null == product) {
            queueIndex = (int) (System.currentTimeMillis() % manageQueues.length);
            targetQueue = manageQueues[queueIndex];
            product = targetQueue.takeLast();
        }

        return product;
    }
}
