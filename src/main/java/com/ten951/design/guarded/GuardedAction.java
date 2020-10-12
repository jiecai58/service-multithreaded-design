package com.ten951.design.guarded;

import java.util.concurrent.Callable;

/**
 * @author 王永天
 * @date 2020-10-12 16:00
 */
public abstract class GuardedAction<V> implements Callable<V> {

    protected final Predicate guard;

    public GuardedAction(Predicate guard) {
        this.guard = guard;
    }
}
