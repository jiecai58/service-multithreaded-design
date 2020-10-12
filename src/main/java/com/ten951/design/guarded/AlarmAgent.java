package com.ten951.design.guarded;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 保护性暂时挂起.   意思为 当我们要做一件事情B依赖事情A. 我们希望A完成后B在执行. 如果A未完成.就挂起B等待A完成后, 自动执行
 *
 * @author 王永天
 * @date 2020-10-12 15:55
 */
public class AlarmAgent {

    private static final Logger logger = LoggerFactory.getLogger(AlarmAgent.class);

    private volatile boolean connectedToServer = false;

    private final Predicate agentConnected = () -> connectedToServer;

    private final Blocker blocker = new ConditionVarBlocker();


    public void sendAlarm(final AlarmInfo alarm) throws Exception {
        GuardedAction<Void> guardedAction = new GuardedAction<Void>(agentConnected) {
            @Override
            public Void call() throws Exception {
                doSendAlarm(alarm);
                return null;
            }
        };
        blocker.callWithGuard(guardedAction);
    }

    private void doSendAlarm(AlarmInfo alarm) {
        System.out.println("alarm = " + alarm);
        try {
            Thread.sleep(50);
        } catch (Exception ignored) {

        }
    }

    public void init() {
        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "pool-heartbeat"));
        // 这个和task执行时间有关系. 如果执行时间 > 2s.  那么下一个任务会在执行完立即执行.  如果执行时间<2s, 那么等任务执行完间隔2s在执行
        // scheduledThreadPool.scheduleAtFixedRate(new HeartbeatTasK(), 6000L, 2000L, TimeUnit.MILLISECONDS);
        // 这个和执行时间无关   任务开始6秒后. 等上一个任务执行完成, 2秒后启动下一个任务
        scheduledThreadPool.scheduleWithFixedDelay(new HeartbeatTasK(), 6000L, 2000L, TimeUnit.MILLISECONDS);
    }

    private void onConnected() {
        try {
            blocker.signalAfter(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    connectedToServer = true;
                    System.out.println("connected to server: " + System.currentTimeMillis());
                    return Boolean.TRUE;
                }
            });
        } catch (Exception e) {
            logger.error("连接服务器异常", e);
        }
    }

    protected void onDisconnected() {
        connectedToServer = false;
    }

    public void disconnected() {
        logger.info("Disconnected form alarm server.");
        connectedToServer = false;
    }

    private class ConnectingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                System.out.println("connect finish: " + System.currentTimeMillis());
            } catch (InterruptedException ignored) {

            }
            onConnected();
        }
    }

    private class HeartbeatTasK implements Runnable {
        @Override
        public void run() {
            if (!testConnection()) {
                onDisconnected();
                reconnect();
            }
        }

        private boolean testConnection() {
            return false;
        }

        private void reconnect() {
            ConnectingTask connectingTask = new ConnectingTask();
            connectingTask.run();
        }
    }

    public static void main(String[] args) throws Exception {
        AlarmAgent alarmAgent = new AlarmAgent();
        alarmAgent.init();
        System.out.println(System.currentTimeMillis());
        alarmAgent.sendAlarm(new AlarmInfo("湖人总冠军"));
    }
}
