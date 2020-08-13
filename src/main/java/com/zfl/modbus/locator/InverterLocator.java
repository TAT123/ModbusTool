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
 * @ClassName InverterLocator
 * @Description 这个类使用Modbus4j，包括ModbusLocator以及扩展了pv设备的相关特性功能
 * The locators are the elements that are used to configure the R/W on the modbus to expose that
 * @Author hello world
 * @DATE 2019/7/9  11:02
 */
@Component
public class InverterLocator implements Inverter {

    private Logger log = LoggerFactory.getLogger(InverterLocator.class);
    private BaseLocator modbusLocator = null;
    private static String portName = "COM3";

    /**
     * 数据编码采用big-Endian 大端法 低位字节在高位地址，高位字节在低位地址 比如说在寄存器中存储方式为0X1234 读出时应该为12 34
     */
    private static String characterEncoding;
    /**
     * 用于以后对数据运算的扩展  乘数  加数
     */
//    private final Double multiplier;
//    private final Double additive;
//    private final String eventName;
//    private final String objectName;
//    private final String objectClass;
//    private final String objectBehavior;
    private Object lastValueReaded;


    private static BaseLocator<?> locator;



    public static BaseLocator<Boolean> booleanBaseLocator;



//    /**
//     * 这里的configuration会将基本配置
//     * @param configuration  初始化基本配置
//     * @param i 用于进行元组的分类
//     */
//    public InverterLocator(Config configuration, int i) {
//
//        if(i < 0) {
//            portName = configuration.getStringProperty("PortName", "COM3");
//            slaveId = configuration.getIntProperty("SlaveId", 1);
//            registerRange = parseRegisterRange(configuration.getStringProperty("registerRange", "HOLDING_REGISTER"));
//            dataType = parseDataType(configuration.getStringProperty("DataType", "TWO_BYTE_INT_UNSIGNED"));
//            offset = configuration.getIntProperty("Offset", 0);
//
//            //protocol read link
//            objectName = configuration.getStringProperty("objectName", "");
//            objectClass = configuration.getStringProperty("objectClass", "");
//            objectBehavior = configuration.getStringProperty("objectBehavior", "");
//            //必须扩展Modbus4j功能以允许读取和组合来自同一个寄存器的几个“位”值。
//            // 例如，bit1和bit2生成4个四态值，并且有Slaves使用这种格式，我们要尝试解析位值。
//            bit = Byte.valueOf(configuration.getStringProperty("Bit", "-1"));
//
//            //TODO: use the number of registers
//            characterEncoding=configuration.getTuples().getStringProperty(i,"CharacterEncoding","big-Endian");
//            multiplier = configuration.getDoubleProperty("Multiplier", 1);
//            additive = configuration.getDoubleProperty("Additive", 0);
//            eventName = configuration.getStringProperty("EventName", "Event");
//
//        } else {
//            //i 代表元组号，新传进来的一个i 标志着一组新的连接
//            portName = configuration.getTuples().getStringProperty(i, "Name", "COM3");
//            slaveId = configuration.getTuples().getIntProperty(i, "SlaveId", 1);
//            registerRange = parseRegisterRange(configuration.getTuples().getStringProperty(i, "RegisterRange", "HOLDING_REGISTER"));
//            dataType = parseDataType(configuration.getTuples().getStringProperty(i, "DataType", "TWO_BYTE_INT_UNSIGNED"));
//            offset = configuration.getTuples().getIntProperty(i, "Offset", 0);
//            characterEncoding = configuration.getTuples().getStringProperty(i, "CharacterEncoding", "big-Endian");
//
//            //protocol read link
//            objectName = configuration.getTuples().getStringProperty(i, "objectName", "");
//            objectClass = configuration.getTuples().getStringProperty(i, "objectClass", "");
//            objectBehavior = configuration.getTuples().getStringProperty(i, "objectBehavior", "");
//
//            //TODO: The Modbus4j functionality must be extend to allow to read and combine several "bit" values from
//            // the same register. For example bit1&bit2 generates a 4 four states value and there are Slaves that uses this format,
//            // and should be abstracted.
//            //we try to parse the bit value.
//            bit = Byte.valueOf(configuration.getTuples().getStringProperty(i, "Bit", "-1"));
//
//            //TODO: use the number of registers
//            multiplier = configuration.getTuples().getDoubleProperty(i, "Multiplier", 1);
//            additive = configuration.getTuples().getDoubleProperty(i, "Additive", 0);
//            eventName = configuration.getTuples().getStringProperty(i, "EventName", "Event");
//
//        }
//
//
//    }


//
//    /**
//     * 返回不同类型的定位器，定位到0x funCodeXXX 所在的地址
//     * @param funCode 要用16进制的大写表示
//     *
//     * @param slaveId
//     * @param offset
//     */
//    public static void handleModbusFun(int funCode, ModbusMaster master, int slaveId, int offset, int ){
//        switch (funCode) {
//            case 0X10:
//                ModbusUtil.writeHoldingRegisters(master, slaveId, offset, test);
//            case 0X03:
//                ModbusUtil.readHoldingRegister()
//            case 0X06:
//                ModbusUtil.writeHoldingRegister(master, slaveId, offset, );
//            case 0X2B:
//                //不确定先这样子写着
//                booleanBaseLocator = BaseLocator.holdingRegisterBit(slaveId, offset, bit);
//            default:
//                log.debug("输入的功能码不在设备的处理范围之内");
//        }
//    }

