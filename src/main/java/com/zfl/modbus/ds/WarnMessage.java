package com.zfl.modbus.ds;

import java.io.Serializable;

/**
 * @ClassName WarnMessage
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/22  15:28
 **/
public class WarnMessage implements Serializable{
    /**
     * number 代表序号
     * bit 代表告警信息所属的位
     */

    public int number;
    public String warnType;
    public int bit;
    public String warnName;
    public int warnId;
    public String warnLevel;

    public WarnMessage(int number, String warnType, int bit, String warnName, int warnId, String warnLevel) {
        this.number = number;
        this.warnType = warnType;
        this.bit = bit;
        this.warnName = warnName;
        this.warnId = warnId;
        this.warnLevel = warnLevel;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public int getBit() {
        return bit;
    }

    public void setBit(int bit) {
        this.bit = bit;
    }

    public String getWarnName() {
        return warnName;
    }

    public void setWarnName(String warnName) {
        this.warnName = warnName;
    }

    public int getWarnId() {
        return warnId;
    }

    public void setWarnId(int warnId) {
        this.warnId = warnId;
    }

    public String getWarnLevel() {
        return warnLevel;
    }

    public void setWarnLevel(String warnLevel) {
        this.warnLevel = warnLevel;
    }

    @Override
    public String toString() {
        return "WarnMessage{" + "number=" + number + ", warnType='" + warnType + '\'' + ", bit=" + bit + ", warnName='" + warnName + '\'' + ", warnId='" + warnId + '\'' + ", warnLevel='" + warnLevel + '\'' + '}';
    }
}
