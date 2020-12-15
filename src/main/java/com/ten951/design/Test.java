package com.ten951.design;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 王永天
 * @date 2020-11-03 14:01
 */
public class Test<T> {
    private final BlockingQueue<T> queue = new ArrayBlockingQueue<>(400);
    ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(30));
    private ReentrantLock lock = new ReentrantLock();

    public enum TaskType {
        sub1, sub2, sub3;
    }

    public void cu() throws InterruptedException {
        // CompletionService service = new ExecutorCompletionService<>(executorService);
        Map<TaskType, Boolean> map = new ConcurrentHashMap<>(3);
        map.put(TaskType.sub1, Boolean.FALSE);
        map.put(TaskType.sub2, Boolean.FALSE);
        map.put(TaskType.sub3, Boolean.FALSE);
        CountDownLatch latch = new CountDownLatch(1);
        Thread1 objectThread1 = new Thread1(latch, map);
        Thread2 objectThread2 = new Thread2(latch, map);
        Thread3 objectThread3 = new Thread3(latch, map);
       /* Future<String> submit = executorService.submit(objectThread1);
        Future<String> submit2 = executorService.submit(objectThread2);*/
        Future<String> submit3 = executorService.submit(objectThread3);
        Future<String> submit31 = executorService.submit(objectThread3);
        Future<String> submit32 = executorService.submit(objectThread3);
        System.out.println("执行关闭");
        executorService.shutdownNow();
        /*service.submit(() -> {
            Thread.sleep(1000L);
            throw new NullPointerException();
           // return "T1";
        });
        service.submit(() -> {
            Thread.sleep(2000L);
            return "T2";
        });
        service.submit(() -> {
            Thread.sleep(3000L);
            return "T3";
        });*/
        // Object o = service.take().get();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Test<>().cu();

    }


    private static class Thread1 implements Callable<String> {
        private final CountDownLatch latch;
        private final Map<TaskType, Boolean> map;

        public Thread1(CountDownLatch latch, Map<TaskType, Boolean> map) {
            this.latch = latch;
            this.map = map;
        }

        @Override
        public String call() {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //重新中断
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
            map.put(TaskType.sub1, Boolean.TRUE);
            return "T1执行完成";
        }
    }

    private static class Thread2 implements Callable<String> {
        private final CountDownLatch latch;
        private final Map<TaskType, Boolean> map;

        public Thread2(CountDownLatch latch, Map<TaskType, Boolean> map) {
            this.latch = latch;
            this.map = map;
        }

        @Override
        public String call() {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                //重新中断
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
            map.put(TaskType.sub2, Boolean.TRUE);
            return "T2执行完成";
        }
    }

    private static class Thread3 implements Callable<String> {
        private final CountDownLatch latch;
        private final Map<TaskType, Boolean> map;

        public Thread3(CountDownLatch latch, Map<TaskType, Boolean> map) {
            this.latch = latch;
            this.map = map;
        }

        @Override
        public String call() {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                //重新中断
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
            if (!Thread.currentThread().isInterrupted()) {
                System.out.println("继续执行");
            } else {
                System.out.println("中断了 不执行");
            }
            map.put(TaskType.sub3, Boolean.TRUE);
            return "T3执行完成";
        }
    }
}
