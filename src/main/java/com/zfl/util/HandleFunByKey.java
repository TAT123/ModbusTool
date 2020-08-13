package com.zfl.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zfl.gsonMessage.Result;
import com.zfl.modbus.ds.WarnMessage;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName HandleFunByKey
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/18  17:21
 **/


public class HandleFunByKey {

    public static WarnMessage warnMessage;

    /**
     * 只有警告信息才用这里的list
     */

    public static List<WarnMessage> list = new ArrayList <>();

    private static List<WarnMessage> listForTwo = new ArrayList <>();

    public static List<WarnMessage> listSmall = new ArrayList <>();

    public static final Gson GSON = new GsonBuilder().create();

    private static final Logger log = LoggerFactory.getLogger(HandleFunByKey.class);
    /**
     * 对获取的设备状态返回的十六进制字符串 进行判断处理
     * 60型号与33型号共用一个
     * @param hexStr
     * @return
     */
    public static String handleDeviceStatus(String hexStr) {
        switch(hexStr) {
            case "0" :
                return "0x0000:待机：初始化";
            case "1" :
                return "0x0001:待机：绝缘阻抗检测";
            case "2" :
                return "0x0002:待机：光照检测";
            case "3" :
                return "0x0003:待机：电网检测";
            case "100" :
                return "0x0100:启动";
            case "200" :
                return "0x0200:并网";
            case "201" :
                return "0x0201:并网:限功率";
            case "202" :
                return "0x0202:并网:自降额";
            case "300" :
                return "0x0300:关机：异常关机";
            case "301" :
                return "0x0301:关机：指令关机";
            case "302" :
                return "0x0302:关机:OVGR";
            case "303" :
                return "0x0303:关机:通信断链";
            case "304" :
                return "0x0304:关机:限功率";
            case "305" :
                return "0x0305:关机:需手动开机";
            case "306" :
                return "0x0306:关机:直流开关断开";
            case "401" :
                return "0x0401:电网调度：cosψ-P曲线";
            case "402" :
                return "0x0402:电网调度：Q-U 曲线";
            case "a000" :
                return "0xA000:待机：无光照";
            case "A000" :
                return "0xA000:待机：无光照";
            case "500" :
                return "0x0500:点检就绪";
            case "501" :
                return "0x0501:点检中";
            case "600"  :
                return "0x0501:点检中";
            case "700" :
                return "0X0700:AFCI自检";
            case "800" :
                return "0X0800:IV扫描中";
            case "900" :
                return "0X0900:直流输入检测";
            default:
                return "非法的设备状态代码";
        }
    }

    /**
     * 处理返回的大小为16的布尔类型的数组中10位bit信息  采用大端法bit9从a[6] 开始到 bit0 a[15]
     * 默认处理为全部为false
     * Todo 这里要把message{} 中 a[6] 到a[15]的信息进行对比
     * @param message
     * @return  返回时处理的信息逆序排列
     */
    public static List<String> handleSingleMessage(boolean [] message) {
        List<String> list = new ArrayList <>();
        String [] flag = {"9点检 ","8关机"," 7限电停运 ","6故障停运 ","5正常停运 ","4自降额并网 ","3限电降额并网 ","2正常并网 ","1并网 ","0待机"};
        if(message.length != 10) {
            log.info("错误的数组长度");
            return list;
        } else {
            for (int i = 0; i < flag.length ; i++) {
                list.add(i, "");
            }
            for (int j = 0; j < message.length ; j++) {
                if(message [j + 6]) {
                    list.set(j , flag[j]);
                }
            }
            return list;

        }

    }




    /**
     * 3位bit信息处理 高字节位于低位地址 大端法
     * @param message 13 14 15 位信息分别对应bit2 bit1 bit0的信息
     * @return
     */
    public static List<String> handleRunStatus(boolean [] message) {
        int bitTwo = 13;
        int bitOne = 14;
        int bitZero = 15;
        int size = 16;
        List<String> list = new ArrayList <>();
        if(message.length != size) {
            log.info("错误的数组长度");
        } else {
            if(message [bitTwo]) {
                list.add(0 , "Bit2:DSP数据采集状态(1：有)");
            } else {
                list.add(0 , "Bit2:DSP数据采集状态(0：无)");
            }
            if(message [bitOne]) {
                list.add(1 , "Bit1:PV连接状态(1:连接)");
            }else {
                list.add(1 , "Bit1:PV连接状态(0:未连接)");
            }
            if(message [bitZero]) {
                list.add(2 , "Bit0:闭锁状态（1：非闭锁）");
            }else {
                list.add(2 , "Bit0:闭锁状态（0:闭锁）");
            }
        }
        return list;
    }

