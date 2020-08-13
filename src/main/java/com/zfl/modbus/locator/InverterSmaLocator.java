package com.zfl.modbus.locator;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.zfl.util.ModbusUtil;
import com.zfl.entity.Inverter;
import com.zfl.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @ClassName InverterSmaLocator
 * @Description 33型号
 * @Author hello world
 * @DATE 2019/7/23  21:34
 **/
@Component
public class InverterSmaLocator implements Inverter {

    private Logger log = LoggerFactory.getLogger(InverterSmaLocator.class);

    private BaseLocator modbusLocator = null;

    private static String portName = "COM3";

    private static BaseLocator<?> locator;

    /**
     * @return the modbusLocator
     */
    private BaseLocator getModbusLocator() {
        return modbusLocator;
    }

    /**
     * Fills the BatchRead with the information of the modbusLocators
     *
     * @param batchRead the batchRead that is going to be updated
     */
    void updateBatchRead(BatchRead<String> batchRead) {
        batchRead.addLocator(portName, getModbusLocator());
    }




    /**
     * 逆变器额定容量
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    public int getInverterCapacity(ModbusMaster master, int slaveId) {
        int flag =  (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 32001, 1);
        log.info("逆变器额定容量为 ：" + flag);
        return flag;
    }

    /**
     * 输出方式
     * 0：三相四线制  1：三相三线制
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    public int getOutputMode(ModbusMaster master, int slaveId) {
        int flag =  (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 32002, 1);
        log.info("输出方式：[ 0 三相四线制   1 三相三线制 ] -- " + flag);
        return flag;
    }

    /**
     * @param master
     * @return  机型名称 String
     */
    public String getNameMessage(ModbusMaster master, int slaveId){
        String str = "";
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER,32003, DataType.VARCHAR, 16, 10);
        try {
            str =  master.getValue(locator).toString();
            log.info("设备序列号ESN为： " + str);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 系统时间
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    @Override
    public long getSystemTime(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 32200, 2);
        log.info("系统时间为 ：" + flag);
        return flag;
    }