    private int parseRegisterRange(String stringProperty) {
        //TODO Check that the registerRange is correct
        //TODO use an enum
        switch (stringProperty) {
            case "COIL_STATUS":
                return 1;
            case "INPUT_STATUS":
                return 2;
            case "HOLDING_REGISTER":
                return 3;
            case "INPUT_REGISTER":
                return 4;
            default:
                return -1;
        }

    }

    /**
     * @return the modbusLocator
     */
    private BaseLocator getModbusLocator() {
        return modbusLocator;
    }

    private int parseDataType(String stringProperty) {

        switch (stringProperty) {
            case "BINARY":
                return 1;
            case "TWO_BYTE_INT_UNSIGNED":
                return 2;
            case "TWO_BYTE_INT_SIGNED":
                return 3;
            case "FOUR_BYTE_INT_UNSIGNED":
                return 4;
            case "FOUR_BYTE_INT_SIGNED":
                return 5;
            case "FOUR_BYTE_INT_UNSIGNED_SWAPPED":
                return 6;
            case "FOUR_BYTE_INT_SIGNED_SWAPPED":
                return 7;
            case "FOUR_BYTE_FLOAT":
                return 8;
            case "FOUR_BYTE_FLOAT_SWAPPED":
                return 9;
            case "EIGHT_BYTE_INT_UNSIGNED":
                return 10;
            case "EIGHT_BYTE_INT_SIGNED":
                return 11;
            case "EIGHT_BYTE_INT_UNSIGNED_SWAPPED":
                return 12;
            case "EIGHT_BYTE_INT_SIGNED_SWAPPED":
                return 13;
            case "EIGHT_BYTE_FLOAT":
                return 14;
            case "EIGHT_BYTE_FLOAT_SWAPPED":
                return 15;
            case "TWO_BYTE_BCD":
                return 16;
            case "FOUR_BYTE_BCD":
                return 17;
            case "CHAR":
                return 18;
            case "VARCHAR":
                return 19;
            default:
                return -1;
        }
    }

    /**
     * Fills the BatchRead with the information of the modbusLocators
     *
     * @param batchRead the batchRead that is going to be updated
     */
    void updateBatchRead(BatchRead<String> batchRead) {
        //TODO: The Name is not a good id.
        batchRead.addLocator(portName, getModbusLocator());
    }

//    /**
//     * Uses the FreedomModbusLocator to fill an Event with the correct
//     * information Used by the ModbusSensor
//     *
//     * @param results the readed values
//     * @param event The event that is filled with the information
//     */
//    void fillEvent(BatchResults<String> results, GenericEvent event) {
//        //GenericEvent event = new GenericEvent(sensor);
//        //TODO: We can use a switch over the eventName to send a more specialized event
//        String value;
//        if (bit != -1) //it's a bit value
//        {
//            value = results.getValue(portName).toString();
//        } else if (dataType == parseDataType("BINARY")) {
//            value = results.getValue(portName).toString();
//        } else //it's a numeric value
//        {
//            value = Double.toString(getAdjustedValue(Double.parseDouble((results.getValue(portName).toString()))));
//        }
//
//        event.addProperty(getName(), value);
//    }

//
//    void fillProtocolEvent(BatchResults<String> results, ProtocolRead event) {
//        String value;
//        //System.out.println("value: " + results.getValue(name));
//        if (bit != -1) //it's a bit value
//        {
//            value = results.getValue(portName).toString();
//        } else if (dataType == parseDataType("BINARY")) {
//            value = results.getValue(portName).toString();
//        } else //it's a numeric value
//        {
//            value = Double.toString(getAdjustedValue(Double.parseDouble((results.getValue(portName).toString()))));
//        }
//        event.addProperty("object.class", objectClass);
//        event.addProperty("object.name", objectName);
//        event.addProperty("behavior", objectBehavior);
//        event.addProperty("behaviorValue", value);
//    }



