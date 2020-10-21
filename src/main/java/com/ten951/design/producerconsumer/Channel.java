package com.ten951.design.producerconsumer;

/**
 * @author 王永天
 * @date 2020-10-21 10:52
 */
public interface Channel<P> {
    P take() throws InterruptedException;

    void put(P product) throws InterruptedException;
}