    /**
     * 二氧化碳减排量
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    public long getCarbonReduction(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 32202, 2);
        log.info("二氧化碳减排量为 ：" + flag);
        return flag;
    }





    /**
     * PV电压 10 电流 10 增幅  ,32314,32315,32316,32317
     * @param master
     * @return  返回 pv1-pv8 的电压与电流信息  有符号的16位整数 short
     */
    public Short[] getPvElecMessage(ModbusMaster master, int slaveId) {
        int [] offset = {32262,32263,32264,32265,32266,32267,32268,32269,32270,32271,32272,32273};
        Short [] flag = {};
        try {
            flag = ModbusUtil.bathReadByShort(master, slaveId, DataType.TWO_BYTE_INT_SIGNED, offset);
            log.info("[ PV1---pv6的电压 ：电流 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 已经用返回的short[3] 转化为 int[3]
     * 返回 电网ABC相电压  16位 Unsigned int
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public int [] getVoltageMessage(ModbusMaster master, int slaveId) {
        int [] flag =  ModbusUtil.readHoldingRegisterByInt(master, slaveId, 32277, 3);
        log.info("[ 电网A相电压  电网B相电压  电网C相电压 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 已经用返回的short[3] 转化为 int[3]
     * 返回电网ABC线之间的电压  16位 Unsigned int
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public int[] getVoltageConnMessage(ModbusMaster master, int slaveId) {
        int [] offset = {32274,32275,32276};
        Integer [] flag = {};
        try {
            flag =  ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        log.info("[ 电网AB线电压  电网BC线电压  电网CA线电压 ] -- " + Arrays.toString(flag));
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 返回电网ABC相的电流 32位有符号整形int
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public int [] getCurrentMessage(ModbusMaster master, int slaveId) {
        int [] flag =  ModbusUtil.readHoldingRegisterByInt(master, slaveId, 32280, 3);
        log.info("[ 电网A相电流  电网B相电流 电网C相电流 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 功率因数 16位short
     * @param master
     * @param slaveId
     * @return
     */
    public Short getPowerFactor(ModbusMaster master, int slaveId) {
        short flag =  (Short) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_SIGNED, 32284, 1);
        log.info("功率因数为 ： " + flag);
        return flag;

    }





    /**
     * 返回 电网频率： 效率 无符号16位整数 short表示
     * @param master
     * @param slaveId
     * @return , 32285
     */
    @Override
    public int[] getEffectiveMessage(ModbusMaster master, int slaveId){
        int [] offset = {32283, 32285};
        Integer [] flag = {};
        try {
            flag =  ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        log.info("[ 电网频率： 逆变器效率] -- " + Arrays.toString(flag));
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 16位有符号数 表示内部温度 short类型表示
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public short getInnerTemperature(ModbusMaster master, int slaveId) {
        short flag = 0;
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32286, DataType.TWO_BYTE_INT_SIGNED, 16, 1);
        try {
            //自动装包、拆包
            flag = (Short) master.getValue(locator);
            log.info("内部温度 ：" + flag);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        if (flag != 0) {
            return flag;
        } else {
            log.error("未读到内部温度相应数据 默认返回零");
            return flag;
        }
    }

    /**
     * 设备状态 返回16位无符号数跟十六进制数进行对比
     * @param master
     * @param slaveId
     */
    @Override
    public String getDeciveStatus(ModbusMaster master, int slaveId) {
        int flag = 0;
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32287, DataType.TWO_BYTE_INT_UNSIGNED, 16, 1);
        try {
            flag = (Integer) master.getValue(locator);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        String str = Integer.toHexString(flag);
        log.info("逆变器状态 ：" + str);
        return str;
    }

    /**
     * 返回输出功率 有符号32位整数
     * 当天峰值有功功率 ： 有功功率 ： 无功功率
     * @param master
     * @param slaveId
     * @return  ,32290,32292
     */
    @Override
    public Integer[] getOutputPowerMessage(ModbusMaster master, int slaveId){
        int [] offset = {32288, 32290, 32292};
        Integer[] flag = {};
        try {
            flag =  ModbusUtil.bathReadByInt(master, slaveId, DataType.FOUR_BYTE_INT_SIGNED, offset);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        log.info("[ 当天峰值有功功率 ： 有功功率 ： 无功功率 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 输入总功率
     * @param master
     * @param slaveId
     * @return 32位无符号数
     */
    public long getInputTotalPower(ModbusMaster master, int slaveId) {
        long flag = (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED,32294, 2);
        log.info("输入总功率为 ：" + flag);
        return flag;
    }




    /**
     * 累计总发电量
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    @Override
    public long getPowerGeneration(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 32306, 2);
        log.info("累计发电量为 ：" + flag);
        return flag;
    }

    /**
     * 当前发电量统计时间 ： 当前小时发电量 ：当前日发电量 ：当前月发电量 : 当前年发电量 : 总发电量
     * @param master
     * @param slaveId
     * @return 无符号32位整数的数组
     */
    public Long[] getNowTotalGeneration(ModbusMaster master, int slaveId) {
        int [] offset = {32296,32298,32300,32302,32304,32306};
        Long [] flag = {};
        try {
            flag = ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 当前发电量统计时间 ： 当前小时发电量 ：当前日发电量 ：当前月发电量 : 当前年发电量 : 总发电量 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 读取闭锁状态
     * 0:闭锁  1:非闭锁
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getBlockedState(ModbusMaster master, int slaveId) {
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 32320, 1);
        log.info("闭锁状态为 ：" + flag);
        return flag;
    }

    /**
     * 零电压穿越保护状态 : 低电压穿越保护状态 : 孤岛效应保护状态
     * 这种表达效果不好的话再去用batchRead获取布尔值的某一个的方式表达
     * @param master
     * @param slaveId
     * @return bit位表达信息
     */
    public boolean [] getProtectState(ModbusMaster master, int slaveId) {
        log.info("[ 孤岛效应保护状态 2 : 低电压穿越保护状态 1 : 零电压穿越保护状态 0 ]  " );
        boolean [] flag = ModbusUtil.readHoldingRegisterByBit(master, slaveId,32321,1);
        return flag;

    }

    /**
     * 逆变并网状态
     * 00：离网  01：并网
     * @param master
     * @param slaveId
     * @return
     */
    public int invertGridState(ModbusMaster master, int slaveId) {
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED,32322, 1);
        log.info("[ 逆变并网状态 ： 00 离网  01 并网 ] -- " + flag);
        return flag;
    }

    /**
     * 绝缘阻抗值
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    @Override
    public int getResistance(ModbusMaster master, int slaveId) {
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 32323, 1 );
        log.info("绝缘阻抗值为 ：" + flag);
        return flag;
    }

    /**
     * MPPT输入总功率 (1-4)
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    public long[] getMpptInputPower(ModbusMaster master, int slaveId) {
        long [] flag = ModbusUtil.readHoldingRegisterByLong(master, slaveId,33022,6);
        log.info("[ MPPT1输入总功率 ： MPPT2输入总功率 ： MPPT3输入总功率 : MPPT4输入总功率 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 可读可写
     */

    /**
     * 日期时间同步
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    public long getDateSynch(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 40000, 2);
        log.info("日期时间同步 ：" + flag);
        return flag;
    }

    /**
     * 设置系统时间 无符号32位 long
     * @param master
     * @param slaveId
     * @param value
     */
    @Override
    public void writeSystemTime(ModbusMaster master, int slaveId, long value) {
        long lower = 1325376000;
        if ( value >= lower )  {
            locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 40000, DataType.FOUR_BYTE_INT_UNSIGNED, 16, 2);
            try {
                master.setValue(locator, value);
                log.info("（33型号）日期时间同步写入成功 ");
            } catch (ModbusTransportException | ErrorResponseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 无功功率补偿方式(0-6)
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    public int getReactivePowerMode(ModbusMaster master, int slaveId) {
        int flag =  (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 40117, 1);
        if (flag < 0 || flag > 6 ) {
            log.info("读出无功功率补偿方式不在合法范围内");
        }
        log.info("无功功率补偿方式为 ：" + flag);
        return flag;
    }


    /**
     * 读有功功率  非连续寄存器地址
     * 33型号的为连续地址  有功功率控制方式（0-4） : 参数设定有功功率降额% （0-100）：
     *                   参数设定有功功率降额（KW）（0-40）：有功功率降额梯度%（0.5-10）
     * @param master 无符号16位 int表示
     * @param slaveId
     * @return
     */
    @Override
    public int[] getActivePower(ModbusMaster master, int slaveId){
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 100;
        int [] flag = {};
        flag = ModbusUtil.readHoldingRegisterByInt(master, slaveId, 40118, 4);
        if( flag[0] < 0 || flag[1] > 4) {
            log.debug("有功功率控制方式（0-4）");
        } else if ( flag[1] < 0 || flag[1] > pmax) {
            log.debug("参数设定有功功率降额% （0-100）");
        } else if ( flag[2] < 0 || flag[2] > 40) {
            log.debug(" 参数设定有功功率降额（KW）（0-40）");
        } else if ( flag[3] < 0.5 || flag[3] > 10) {
            log.debug("有功功率降额梯度%（0.5-10）");
        }
        log.info("[ 有功功率控制方式（0-4） : 参数设定有功功率降额% （0-100）： 参数设定有功功率降额（KW）（0-40）：有功功率降额梯度%（0.5-10）] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 读无功功率 有符号16位
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public short[] getReactivePower(ModbusMaster master, int slaveId) {
        //TODO 要对数据范围进行处理
        short [] flag =  ModbusUtil.readHoldingRegister(master, slaveId, 40122, 2);
        if (flag[0] <= -1 || ( flag[0] < 0.8 && flag[1] > -0.8 )
                || flag[0] > 1) {
            log.debug("读无功功率补偿（PF）超出允许范围");
        } else if (flag [1] <= -1 || flag[1] > 1) {
            log.debug("读无功功率补偿（Q/S）超出允许范围");
        }
        log.info(" [ 无功功率补偿（PF） ： 无功功率补偿（Q/S）] -- " + Arrays.toString(flag));
        return flag;
    }


    /**
     * 有功功率控制方式 并要对数据进行校验
     * 不支持高频写操作 [0-6]
     * @param master
     * @param slaveId
     * @param value 使用int代替无符号16进制数
     * @return
     */
    public boolean setActiveControl(ModbusMaster master, int slaveId, int value) {
        int offset = 40118;
        int pmax = 4;
        if (value >= 0 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）有功功率控制方式写入成功");
            return true;
        } else {
            log.info("有功功率控制方式输入数据范围非法");
            return false;
        }
    }

    /**
     * 有功功率固定值降额   并要对数据进行校验
     * 不支持高频写操作 [0-40]
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    @Override
    public boolean setActivePower(ModbusMaster master, int slaveId, int value) {
        int offset = 40120;
        int pmax = 40;
        if (value >= 0 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）参数设定有功功率降额（KW）（0-40）：有功功率降额梯度%（0.5-10）写入成功");
            return true;
        } else {
            log.info("有功功率固定值降额输入数据范围非法");
            return false;
        }
    }

    /**
     * 有功功率固定值降额   并要对数据进行校验
     * 不存储,支持高频写操作 [0-28]
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setActivePowerHigh(ModbusMaster master, int slaveId, int value) {
        int offset = 40235;
        int pmax = 28;
        if (value >= 0 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）有功功率固定值降额(支持高频写)写入成功");
            return true;
        } else {
            log.info("有功功率固定值降额(支持高频写)输入数据范围非法");
            return false;
        }
    }


    /**
     * 33型号有功功率百分比降额(0.1%)
     * 存储，不支持高频写操作
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    @Override
    public boolean setActivePowerPercentage(ModbusMaster master, int slaveId, int value) {
        int offset = 40119;
        //high代表最大百分比
        int high = 100;
        if (value >= 0 && value <= high) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）有功功率百分比降额(%)写入成功");
            return true;
        } else {
            log.info("有功功率百分比降额(%)输入数据范围非法");
            return false;
        }
    }

    /**
     * 33型号有功功率百分比降额(%)
     * 不存储，支持高频写操作 [0-100]
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setActivePercentageHigh(ModbusMaster master, int slaveId, int value) {
        int offset = 40234;
        //high代表最大百分比
        int high = 100;
        if (value >= 0 && value <= high) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）有功功率百分比降额(%)写入成功--不存储，支持高频写操作：");
            return true;
        } else {
            log.info("输入数据范围非法");
            return false;
        }
    }






    /**
     * 有功功率降额梯度  存储，不支持高频写操作
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setActivePerGradient(ModbusMaster master, int slaveId, int value) {
        int offset = 40121;
        //high代表最大百分比
        int high = 10;
        if (value >= 0.5 && value <= 10) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）有功功率降额梯度写入成功");
            return true;
        } else {
            log.info("（33型号）有功功率降额梯度输入数据范围非法");
            return false;
        }
    }





    /**
     * 读无功功率调整时间
     * @param master
     * @param slaveId
     * @return
     */
    public int getReactivePowerTime(ModbusMaster master, int slaveId) {
        int flag =  (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 40124, 1);
        if (flag < 5 || flag > 120 ) {
            log.info("读出无功功率调整时间不在合法范围内");
        }
        log.info("无功功率调整时间为 ：" + flag);
        return flag;
    }




    /**
     * 写无功功率 （PF） （Q/S）规定数组为两位，并要对数据进行校验  U16用int代替了
     * @param master
     * @param slaveId
     * @param flag pf在先 QS在后
     * @return
     */
    @Override
    public boolean setReactivePower(ModbusMaster master, int slaveId, short [] flag) {
        int offset = 40122;
        if (flag[0] <= -1 || ( flag[0] < 0.8 && flag[1] > -0.8 )
                || flag[0] > 1) {
            log.debug("参数设定无功功率补偿（PF）超出允许范围");
            return false;
        } else if (flag [1] < -0.6 || flag[1] > 0.6) {
            log.debug("参数设定无功功率补偿（Q/S）超出允许范围");
            return false;
        } else {
            ModbusUtil.writeHoldingRegisters(master, slaveId, offset, flag);
            log.info("参数设定无功功率补偿成功");
            return true;
        }
    }

    /**
     * 写无功功率 （PF） （Q/S）规定数组为两位，并要对数据进行校验  U16用int代替了
     * @param master
     * @param slaveId
     * @param flag QS在先 PF在后
     * @return
     */
    public boolean setReactiveHigh(ModbusMaster master, int slaveId, short [] flag) {
        int offset = 40235;
        if (flag[1] <= -1 || ( flag[1] < 0.8 && flag[1] > -0.8 )
                || flag[1] > 1) {
            log.debug("参数设定无功功率补偿（PF）超出允许范围");
            return false;
        } else if (flag [0] <= -1 || flag[0] > 1) {
            log.debug("参数设定无功功率补偿（Q/S）超出允许范围");
            return false;
        } else {
            ModbusUtil.writeHoldingRegisters(master, slaveId, offset, flag);
            log.info("参数设定无功功率（支持高频写）补偿成功");
            return true;
        }
    }





    /**
     * 无功功率补偿方式 并要对数据进行校验
     * 不支持高频写操作 [0-6]
     * @param master
     * @param slaveId
     * @param value 使用int代替无符号16进制数
     * @return
     */
    public boolean setReActiveCompensate(ModbusMaster master, int slaveId, int value) {
        int offset = 40117;
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 6;
        if (value >= 0 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）无功功率补偿方式写入成功");
            return true;
        } else {
            log.info("输入数据范围非法");
            return false;
        }
    }

    /**
     * 有功功率固定值降额   并要对数据进行校验
     * 不支持高频写操作 [5-120]
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setReActiveAdjustTime(ModbusMaster master, int slaveId, int value) {
        int offset = 40124;
        int pmax = 120;
        if (value >= 5 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("（33型号）无功功率调整时间写入成功");
            return true;
        } else {
            log.info("输入数据范围非法");
            return false;
        }
    }

    /**
     * cosψ-P/Pn特征曲线设置
     * @param master
     * @param slaveId
     * @return
     */
    public int [] cosCurve(ModbusMaster master, int slaveId) {
        int [] flag = ModbusUtil.readHoldingRegisterByInt(master, slaveId, 40133, 21);
        log.info("[ cosψ-P/Pn特征曲线设置 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 这里用int类型来代替U16类型的数了，只要不为负数即可，
     * TODO 输入的数据不能为负，统一用16进制输入 这里输入的数据有待验证
     * @param instruction
     * @return
     */
    @Override
    public boolean startDevice(ModbusMaster master, int slaveId, int instruction) {
        int offset = 40200;
        locator = BaseLocator.holdingRegister(slaveId, offset, instruction);
        if (instruction != 0) {
            log.debug("输入数据必须为0");
            return false;
        } else {
            try {
                master.setValue(locator , instruction);
            } catch (ModbusTransportException | ErrorResponseException e) {
                log.debug(e.toString());
            }
            log.info("设备已打开成功");
            return true;
        }
    }

    /**
     * 这里用int类型来代替U16类型的数了，只要不为负数即可，
     * TODO 输入的数据不能为负，统一用16进制输入 这里输入的数据有待验证
     * @param instruction
     * @return
     */
    @Override
    public boolean stopDevice(ModbusMaster master, int slaveId, int instruction) {
        int offset = 40201;
        locator = BaseLocator.holdingRegister(slaveId, offset, instruction);
        if (instruction != 0) {
            log.debug("输入数据必须为0");
            return false;
        } else {
            try {
                master.setValue(locator, instruction);
            } catch (ModbusTransportException | ErrorResponseException e) {
                log.debug(e.toString());
            }
            log.info("设备已成功关闭");
            return true;
        }
    }

    /**
     * Q-U特征曲线设置
     * @param master
     * @param slaveId
     * @return
     */
    public int [] quCurve(ModbusMaster master, int slaveId) {
        int [] flag = ModbusUtil.readHoldingRegisterByInt(master, slaveId, 40154, 21);
        log.info("[ Q-U特征曲线设置 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 电网一级过压保护时间 : 电网二级过压保护时间 : 电网一级欠压保护时间 : 电网二级欠压保护时间 : 电网一级过频保护时间 : 电网二级过频保护时间 : 电网一级欠频保护时间 : 电网二级欠频保护时间 : 十分钟过压保护时间
     * @param master [50,600000]
     * @param slaveId
     * @return
     */
    public Long[] getPowerProtectTime(ModbusMaster master, int slaveId){
        int [] offset = {42045,42047,42049,42051,42053,42055,42057,42059,42061};
        Long [] flag = {};
        try {
            flag = ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 电网一级过压保护时间 : 电网二级过压保护时间 : 电网一级欠压保护时间 : 电网二级欠压保护时间 : 电网一级过频保护时间 : 电网二级过频保护时间 : 电网一级欠频保护时间 : 电网二级欠频保护时间 : 十分钟过压保护时间 ] --" + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 电网一级过压保护点 : 电网二级过压保护点 : 电网一级欠压保护点 :
     * 电网二级欠压保护点 : 电网一级过频保护点 : 电网二级过频保护时间 : 电网一级欠频保护时间 : 电网二级欠频保护时间 : 十分钟过压保护时间
     * @param master [50,600000]
     * @param slaveId
     * @return
     */
    public long[] getPowerProtectPoint(ModbusMaster master, int slaveId){
        int [] offset = {42063,42064,42065,42066,42067,42068,42069,42070,42071};
        Long [] flag = {};
        try {
            flag= (Long []) ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 电网一级过压保护点 : 电网二级过压保护点 : 电网一级欠压保护点 : 电网二级欠压保护点 : 电网一级过频保护点 : 电网二级过频保护点 : 电网一级欠频保护点 : 电网二级欠频保护点 : 十分钟过压保护点 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return NumberUtil.longParse(flag);
    }

    /**
     * 电网标准码
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getGridStandardCode(ModbusMaster master, int slaveId) {
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42072, 1);
        if( flag < 0 || flag > 40) {
            log.info("错误的电网标准码范围");
        }
        log.info("电网标准码为： " + flag);
        return flag ;
    }


    /**
     * 电网标准码
     * @param master
     * @param slaveId
     * @param value
     */
    public void setGridStandardCode(ModbusMaster master, int slaveId, int value) {
        int low = 0;
        int high = 40;
        if(value < low || value > high) {
            log.info("写入电网标准码范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42072, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入电网标准码范围成功!");
        }
    }

    /**
     * 绝缘阻抗ISO保护点
     * TODO 写入没写
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getResistencePoint(ModbusMaster master, int slaveId) {
        double low = 0.033;
        int high =1;
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42074, 1);
        if( flag < low || flag > high) {
            log.info("绝缘阻抗ISO保护点范围错误!");
        }
        log.info("绝缘阻抗ISO保护点为： " + flag);
        return flag ;
    }

    /**
     * 绝缘阻抗ISO保护点
     * @param master
     * @param slaveId
     * @param value
     */
    public void setResistencePoint(ModbusMaster master, int slaveId, int value) {
        double low = 0.033;
        int high = 1;
        if(value < low || value > high) {
            log.info("写入绝缘阻抗ISO保护点范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42074, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入绝缘阻抗ISO保护点范围成功!");
        }
    }

    /**
     * 电网电压不平衡度保护点
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getVoltageProPoint(ModbusMaster master, int slaveId) {
        int low = 0;
        int high =50;
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42075, 1);
        if( flag < low || flag > high) {
            log.info("电网电压不平衡度保护点范围错误");
        }
        log.info("电网电压不平衡度保护点为： " + flag);
        return flag ;
    }


    /**
     * 电网电压不平衡度保护点
     * @param master
     * @param slaveId
     * @param value
     */
    public void setVoltageProPoint(ModbusMaster master, int slaveId, int value) {
        int low = 0;
        int high = 50;
        if(value < low || value > high) {
            log.info("写入电网电压不平衡度保护点错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42075, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入电网电压不平衡度保护点成功!");
        }
    }



    /**
     * 电网故障开机软启时间 sec秒
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getGridRestartTime(ModbusMaster master, int slaveId) {
        int low = 20;
        int high = 800;
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42083, 1);
        if( flag < low || flag > high) {
            log.info("电网故障开机软启时间范围错误");
        }
        log.info("电网故障开机软启时间为： " + flag);
        return flag ;
    }

    /**
     * 电网故障开机软启时间
     * @param master
     * @param slaveId
     * @param value
     */
    public void setGridFaultRestart(ModbusMaster master, int slaveId, int value) {
        int low = 20;
        int high = 800;
        if(value < low || value > high) {
            log.info("写入电网电压不平衡度保护点范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42083, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入电网电压不平衡度保护点范围成功!");
        }
    }

    /**
     * 低电压穿越使能设置
     * @param master
     * @param slaveId
     * @param value
     */
    public void setLowVoltageTran(ModbusMaster master, int slaveId, int value) {
        if(value != 0 || value != 1) {
            log.info("写入低电压穿越使能设置范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42084, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入低电压穿越使能设置范围成功!");
        }
    }

    /**
     * 孤岛检测使能设置
     * @param master
     * @param slaveId
     * @param value
     */
    public void setIslandDetection(ModbusMaster master, int slaveId, int value) {
        if(value != 0 || value != 1) {
            log.info("写入孤岛检测使能设置范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42087, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入孤岛检测使能设置范围成功!");
        }
    }


    /**
     * LVRT无功补偿因子
     * @param master
     * @param slaveId
     * @param value
     */
    public void setLvrt(ModbusMaster master, int slaveId, int value) {
        if((value != 0) || ( value != 1)) {
            log.info("写入LVRT无功补偿因子范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42089, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入LVRT无功补偿因子范围成功!");
        }
    }



    /**
     * 开机软启时间 sec秒
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getStartTime(ModbusMaster master, int slaveId) {
        int low = 20;
        int high = 800;
        int data = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42085, 1);
        if( data < low || data > high) {
            log.info("开机软启时间范围错误");
        }
        log.info("开机软启时间为 ：" + data);
        return data ;
    }


    /**
     * 开机软启时间
     * @param master
     * @param slaveId
     * @param value
     */
    public void setStartTime(ModbusMaster master, int slaveId, int value) {
        int low = 20;
        int high = 800;
        if(value < low || value > high) {
            log.info("写入开机软启时间范围错误!");
        } else {
            ModbusUtil.writeValue(master, slaveId, 42085, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("写入开机软启时间范围成功!");
        }
    }

    /**
     * 电网故障恢复并网时间 sec秒
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int getGridRecoveryTime(ModbusMaster master, int slaveId) {
        int low = 10;
        int high = 600;
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 42086, 1);
        if( flag < low || flag > high) {
            log.info("电网故障开机软启时间范围错误");
        }
        log.info("电网故障开机软启时间为 ：" + flag);
        return flag ;
    }


    /**
     * 低电压穿越使能设置 : 孤岛检测使能设置 ：LVRT无功补偿因子
     * @param master
     * @param slaveId
     * @return
     */
    public int[] getSet(ModbusMaster master, int slaveId){
        int [] offset = {42084,42087,42089};
        Integer [] flag = {};
        try {
            flag = ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
            log.info("[ 低电压穿越使能设置 (0,1): 孤岛检测使能设置 (0,1) ：LVRT无功补偿因子(0,5) ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 无功补偿（cosψ-P）触发电压
     : 无功补偿（cosψ-P）退出电压
     * @param master
     * @param slaveId
     * @return
     */
    public int[] getReactiveCompensate(ModbusMaster master, int slaveId){
        int [] offset = {42090,42091};
        Integer [] flag = {};
        try {
            flag = ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
            log.info("[ 无功补偿（cosψ-P）触发电压 (100,110) : 无功补偿（cosψ-P）退出电压 (90,100) ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 无功补偿（cosψ-P）触发电压
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setReactiveVoltage(ModbusMaster master, int slaveId, int flag) {
        int low = 100;
        int high = 110;
        if( flag < low || flag > high) {
            log.info("无功补偿（cosψ-P）触发电压范围错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42090, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("无功补偿（cosψ-P）触发电压成功 ");
        return flag ;
    }

    /**
     * 无功补偿（cosψ-P）退出电压
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setQuitVoltage(ModbusMaster master, int slaveId, int flag) {
        int low = 90;
        int high = 100;
        if( flag < low || flag > high) {
            log.info("无功补偿（cosψ-P）退出电压范围错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42091, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("无功补偿（cosψ-P）退出电压成功 ");
        return flag ;
    }

    /**
     * 过频降额触发频率 : 过频降额退出频率 ： 过频降额恢复频率
     * @param master
     * @param slaveId
     * @return
     */
    public short[] getOverFrequency(ModbusMaster master, int slaveId) {
        short[] flag = ModbusUtil.readHoldingRegister(master, slaveId, 42092, 3);
        log.info("[ 过频降额触发频率(50,52) : 过频降额退出频率(49.9,51) ： 过频降额恢复频率(5,20) ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 设置 过频降额触发频率(50,52) : 过频降额退出频率(49.9,51) ： 过频降额恢复频率(5,20)
     * @param master
     * @param slaveId
     * @param data
     * @return
     */
    public boolean setOverFrequency(ModbusMaster master, int slaveId, Integer [] data) {
        if (data.length != 3) {
            log.debug("数组长度为三");
            return false;
        }
        if (data[0] >= 50 && data[0] <= 52) {
            if(data[1] <= 51 && data[1] >= 49.9) {
                if (data[2] >= 5 && data[2] <= 20) {
                    ModbusUtil.writeValue(master, slaveId, 42092, DataType.TWO_BYTE_INT_UNSIGNED, data);
                }
            }
        }
        return  true;
    }

    /**
     * Q-U特征曲线模式
     * @param master
     * @param slaveId
     * @return 16位无符号数 0 or 1
     */
    public int setCurveMode(ModbusMaster master, int slaveId, int flag) {
        int high = 1;
        if( flag != 0 ) {
            if (flag != high) {
                log.info("Q-U特征曲线模式范围错误");
            }
        }
        ModbusUtil.writeValue(master, slaveId, 42095, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("写入Q-U特征曲线模式成功 ");
        return flag ;
    }




    /**
     * Q-U特征曲线模式 : Q-U调度触发功率百分比
     * @param master
     * @param slaveId
     * @return
     */
    public short[] getCurveMode(ModbusMaster master, int slaveId) {
        short [] flag = ModbusUtil.readHoldingRegister(master, slaveId, 42095, 2);
        log.info("[ Q-U特征曲线模式 (0,1): Q-U调度触发功率百分比(10,100) ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * Q-U调度触发功率百分比
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setQuitPercentage(ModbusMaster master, int slaveId, int flag, int value) {
        int low = 10;
        int high = 100;
        if( flag < low || flag > high) {
            log.info("Q-U调度触发功率百分比错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42096, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("Q-U调度触发功率百分比成功 ");
        return flag ;
    }


    /**
     * MPPT多峰扫描使能 [0:禁能-1：使能]: MPPT扫描间隔时间[5,30]
     * @param master
     * @param slaveId
     * @return
     */
    public short[] getMpttScan(ModbusMaster master, int slaveId) {
        short [] flag = ModbusUtil.readHoldingRegister(master, slaveId, 42097, 2);
        log.info("[ MPPT多峰扫描使能 (0禁能  1使能) : MPPT扫描间隔时间(5,30) ] -- " + Arrays.toString(flag));
         return flag;
    }

    /**
     * MPPT多峰扫描使能 (0禁能  1使能)
     * @param master
     * @param slaveId
     * @return 16位无符号数 0 or 1
     */
    public int setMpttScan(ModbusMaster master, int slaveId, int flag) {
        int high = 1;
        if( flag != 0 ) {
            if (flag != high) {
                log.info("MPPT多峰扫描使能错误");
            }
        }
        ModbusUtil.writeValue(master, slaveId, 42097, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("写入MPPT多峰扫描使能成功 ");
        return flag ;
    }

    /**
     * MPPT扫描间隔时间(5,30)
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setMpttTime(ModbusMaster master, int slaveId, int flag, int value) {
        int low = 5;
        int high = 30;
        if( flag < low || flag > high) {
            log.info("写入MPPT扫描间隔时间错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42101, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("写入MPPT扫描间隔时间成功 ");
        return flag ;
    }



    /**
     * 年月日时分秒
     * TODO 要判断范围
     * @param master
     * @param slaveId
     * @return
     */
    public short[] systemTimePart(ModbusMaster master, int slaveId) {
        short [] flag = ModbusUtil.readHoldingRegister(master, slaveId, 42300, 6);
        log.info("[ 年 (2000,2069) : 月 (1,12) : 日 (1,31) : 时 (0,23) : 分 (0,59) : 秒 (0,59) ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 16位无符号数
     * @param master
     * @param slaveId
     * @param data 年月日时分秒
     */
    public void setPartTime(ModbusMaster master, int slaveId,Integer [] data) {
        ModbusUtil.writeValue(master, slaveId, 42300, DataType.TWO_BYTE_INT_UNSIGNED, data);
    }


    /**
     * 有功功率补偿 由于范围有限制能用short完全表达
     * @param master
     * @param slaveId
     * @return 无符号16进制数 [0,100]
     */
    public short[] getActiveDerating(ModbusMaster master, int slaveId) {
        short [] flag = ModbusUtil.readHoldingRegister(master, slaveId, 42320, 1);
        log.info("有功功率补偿（%） (0,100)为 ：" + Arrays.toString(flag));
        return flag;
    }

    /**
     * 有功功率降额（%） (0,100)
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setActiveDerating(ModbusMaster master, int slaveId, int flag, int value) {
        int low = 0;
        int high = 100;
        if( flag < low || flag > high) {
            log.info("写入有功功率降额错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42320, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("写入有功功率降额成功 ");
        return flag ;
    }

    /**
     * 无功功率补偿
     * @param master
     * @param slaveId
     * @return 有符号16进制数  (-1,-0.8]U [0.8,1)
     */
    public Short getReActiveDerating(ModbusMaster master, int slaveId) {
        short flag = (Short) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_SIGNED, 42321, 1);
        log.info("无功功率补偿（%）为 (-1,-0.8]U [0.8,1)：" + flag);
        return flag;
    }

    /**
     * 无功功率补偿（%）为 (-1,-0.8]U [0.8,1)
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    public int setReActiveDerating(ModbusMaster master, int slaveId, int flag, int value) {
        if(( flag <= -1 && flag > 0.8) || ( flag < 0.8 && flag > 1)) {
            log.info("写入有功功率降额错误");
        }
        ModbusUtil.writeValue(master, slaveId, 42321, DataType.TWO_BYTE_INT_UNSIGNED, flag);
        log.info("写入有功功率降额成功 ");
        return flag ;
    }

    /**
     * 返回开机、关机 32位无符号数 long
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public Long[] getOffAndOnTime(ModbusMaster master, int slaveId){
        int [] offset = {32325,32327};
        Long [] flag = {};
        try {
            flag =  ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 逆变器开机时间 ： 逆变器关机时间 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 前一小时发电量统计时间 ： 前一小时发电量 ：前一日发电量统计时间 ：前一日发电量 :
     * 前一月发电量统计时间 : 前一月发电量 : 前一年发电量统计时间 ： 前一年发电量
     * @param master
     * @param slaveId
     * @return
     */
    public Long[] getFrontTotalGeneration(ModbusMaster master, int slaveId) {
        int [] offset = {32343,32345,32347,32349,32351,32353,32355,32357};
        Long [] flag = {};
        try {
            flag = ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 前一小时发电量统计时间 ： 前一小时发电量 ：前一日发电量统计时间 ：前一日发电量 : 前一月发电量统计时间 : 前一月发电量 : 前一年发电量统计时间 ： 前一年发电量 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }




    /**
     * 告警信息0
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnZero(ModbusMaster master, int slaveId) {
        log.info("告警零： [ 13 Flash故障（次要） 12 升级失败（重要） 10 软件版本不匹配（次要）]l");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50000, 1);
    }

    /**
     * 告警信息1
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnOne(ModbusMaster master, int slaveId) {
        log.info("告警一：[ 14 系统故障（重要） 12 SPI通讯异常（重要） 11 风扇故障（次要） 10 系统故障（重要）"+
                " 8 温度过高（重要） 7 残余电流异常（重要） 6 逆变电路异常（重要） 4 系统故障（重要） 3 系统故障 （重要） 2 软件版本不匹配（次要） 1 软件版本不匹配（次要） ]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50001, 1);
    }

    /**
     * 告警信息2
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnTwo(ModbusMaster master, int slaveId) {
        log.info("告警二：[ 15 系统故障（重要） 14 直流电弧故障（重要） 13 直流电弧故障（重要） 12 直流电弧故障（重要） "+
                " 9 组串3反向（重要） 8 系统故障（重要） 7 系统故障（重要） 4 AFCI自检失败（重要） 3 AFCI自检失败 （重要） 2 AFCI自检失败（重要） 1 AFCI自检失败（重要） 0 绝缘阻抗低（重要） ]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50002, 1);
    }

    /**
     * 告警信息3
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnThree(ModbusMaster master, int slaveId) {
        log.info("告警三：[ 15 直流电路异常（重要） 12 DC输入电压高（重要） 11 DC输入电压高 10 DC输入电压高（重要）"+
                " 9 DC输入电压高（重要） 8 组串6反向（重要） 7 组串5反向（重要） 6 组串4反向（重要） 3 直流电路异常 （重要） 2 组串2反向（重要） 1 组串1反向（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50003, 1);
    }

    /**
     * 告警信息4
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnFour(ModbusMaster master, int slaveId) {
        log.info("告警四：[ 15 组串6反向（提示） 14 组串5反向（提示） 13 组串4反向（提示） 12 组串3反向（提示）7 组串8反向（提示） 6 组串8反向（重要） 5 组串7反向（提示） 4 组串7反向（重要） 3 组串2反向 （提示） 2 组串1反向（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50004, 1);
    }

    /**
     * 告警信息5
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnFive(ModbusMaster master, int slaveId) {
        log.info("告警五：[ 6 直流电路异常（提示） 5 直流电路异常（提示） 4 直流电路异常（提示）3 辅助电源异常 （重要） 2 直流电路异常（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50005, 1);
    }

    /**
     * 告警信息6
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnSix(ModbusMaster master, int slaveId) {
        log.info("告警六：[ 6 直流电路异常（重要） 5 直流电路异常（重要） 4 直流电路异常（提示）2 辅助电源异常 （重要） 1 直流电路异常（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50006, 1);
    }

    /**
     * 告警信息7
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnSeven(ModbusMaster master, int slaveId) {
        log.info("告警七：[ 12 逆变电路异常（重要） 10 逆变电路异常（重要） 6 系统故障（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50007, 1);
    }

    /**
     * 告警信息8
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnEight(ModbusMaster master, int slaveId) {
        log.info("告警八：[ 12 电网频率异常（重要） 11 电网电压异常（重要） 10 接地异常（重要） 9 电网电压异常（重要）"+
                " 8 电网电压异常（重要） 7 电网频率异常（重要） 6 电网频率异常（重要） 4 系统故障（重要） 3 电网电压异常 （重要） 0 电网电压异常（次要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50008, 1);
    }

    /**
     * 告警信息9
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnNine(ModbusMaster master, int slaveId) {
        log.info("告警九：[ 8 电网电压异常（重要） 7 电网电压异常（重要） 6 电网电压异常（重要） 0 电网电压异常（次要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50009, 1);
    }

    /**
     * 告警信息16
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnSixteen(ModbusMaster master, int slaveId) {
        log.info("告警十六：[ 7 组串8异常（提示） 6 组串7异常（提示） 5 组串6异常（提示） 4 组串5异常 （提示） 3 组串4异常（提示） 2 组串3异常（提示）1 组串2异常（提示）0 组串1异常（提示）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 50016, 1);
    }























}