    /**
     * Transforms the value using the FreedomoticModbusLocator information to
     * translate from/to Freedomotic to Modbus scales
     *
     * @param value the value to transform
     * @return the transformed value
     */
//    private double getAdjustedValue(double value) {
//        return value * multiplier + additive;
//    }

//    Object parseValue(Config properties, int i) {
//        //TODO: use the DataType to parse the correct type
//        String value = properties.getTuples().getStringProperty(0, "value", "0");
//        if (bit != -1) {
//            return value;
//        } else if (dataType == parseDataType("BINARY")) {
//            return Boolean.parseBoolean(value);
//        } else {
//            return getAdjustedValue(Double.parseDouble(value));
//        }
//    }


    /**
     * @return the name
     */
    public String getName() {
        return portName;
    }

    //只读的信号

    /**
     * @param master
     * @return  机型名称 String
     */
    public String getNameMessage(ModbusMaster master, int slaveId){
        String str = "";
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER,30000, DataType.VARCHAR, 16, 15);
        try {
            str =  master.getValue(locator).toString();
            log.info("机型名称为： " + str);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * SN 信号
     * @param master
     * @param slaveId
     * @return
     */
    public String getSnMessage(ModbusMaster master, int slaveId){
        String str = "";
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER,30015, DataType.VARCHAR, 16, 10);
        try {
            str =  master.getValue(locator).toString();
            log.info("SN信号为： " + str);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 返回 PN 信号
     * @param master
     * @param slaveId
     * @return
     */
    public String getPnMessage(ModbusMaster master, int slaveId){
        String str = "";
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER,30025, DataType.VARCHAR, 16, 10);
        try {
            str =  master.getValue(locator).toString();
            log.info("PN信号为： " + str);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * @param master  16位无符号整数 用int类型的数据表示16为无符号数
     * @return  返回机型ID，组串个数，MPPT个数 Unsigned16位
     */
    public short[] getConnMessage(ModbusMaster master, int slaveId) {
        short[] flag =  ModbusUtil.readHoldingRegister(master, slaveId, 30070, 3);
        log.info("[ 机型ID 组串信息 MPPT个数 ] --" + Arrays.toString(flag));
        return flag;

    }

    /**
     * 额定功率（Pn）最大有功（Pmax） 最大视在（Smax）32位无符号数 long
     * @param master
     * @param slaveId
     * @return
     */
    public Long[] getPower(ModbusMaster master, int slaveId){
        int [] offset = {32073,32075,30077};
        Long [] flag = new Long[offset.length];
        try {
            flag = (Long[]) ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 额定功率（Pn） 最大有功（Pmax） 最大视在（Smax）] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * @param master  32位有符号整数 用int类型的数据表示
     * @return  最大无功（Qmax,向电网馈入）  最大无功（Qmax,从电网吸收）
     */
    public int[] getMaxReactivePower(ModbusMaster master, int slaveId) {
        int [] flag =  ModbusUtil.readHoldingRegisterByInt(master, slaveId, 30079, 2);
        log.info("[ 最大无功（Qmax,向电网馈入） 最大无功（Qmax,从电网吸收）] -- " + Arrays.toString(flag));
        return flag;


    }





    /**
     * PV电压 10 电流 100 信息
     * @param master
     * @return  返回 pv1-pv12 的电压与电流信息  有符号的16位整数 short
     */
    public short[] getPvElecMessage(ModbusMaster master, int slaveId) {
        short [] flag =  ModbusUtil.readHoldingRegister(master, slaveId, 32016, 23);
        log.info("[ PV1---pv12的电压 ：电流 ] -- " + Arrays.toString(flag));
        return  flag;
    }

    /**
     * 告警一信息
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnOne(ModbusMaster master, int slaveId) {
        log.info(" 告警一： [ 15 输出电流直流分量过大（重要） 14 输出过流（重要） 13 电网频率不稳定（重要） 12 电网欠频（重要） 11 电网过频 10 电网电压不平衡（重要）"+
                " 9 电网过压（重要） 8 电网欠压（重要） 7 电网掉电（重要） 6 电网相线对PE短路（重要） 5 AFCI自检失败（重要） 4 组串功率异常（提示） 3 组串反灌 （提示） 2 组串反接（重要） 1 直流电弧故障（重要） 0 组串电压高（重要）] ");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 32008, 1);
    }

    /**
     * 告警二信息
     * @param master
     * @param slaveId
     * @return
     */
    public boolean [] reportWarnTwo(ModbusMaster master, int slaveId) {
        log.info("告警二：[ 7 监控单元故障（次要） 6 License到期（提示） 5 升级失败（次要） 4 设备异常（重要） 3 温度过高 （重要） 2 绝缘阻抗低（重要） 1 系统接地异常（重要） 0 残余电流异常（重要）]");
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 32009, 1);
    }

    /**
     * 单机遥信   16位无符号整数 用10位bit信息表示单机遥信信息
     * TODO 待检验两种方法的不同  加一个反馈函数凡是以位表达信息的处理函数
     * @param master
     * @return
     */
    public boolean[] singleMachineMessage(ModbusMaster master, int slaveId) {

        boolean [] contain ={};
        log.info("单机遥信 : [ 9点检 8关机 7限电停运 6故障停运 5正常停运 4自降额并网 3限电降额并网 2正常并网 1并网 0待机 ] ");
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER,32000, DataType.TWO_BYTE_INT_UNSIGNED,16 , 1);
        return ModbusUtil.readHoldingRegisterByBit(master, slaveId, 32000, 1);
    }

    /**
     * 运行状态  16位无符号整数
     * TODO 要对位信息进行处理判断
     * @param master
     * @param slaveId
     * @return
     */
    public boolean[] runStatusMessage(ModbusMaster master, int slaveId) {
        log.info(" 运行状态 ：[ 2 DPS数据采集状态 1 PV连接状态 0 闭锁状态 ] " );
        boolean [] flag =  ModbusUtil.readHoldingRegisterByBit(master, slaveId, 32002, 1);
        return flag;
    }

    /**
     * 输入功率  32 位有符号整数 int
     * @param master
     * @param slaveId
     * @return 返回int类型
     */

    public int getInputPower(ModbusMaster master, int slaveId) {
        int flag = 0;
        //TODO
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32064, DataType.FOUR_BYTE_INT_SIGNED, 16, 2);
        try {
            flag = (Integer) master.getValue(locator);
            log.info("输入功率为 ：" + flag);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 返回电网ABC相的电流 32位有符号整形int
     * @param master
     * @param slaveId
     * @return
     */
    public int[] getCurrentMessages(ModbusMaster master, int slaveId) {
        int [] offset = {32072,32074,32076};
        Integer [] flag = {};
        try {
            log.info("电网A相电流 ： 电网B相电流 ：电网C相电流");
            flag = ModbusUtil.bathReadByInt(master, slaveId, DataType.FOUR_BYTE_INT_SIGNED, offset);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return NumberUtil.integerToInt(flag);
    }

    @Override
    public int [] getCurrentMessage(ModbusMaster master, int slaveId) {
        int [] flag =  ModbusUtil.readHoldingRegisterByInt(master, slaveId, 32072, 3);
        log.info("[ 电网A相电流  电网B相电流 电网C相电流 ] -- " + Arrays.toString(flag));
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
        int [] flag =  ModbusUtil.readHoldingRegisterByInt(master, slaveId, 32069, 3);
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
        int [] flag = ModbusUtil.readHoldingRegisterByInt(master, slaveId, 32066, 3);
        log.info("[ 电网AB线电压  电网BC线电压  电网CA线电压 ] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 返回输出功率 有符号32位整数
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public Integer[] getOutputPowerMessage(ModbusMaster master, int slaveId){
        int [] offset = {32078,32080,32082};
        Integer[] flag = {};
        try {
            flag =  ModbusUtil.bathReadByInt(master, slaveId, DataType.FOUR_BYTE_INT_SIGNED, offset);
            log.info("[ 当天峰值有功功率 ： 有功功率 ： 无功功率 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 功率因数 16位short
     * @param master
     * @param slaveId
     * @return
     */

    public short getPowerFactor(ModbusMaster master, int slaveId) {
        short flag =  (Short) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_SIGNED, 32084, 1);
        log.info("功率因数为 ： " + flag);
        return flag;
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
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32087, DataType.TWO_BYTE_INT_SIGNED, 16, 1);
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
     * 返回 电网频率： 效率 ： 绝缘阻抗值 无符号16位整数 short表示
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public int[] getEffectiveMessage(ModbusMaster master, int slaveId){
        int [] offset = {32085, 32086};
        Integer [] flag = {};
        try {
            flag = ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
            log.info("[电网频率  效率] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 绝缘阻抗值
     * @param master
     * @param slaveId
     * @return 16位无符号数
     */
    @Override
    public int getResistance(ModbusMaster master, int slaveId) {
        int flag = (Integer) ModbusUtil.getValue(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, 32088, 1 );
        log.info("绝缘阻抗值为 ： " + flag);
        return flag;
    }




    /**
     * 设备状态 返回16位无符号数跟十六进制数进行对比
     * TODO 要对设备状态的十六进制数进行处理
     * @param master
     * @param slaveId
     */
    @Override
    public String getDeciveStatus(ModbusMaster master, int slaveId) {
        int flag = 0;
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32089, DataType.TWO_BYTE_INT_UNSIGNED, 16, 1);
        try {
            flag = (Integer) master.getValue(locator);
            log.info("设备状态 ：" + Integer.toHexString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return Integer.toHexString(flag);

    }

    /**
     * 返回异常码
     * TODO 要对返回的故障码进行判断处理
     * @param master
     * @param slaveId
     * @return
     */
    public int getExceptionCode(ModbusMaster master, int slaveId) {
        int flag = 0;
        locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 32090, DataType.TWO_BYTE_INT_UNSIGNED, 16, 1);
        try {
            flag = (Integer) master.getValue(locator);
            log.info("异常码 ：" + flag);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 返回开机、关机、系统时间 累计发电量 32位无符号数 long
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public Long[] getOffAndOnTime(ModbusMaster master, int slaveId){
        int [] offset = {32091,32093};
        Long [] flag = {};
        try {
            flag = ModbusUtil.bathRead(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, offset);
            log.info("[ 开机时间 ： 关机时间 ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 累计发电量
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    @Override
    public long getPowerGeneration(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 32106, 2);
        log.info("累计发电量为 ：" + flag);
        return flag;
    }

    /**
     * 系统时间
     * @param master
     * @param slaveId
     * @return 无符号32位数
     */
    @Override
    public long getSystemTime(ModbusMaster master, int slaveId) {
        long flag =  (Long) ModbusUtil.getValue(master, slaveId, DataType.FOUR_BYTE_INT_UNSIGNED, 40000, 2);
        log.info("系统时间为 ：" + flag);
        return flag;
    }



    //下面是可读可写的部分信号

    /**
     * 设置系统时间 无符号32位 long
     * @param master
     * @param slaveId
     * @param value
     */
    @Override
    public void writeSystemTime(ModbusMaster master, int slaveId, long value) {
        long lower = 946684800;
        if ( value >= lower )  {
            locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, 40000, DataType.FOUR_BYTE_INT_UNSIGNED, 16, 2);
            try {
                master.setValue(locator, value);
                log.info("系统时间为 ：" + master.getValue(locator).toString());
            } catch (ModbusTransportException | ErrorResponseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读有功功率  非连续寄存器地址
     * @param master 无符号16位 int表示
     * @param slaveId
     * @return
     */
    @Override
    public int[] getActivePower(ModbusMaster master, int slaveId){
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 65535;
        int qmax = 1000;
        int [] offset = {40120,40125};
        Integer [] flag = {};
        try {
            flag =  ModbusUtil.bathReadByInt(master, slaveId, DataType.TWO_BYTE_INT_UNSIGNED, offset);
            log.info("[ 有功功率固定值降额 （kW）： 有功功率百分比降额（0.1%) ] -- " + Arrays.toString(flag));
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        if( flag[0] < 0 || flag[0] > pmax) {
            log.debug("读有功功率固定值降额超出允许范围");
        } else if ( flag[1] < 0 || flag[1] > qmax) {
            log.debug("读有功功率百分比降额(0.1%)超出允许范围");
        }
        return NumberUtil.integerToInt(flag);
    }

    /**
     * 有功功率固定值降额   并要对数据进行校验
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    @Override
    public boolean setActivePower(ModbusMaster master, int slaveId, int value) {
        int offset = 40120;
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 65535;
        if (value >= 0 && value <= pmax) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("有功功率固定值降额写入成功!");
            return true;
        } else {
            log.info("有功功率固定值降额输入数据范围非法!");
            return false;
        }
    }

    /**
     *
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    @Override
    public boolean setActivePowerPercentage(ModbusMaster master, int slaveId, int value) {
        int offset = 40123;
        //high代表最大百分比
        int high = 1000;
        if (value >= 0 && value <= high) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_UNSIGNED, value);
            log.info("有功功率百分比降额(0.1%)写入成功");
            return true;
        } else {
            log.info("有功功率百分比降额(0.1%)输入数据范围非法");
            return false;
        }
    }

    /**
     * 读无功功率 有符号16位
     * @param master
     * @param slaveId
     * @return
     */
    @Override
    public short[] getReactivePower(ModbusMaster master, int slaveId) {
        short [] flag =  ModbusUtil.readHoldingRegister(master, slaveId, 40122, 2);
        if (flag[0] <= -1 || ( flag[0] < 0.8 && flag[1] > -0.8 )
                || flag[0] > 1) {
            log.debug("读无功功率补偿（PF）超出允许范围");
        } else if (flag [1] < -0.6 || flag[1] > 0.6) {
            log.debug("读无功功率补偿（Q/S）超出允许范围");
        }
        log.info("[ 无功功率补偿（PF） ： 无功功率补偿（Q/S）] -- " + Arrays.toString(flag));
        return flag;
    }

    /**
     * 写无功功率 （PF） （Q/S）规定数组为两位，并要对数据进行校验  U16用int代替了
     * @param master
     * @param slaveId
     * @param flag
     * @return
     */
    @Override
    public boolean setReactivePower(ModbusMaster master, int slaveId, short [] flag) {
        int offset = 40122;
        if (flag[0] <= -1 || ( flag[0] < 0.8 && flag[1] > -0.8 )
                || flag[0] > 1) {
            log.debug("写无功功率补偿（PF）超出允许范围");
            return false;
        } else if (flag [1] < -0.6 || flag[1] > 0.6) {
            log.debug("写无功功率补偿（Q/S）超出允许范围");
            return false;
        } else {
            ModbusUtil.writeHoldingRegisters(master, slaveId, offset, flag);
            log.info("写无功功率补偿成功");
            return true;
        }
    }

    /**
     * 无功功率补偿（PF）   并要对数据进行校验
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setReActivePf(ModbusMaster master, int slaveId, short value) {
        int offset = 40122;
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 65535;
        if ((value > -1 && value <= 0.8) || (value >= 0.8 && value <= 1)) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_SIGNED, value);
            log.info("无功功率补偿（PF）写入成功!");
            return true;
        } else {
            log.info("无功功率补偿（PF）输入数据范围非法!");
            return false;
        }
    }

    /**
     * 无功功率补偿（PF）   并要对数据进行校验
     * @param master
     * @param slaveId
     * @param value
     * @return
     */
    public boolean setReActiveQs(ModbusMaster master, int slaveId, short value) {
        int offset = 40122;
        //TODO 带对数据范围进行处理 对于Pmax尚不明确
        int pmax = 65535;
        if ( value >= -0.6 && value <= 0.6 ) {
            ModbusUtil.writeValue(master, slaveId, offset, DataType.TWO_BYTE_INT_SIGNED, value);
            log.info("无功功率补偿（PF）写入成功!");
            return true;
        } else {
            log.info("无功功率补偿（PF）输入数据范围非法!");
            return false;
        }
    }



    /**
     * 这里用int类型来代替U16类型的数了，只要不为负数即可，
     * TODO 输入的数据不能为负，统一用16进制输入 这里输入的数据有待验证
     * @param instruction
     * @return
     */
    @Override
    public boolean startDevice(ModbusMaster master, int slaveId, int instruction) {
        int offset = 40123;
        locator = BaseLocator.holdingRegister(slaveId, offset, instruction);
        if (instruction < 0) {
             log.debug("输入数据不能为负");
             return false;
         } else {
             try {
                 master.setValue(locator , instruction);

             } catch (ModbusTransportException | ErrorResponseException e) {
                 log.debug(e.toString());
             }
             log.info("设备已打开");
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
        if (instruction < 0) {
            log.debug("输入数据不能为负");
            return false;
        } else {
            try {
                master.setValue(locator, instruction);

            } catch (ModbusTransportException | ErrorResponseException e) {
                log.debug(e.toString());
            }
            log.info("设备已关闭");
            return true;
        }
    }


















}
