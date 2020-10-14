package com.ten951.design.twophase;

import com.ten951.design.guarded.AlarmInfo;

/**
 * @author 王永天
 * @date 2020-10-13 19:05
 */
public class AlarmMgr {
    private static final AlarmMgr INSTANCE = new AlarmMgr();
    private volatile boolean shutdownRequest = false;
    private final AlarmSendingThread alarmSendingThread;

    public AlarmMgr() {
        this.alarmSendingThread = new AlarmSendingThread();
    }

    public static AlarmMgr getInstance() {
        return INSTANCE;
    }

    public int sendAlarm(AlarmType type, Long id, String extraInfo) {
        int duplicateSubmissionCount = 0;
        try {
            AlarmInfo alarmInfo = new AlarmInfo(id, type);
            alarmInfo.setExtraInfo(extraInfo);
            duplicateSubmissionCount = alarmSendingThread.sendAlarm(alarmInfo);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return duplicateSubmissionCount;
    }

    public void init() {
        alarmSendingThread.start();
    }

    public synchronized void shutdown() {
        if (shutdownRequest) {
            throw new IllegalStateException("shutdown already requested!");
        }
        alarmSendingThread.terminate();
        shutdownRequest = true;
    }
}
