package com.ten951.design.guarded;

import java.util.StringJoiner;

/**
 * @author 王永天
 * @date 2020-10-12 16:56
 */
public class AlarmInfo {

    private String message;


    public AlarmInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AlarmInfo.class.getSimpleName() + "[", "]")
                .add("message='" + message + "'")
                .toString();
    }
}
