package com.ten951.design.singleton;

/**
 * @author 王永天
 * @date 2020-10-14 16:05
 */
public class Singleton {
    private Singleton() {

    }

    public static class SingletonHolder {
        public static final Singleton s = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.s;
    }
}