    /**
     * 3位bit信息处理 高字节位于低位地址 大端法
     * @param message 13 14 15 位信息分别对应bit2 bit1 bit0的信息
     * @return
     */
    public static List<String> handleProtectedStatus(boolean [] message) {
        int bitTwo = 13;
        int bitOne = 14;
        int bitZero = 15;
        int size = 16;
        List<String> list = new ArrayList <>();
        if(message.length != size) {
            log.info("错误的数组长度");
        } else {
            if(message[bitTwo]) {
                list.add(0, "Bit2:孤岛效应保护状态(1：有)");
            } else {
                list.add(0, "Bit2:孤岛效应保护状态(0：无)");
            }
            if(message [bitOne]) {
                list.add(1, "Bit1:低电压穿越保护状态(1:连接)");
            }else {
                list.add(1, "Bit1:低电压穿越保护状态(0:未连接)");
            }
            if(message[bitZero]) {
                list.add(2, "Bit0:零电压穿越保护状态（1：非闭锁）");
            }else {
                list.add(2, "Bit0:零电压穿越保护状态（0:闭锁）");
            }
        }
        return list;
    }

    /**
     * 根据数组元素所在位置判断
     * @param i
     * @return  return会立即结束整个函数
     */
    private static List<WarnMessage> getWarnOneValue(int i) {
        switch(i) {
            case 15:
                list.add(new WarnMessage(1, "告警1", 0, "组串电压高", 2001, "重要"));
                break;
            case 14:
                list.add(new WarnMessage(2, "告警1", 1, "直流电弧故障", 2002, "重要"));
                break;
            case 13:
                list.add(new WarnMessage(3, "告警1", 2, "组串反接", 2011, "重要"));
                break;
            case 12:
                list.add(new WarnMessage(4, "告警1", 3, "组串反灌", 2012, "提示"));
                break;
            case 11:
                list.add(new WarnMessage(5, "告警1", 4, "组串功率异常", 2013, "提示"));
                break;
            case 10:
                list.add(new WarnMessage(6, "告警1", 5, "AFCI自检失败", 2021, "重要"));
                break;
            case 9:
                list.add(new WarnMessage(7, "告警1", 6, "电网相线对PE短路", 2031, "重要"));
                break;
            case 8:
                list.add(new WarnMessage(8, "告警1", 7, "电网掉电", 2032, "重要"));
                break;
            case 7:
                list.add(new WarnMessage(9, "告警1", 8, "电网欠压", 2033, "重要"));
                break;
            case 6:
                list.add(new WarnMessage(10, "告警1", 9, "电网过压", 2034, "重要"));
                break;
            case 5:
                list.add(new WarnMessage(11, "告警1", 10, "电网电压不平衡", 2035, "重要"));
                break;
            case 4:
                list.add(new WarnMessage(12, "告警1", 11, "电网过频", 2036, "重要"));
                break;
            case 3:
                list.add(new WarnMessage(13, "告警1", 12, "电网欠频", 2037, "重要"));
                break;
            case 2:
                list.add(new WarnMessage(14, "告警1", 13, "电网频率不稳定", 2038, "重要"));
                break;
            case 1:
                list.add(new WarnMessage(15, "告警1", 14, "输出过流", 2039, "重要"));
                break;
            case 0:
                list.add(new WarnMessage(16, "告警1", 15, "输出电流直流分量过大", 2040, "重要"));
                break;
            default:
                list.add(new WarnMessage(17, "无告警", 0, "无告警", 0000, "无告警" ));
                break;
        }
        return list;
    }

    /**
     *对应布尔数组的a[15]--a[8] 对应bit0--bit7
     * @param i
     * @return
     */
    private static List<WarnMessage> getWarnTwoValue(int i) {
        switch(i) {
            case 15:
                listForTwo.add(new WarnMessage(1, "告警2", 0, "残余电流异常", 2051, "重要"));
                break;
            case 14:
                listForTwo.add(new WarnMessage(2, "告警2", 1, "系统接地异常", 2061, "重要"));
                break;
            case 13:
                listForTwo.add(new WarnMessage(3, "告警2", 2, "绝缘阻抗低", 2062, "重要"));
                break;
            case 12:
                listForTwo.add(new WarnMessage(4, "告警2", 3, "温度过高", 2063, "重要"));
                break;
            case 11:
                listForTwo.add(new WarnMessage(5, "告警2", 4, "设备异常", 2064, "重要"));
                break;
            case 10:
                listForTwo.add(new WarnMessage(6, "告警2", 5, "升级失败", 2065, "次要"));
            case 9:
                listForTwo.add(new WarnMessage(7, "告警2", 6, "License到期", 2066, "提示"));
                break;
            case 8:
                listForTwo.add(new WarnMessage(8, "告警2", 7, "监控单元故障", 61440, "次要"));
                break;
            default:
                list.add(new WarnMessage(17, "无告警", 0, "无告警", 0000, "无告警" ));
                break;
        }
        return listForTwo;
    }

