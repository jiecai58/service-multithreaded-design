package com.ten951.design.producerconsumer;

import java.util.concurrent.BlockingDeque;

/**
 * @author 王永天
 * @date 2020-10-21 11:51
 */
public interface WorkStealingEnableChannel<P> extends Channel<P> {
    P take(BlockingDeque<P> preferredQueue) throws InterruptedException;
}
