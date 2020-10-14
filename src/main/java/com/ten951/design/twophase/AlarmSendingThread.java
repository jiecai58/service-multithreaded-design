package com.ten951.design.twophase;

import com.ten951.design.guarded.AlarmAgent;
import com.ten951.design.guarded.AlarmInfo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 王永天
 * @date 2020-10-13 19:07
 */
public class AlarmSendingThread extends AbstractTerminatableThread {
    private final AlarmAgent alarmAgent = new AlarmAgent();
    private final BlockingQueue<AlarmInfo> alarmQueue;
    private final ConcurrentMap<String, AtomicInteger> submittedAlarmRegistry;

    public AlarmSendingThread() {
        alarmQueue = new ArrayBlockingQueue<>(100);
        submittedAlarmRegistry = new ConcurrentHashMap<>();
        alarmAgent.init();
    }

    @Override
    protected void doRun() throws Exception {
        AlarmInfo alarm = alarmQueue.take();
        terminationToken.reservations.decrementAndGet();
        try {
            alarmAgent.sendAlarm(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AlarmType.RESUME == alarm.getType()) {
            String key = AlarmType.FAULT.toString() + ":" + alarm.getId() + '@' + alarm.getExtraInfo();
            submittedAlarmRegistry.remove(key);
            key = AlarmType.RESUME.toString() + ":" + alarm.getId() + '@' + alarm.getExtraInfo();
            submittedAlarmRegistry.remove(key);
        }
    }

    public int sendAlarm(final AlarmInfo alarmInfo) {
        AlarmType type = alarmInfo.getType();
        Long id = alarmInfo.getId();
        String extraInfo = alarmInfo.getExtraInfo();
        if (terminationToken.isToShutdown()) {
            System.err.println("rejected alarm:" + id + "," + extraInfo);
            return -1;
        }
        int duplicateSubmissionCount = 0;
        try {
            AtomicInteger prevSubmittedCounter;
            prevSubmittedCounter = submittedAlarmRegistry.putIfAbsent(type.toString() + ":" + id + "@" + extraInfo, new AtomicInteger(0));
            if (null == prevSubmittedCounter) {
                terminationToken.reservations.incrementAndGet();
                alarmQueue.put(alarmInfo);
            } else {
                duplicateSubmissionCount = prevSubmittedCounter.incrementAndGet();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return duplicateSubmissionCount;
    }

    @Override
    protected void doCleanup(Exception exp) {
        if (null != exp && !(exp instanceof InterruptedException)) {
            exp.printStackTrace();
        }
        alarmAgent.disconnected();
    }
}