    /*
     * 33型号警告处理
     */
    /**
     * 根据数组元素所在位置判断
     * @param i
     * @return  return会立即结束整个函数
     */
    private static List<WarnMessage> getSmaWarnOneValue(int i) {
        switch(i) {
            case 15:
                list.add(new WarnMessage(1, "告警1", 0, "软件版本不匹配", 504, "次要"));
                break;
            case 14:
                list.add(new WarnMessage(2, "告警1", 1, "软件版本不匹配", 504, "次要"));
                break;
            case 13:
                list.add(new WarnMessage(3, "告警1", 2, "系统故障", 400, "重要"));
                break;
            case 12:
                list.add(new WarnMessage(4, "告警1", 3, "系统故障", 400, "重要"));
                break;
            case 11:
                list.add(new WarnMessage(6, "告警1", 4, "逆变电路异常", 202, "重要"));
                break;
            case 9:
                list.add(new WarnMessage(7, "告警1", 6, "电网相线对PE短路", 2031, "重要"));
                break;
            case 8:
                list.add(new WarnMessage(8, "告警1", 7, "电网掉电", 2032, "重要"));
                break;
            case 7:
                list.add(new WarnMessage(9, "告警1", 8, "电网欠压", 2033, "重要"));
                break;
            case 6:
                list.add(new WarnMessage(10, "告警1", 9, "电网过压", 2034, "重要"));
                break;
            case 5:
                list.add(new WarnMessage(11, "告警1", 10, "电网电压不平衡", 2035, "重要"));
                break;
            case 4:
                list.add(new WarnMessage(12, "告警1", 11, "电网过频", 2036, "重要"));
                break;
            case 3:
                list.add(new WarnMessage(13, "告警1", 12, "电网欠频", 2037, "重要"));
                break;
            case 2:
                list.add(new WarnMessage(14, "告警1", 13, "电网频率不稳定", 2038, "重要"));
                break;
            case 1:
                list.add(new WarnMessage(15, "告警1", 14, "输出过流", 2039, "重要"));
                break;
            case 0:
                list.add(new WarnMessage(16, "告警1", 15, "输出电流直流分量过大", 2040, "重要"));
                break;
            default:
                list.add(new WarnMessage(17, "无告警", 0, "无告警", 0000, "无告警" ));
                break;
        }
        return list;
    }


    /**
     * @param message
     * @return 用于返回识别的warnMessage对象队列
     */
    private static List<WarnMessage> handleWarnOne(boolean [] message) {

        log.info(""+message.length);
        if(message.length != 16) {
            log.info("错误的输入数组长度");
        } else {
            for (int i = message.length - 1; i >= 0 ; i--) {
                if (message[i]) {
                    /*
                     * 从message[15]开始遍历
                     * 这里也可以处理其他的警告源
                     */
                    getWarnOneValue(i);
                }
            }
        }
        return list;
    }

    /**
     *
     * @param msg
     * @return 返回告警二的相应告警对象
     */
    public static List<WarnMessage> handleWarnTwo(boolean [] msg) {
        if(msg.length != 16) {
            log.info("错误的输入数组长度");
        } else {
            for (int i = msg.length - 1; i >= 0 ; i--) {
                if (msg[i]) {
                    getWarnTwoValue(i);
                }
            }
        }
        return listForTwo;
    }

    /**
     * 返回json类型的警告信息
     * @param warnMessageList
     * @param warnType
     * @param isList
     * @param ftmId
     * @return
     */
    private String getWarnJson(List<WarnMessage> warnMessageList, String warnType, boolean isList, int ftmId) {
        Result<List<WarnMessage>> result = new Result <List<WarnMessage>>(warnType, isList, ftmId, warnMessageList);
        return result.GetGsonString();
    }


    /**
     * 33型号的位反馈信息
     */




    @Test

    public  void testJson() {
        boolean [] warnOne = {false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false};
        String str = getWarnJson(handleWarnOne(warnOne),"告警一", true, 233);
        log.info(str);
    }



}
