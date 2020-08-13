package com.zfl.entity;

import com.serotonin.modbus4j.ModbusMaster;
import org.springframework.stereotype.Component;

/**
 * 主要对不同型号的逆变器的方法作了规范
 * @ClassName Inverter
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/23  10:11
 **/
@Component
public interface Inverter {

    /**
     * 获取不同逆变器的电流电压信息
     * @param master 主站
     * @param slaveId 从站Id
     * @return  有符号16位整数
     */

    /**
     * 获取ABC三相电压信息
     * @param master 主站
     * @param slaveId 从站Id
     * @return 无符号16位整数
     */
    public int [] getVoltageMessage(ModbusMaster master, int slaveId);

    /**
     * 获取AB、BC、CA线电压
     * @param master
     * @param slaveId
     * @return 无符号16位整数
     */
    public int[] getVoltageConnMessage(ModbusMaster master, int slaveId);

    /**
     * 获取电流ABC三相电流
     * @param master
     * @param slaveId
     * @return 无符号16位整数
     */
    public int [] getCurrentMessage(ModbusMaster master, int slaveId);

    /**
     * 返回功率因数
     * @param master
     * @param slaveId
     * @return 有符号16进制数
     */
//    public Integer getPowerFactor(ModbusMaster master, int slaveId);

    /**
     * 表示内部温度 short类型表示
     * @param master
     * @param slaveId
     * @return 16位有符号数
     */
    public short getInnerTemperature(ModbusMaster master, int slaveId);


    /**
     * 返回输出功率
     * 当天峰值有功功率 ： 有功功率 ： 无功功率
     * @param master
     * @param slaveId
     * @return 32位有符号整数
     */
    public Integer[] getOutputPowerMessage(ModbusMaster master, int slaveId);

    /**
     * 返回
     * 电网频率： 效率 60型号
     * 33型号  电网频率：逆变器效率
     * @param master
     * @param slaveId
     * @return  无符号16位整数 short表示
     */
    public int[] getEffectiveMessage(ModbusMaster master, int slaveId);

    /**
     * 设备状态 返回16位无符号数字符串跟十六进制数进行对比
     * TODO 要对设备状态的十六进制数进行处理
     * @param master
     * @param slaveId
     */
    public String getDeciveStatus(ModbusMaster master, int slaveId);

    /**
     * 返回输出功率
     * 60型号的为非连续寄存器地址   有功功率固定值降额 （kW）： 有功功率百分比降额（0.1%)
     * 33型号的为连续地址  有功功率控制方式（0-4） : 参数设定有功功率降额% （0-100）： 参数设定有功功率降额（KW）（0-40）：有功功率降额梯度%（0.5-10）
     * @param master
     * @param slaveId
     * @return 无符号16位整数
     */
    public int[] getActivePower(ModbusMaster master, int slaveId);


    /**
     *读无功功率 有符号16位
     * @param master
     * @param slaveId
     * @return
     */
    public short[] getReactivePower(ModbusMaster master, int slaveId);

    /**
     * 写无功功率 （PF） （Q/S）规定数组为两位，并要对数据进行校验  U16用int代替了
     * @param master
     * @param slaveId
     * @param flag
     * @return
     */
    public boolean setReactivePower(ModbusMaster master, int slaveId, short[] flag);

    /**
     * 返回绝缘阻抗值
     * @param master
     * @param slaveId
     * @return 无符号16位数
     */
    public int getResistance(ModbusMaster master, int slaveId);

    /**
     * 这里用int类型来代替U16类型的数了，只要不为负数即可，
     * TODO 有待测试
     * @param instruction 默认输入1
     * @return
     */
    public boolean startDevice(ModbusMaster master, int slaveId, int instruction);

    /**
     * 这里用int类型来代替U16类型的数了，只要不为负数即可，
     * TODO 输入的数据不能为负，统一用16进制输入 这里输入的数据有待验证
     * @param instruction
     * @return
     */
    public boolean stopDevice(ModbusMaster master, int slaveId, int instruction);

    /**
     * 输入功率  32 位有符号整数 int
     * @param master
     * @param slaveId
     * @return 返回int类型
     */
//    public int getInputPower(ModbusMaster master, int slaveId);

    /**
     * 累计发电量
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    public long getPowerGeneration(ModbusMaster master, int slaveId);


    /**
     * 系统时间
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    public long getSystemTime(ModbusMaster master, int slaveId);


    /**
     * 返回开机、关机、系统时间 累计发电量  long 单位为秒
     * @param master
     * @param slaveId
     * @return 32位无符号数
     */
    public Long[] getOffAndOnTime(ModbusMaster master, int slaveId);

    /**
     * 有功功率固定值降额   并要对数据进行校验 不支持高频写操作
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setActivePower(ModbusMaster master, int slaveId, int value);

    /**
     * 60型号为0.1%   33型号为%  额度可以自行设置
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setActivePowerPercentage(ModbusMaster master, int slaveId, int value);

    /**
     * 设置系统时间 无符号32位 long
     * @param master
     * @param slaveId
     * @param value
     */
    public void writeSystemTime(ModbusMaster master, int slaveId, long value);







}
