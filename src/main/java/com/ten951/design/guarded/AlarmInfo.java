package com.ten951.design.guarded;

import com.ten951.design.twophase.AlarmType;

import java.util.StringJoiner;

/**
 * @author 王永天
 * @date 2020-10-12 16:56
 */
public class AlarmInfo {

    private String message;

    private Long id;
    private AlarmType type;

    private String extraInfo;

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlarmType getType() {
        return type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }

    public AlarmInfo() {
    }

    public AlarmInfo(String message) {
        this.message = message;
    }

    public AlarmInfo(Long id, AlarmType type) {
        this.id = id;
        this.type = type;
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
