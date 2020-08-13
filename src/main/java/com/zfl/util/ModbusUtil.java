package com.zfl.util;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;


/**
 * @ClassName ModbusUtil
 * @Description TODO 华为SUN2000-60KTL-M0 V300R001型号的光伏逆变器的功能码
 *               : 0x03  读寄存器 支持单个和多个寄存器连续读取 要处理U16、U32跟int、short两种类型
 *               : 0x06  写单个寄存器 支持单寄存器写动作
 *               : 0x10  写多个寄存器 支持多寄存器连续写动作
 *               : 0x2B  读设备识别码 获取设备类型和版本号
 * @Author hello world
 * @DATE 2019/7/2  11:08
 **/
public class ModbusUtil {

    private static BatchRead<Number> batch = new BatchRead<Number>();

    private static BatchResults<Number> results = new BatchResults <>();

    /**
     * 记录日志
     */
    private static Log log = LogFactory.getLog(ModbusUtil.class);

    /**
     * 读取线圈状态 0x01
     * 取得一组逻辑线圈的当前状态
     * @param slaveId      slaveId
     * @param offset       偏移位置
     * @param numberOfBits 读取的长度
     * @return 读取值
     * @throws ModbusTransportException 异常
     */
    public static Boolean readCoilStatus(ModbusMaster master, int slaveId, int offset, int numberOfBits) {
        ReadCoilsRequest request;
        Boolean flag = true;
        try {
            request = new ReadCoilsRequest(slaveId, offset, numberOfBits);
            ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
            if (response.isException()) {
                log.error("function 0x01 Exception response: message=" + response.getExceptionMessage());
                flag = false;
            } else {
                log.info("功能码：1--" + Arrays.toString(response.getBooleanData()));
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 读离散量输入状态 0x02
     *
     * @param slaveId      从机的Id
     * @param offset       偏移量
     * @param numberOfBits 待读开关量的个数
     */
    public static Boolean readDiscreteInput(ModbusMaster master, int slaveId, int offset, int numberOfBits) {

        ReadDiscreteInputsRequest request;
        Boolean flag = true;
        try {
            request = new ReadDiscreteInputsRequest(slaveId, offset, numberOfBits);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);
            if (response.isException()) {
                log.info("function 0x02 Exception response: message=" + response.getExceptionMessage());
                flag = false;
            } else {
                log.info("功能码:2--" + Arrays.toString(response.getBooleanData()));
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 读取保存寄存器 0x03  这个方法可以实现对连续寄存器的连续读操作
     * 默认是返回short类型的数据的 有符号十六位数
     * @param slaveId           从机的Id
     * @param offset            偏移量
     * @param numberOfRegisters 待读寄存器的个数
     */
    public static short[] readHoldingRegister(ModbusMaster master,int slaveId, int offset, int numberOfRegisters) {
        short[] result = null;
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, offset, numberOfRegisters);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
            if (response == null) {
                log.info("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x03 Exception read short Arraydata response: message=" + response.getExceptionMessage());
            } else {
                result = response.getShortData();
//                log.info("功能码：3-- read message by short" + Arrays.toString(result));
            }
        } catch (ModbusTransportException e) {
            log.error("读寄存器数据出错：", e);
        }
        return result;
    }

    /**
     * 对返回若干位二进制的数据重载 主要用于对一些位信息进行判定
     * @param master
     * @param slaveId
     * @param offset
     * @param numberOfRegisters
     * @return
     */
    public static boolean[] readHoldingRegisterByBit(ModbusMaster master,int slaveId, int offset, int numberOfRegisters) {
        boolean[] result = null;
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, offset, numberOfRegisters);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
            if (response == null) {
                log.info("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x03 read bit bool Arraydata Exception response: message=" + response.getExceptionMessage());
            } else {
                result = response.getBooleanData();
                log.info("功能码：3 read message by bit -- " + Arrays.toString(response.getBooleanData()));
            }
        } catch (ModbusTransportException e) {
            log.error("读寄存器数据出错：", e);
        }
        return result;
    }

    /**
     * 当要求返回16位无符号数据时可以用Int类型来代替
     * @param master
     * @param slaveId
     * @param offset
     * @param numberOfRegisters
     * @return
     */
    public static int[] readHoldingRegisterByInt(ModbusMaster master,int slaveId, int offset, int numberOfRegisters) {
        int[] result = new int[numberOfRegisters];
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, offset, numberOfRegisters);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
            if (response == null) {
                log.info("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x03 Exception read Integer Arraydata response: message=" + response.getExceptionMessage());
            } else {

                short [] flag = response.getShortData();
                for (int i = 0; i < flag.length ; i++) {
                    result [i] = NumberUtil.formatInt(flag[i]);
//                    result [i] = flag[i];
                }
//                log.info("功能码：3-- read message by Integer" + Arrays.toString(result));
            }
        } catch (ModbusTransportException e) {
            log.error("读寄存器数据出错：", e);
        }
        return result;
    }

    /**
     * 对于返回的32位无符号数进行重载处理  处理的原理是将每个short类型的数组合并为一个long类型的数据
     * @param master
     * @param slaveId
     * @param offset
     * @param numberOfRegisters
     * @return
     */
    public static long[] readHoldingRegisterByLong(ModbusMaster master,int slaveId, int offset, int numberOfRegisters) {
        long[] result = new long[numberOfRegisters];
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, offset, numberOfRegisters);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
            if (response == null) {
                log.info("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x03 Exception read Long Arraydata response: message=" + response.getExceptionMessage());
            } else {

                short [] flag = response.getShortData();
            }
        } catch (ModbusTransportException e) {
            log.error("读寄存器数据出错：", e);
        }
        return result;
    }







    /**
     * 读取外围设备输入的数据 0x04 输入寄存器
     *
     * @param slaveId 从站ID
     * @param offset   起始位
     * @param len     寄存器个数
     */
    public static short[] readInputRegisters(ModbusMaster master, int slaveId, int offset, int len) {
        short[] result = null;
        try {
            ReadInputRegistersRequest request = new
                    ReadInputRegistersRequest(slaveId, offset, len);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);
            if (response == null) {
                log.info("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x04 Exception response: message=" + response.getExceptionMessage());
            } else {
                result = response.getShortData();
                log.info("功能码：4--" + Arrays.toString(response.getShortData()));
            }
        } catch (ModbusTransportException e) {
            log.error(e);

        }
        return result;
    }

    /**
     * 写开关量数据  0x05
     *
     * @param slaveId 从站ID
     * @param offset  偏移量
     * @param value   写入的值
     */
    public static void writeCoil(ModbusMaster master, int slaveId, int offset, boolean value) {
        try {
            WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, value);
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            if (response.isException()){
                log.error("function 0x05 Exception response: mesage=" + response.getExceptionMessage());

            } else {
                log.info("功能码:5,写入单个数据成功!" + response.getSlaveId());
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保持寄存器，写入单个数据 0x06
     *
     * @param slaveId 从站ID
     * @param offset  偏移量
     * @param value   待写入数据
     */
    public static void writeHoldingRegister(ModbusMaster master, int slaveId, int offset, int value) {
        try {
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
            if (response.isException()) {
                log.error("function 0x06 Exception response: message=" + response.getExceptionMessage());

            } else {
                log.info("功能码:6,写入单个模拟量数据成功!" + response.toString());

            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeHoldingRegister(ModbusMaster master, int slaveId, int offset, short value) {
        try {
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
            if (response.isException()){
                log.error("function 0x06 Exception response: message=" + response.getExceptionMessage());

            } else {
                log.info("功能码:6,写入单个short模拟量数据成功!" + response.toString());

            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }




    /**
     * 写多个线圈 15 0x0f
     *
     * @param slaveId 从站ID
     * @param start   起始位置
     * @param values  数值
     */
    public static void writeCoils(ModbusMaster master, int slaveId, int start, boolean[] values) {
        try {
            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, start, values);
            WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);

            if (response.isException()) {
                log.error("function 0x0F Exception response: message=" + response.getExceptionMessage());

            } else {
                log.info("功能码:15,写入多个开关量数据成功!");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }


    /**
     * 写出多个模拟量数据到保存寄存器 16 0x10
     *
     * @param slaveId 从站Id
     * @param offset  偏移量
     * @param sdata   待写入数据
     */
    public static void writeHoldingRegisters(ModbusMaster master, int slaveId, int offset, short[] sdata) {
        try {

            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, offset, sdata);
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);
            if (response == null) {
                log.debug("连接下位机[" + slaveId + "]失败");
            } else if (response.isException()) {
                log.error("function 0x10 response出错：" + response.getExceptionMessage());
            } else {
                log.info("功能码:16,写入多个模拟量数据到保存寄存器成功!");
            }
        } catch (ModbusTransportException e) {
            log.error("写入寄存器出错", e);
        }
    }

    /**
     * @Description: 读取异常状态 0X07
     * @param master
     * @param slaveId
     */
    public static void readExceptionStatus(ModbusMaster master, int slaveId) {
        try {
            ReadExceptionStatusRequest request = new ReadExceptionStatusRequest(slaveId);
            ReadExceptionStatusResponse response = (ReadExceptionStatusResponse) master.send(request);

            if (response.isException()) {
                log.error("readExceptionStatus Exception response: message=" + response.getExceptionMessage());

            }
            else {
                log.error(response.getExceptionStatus());

            }
        }
        catch (ModbusTransportException e) {
            log.error(e.toString());
        }
    }


    /**
     * @Description: 报告slaveId 17 0x11
     * @param master
     * @param slaveId
     */
    public static void reportSlaveId(ModbusMaster master, int slaveId) {
        try {
            ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
            ReportSlaveIdResponse response = (ReportSlaveIdResponse) master.send(request);

            if (response.isException()) {
                log.error("reportSlaveId Exception response: message=" + response.getExceptionMessage());

            }
            else {
                log.info(Arrays.toString(response.getData()));

            }
        }
        catch (ModbusTransportException e) {
            log.error(e.toString());
        }
    }

    /**
     * @Description: 根据类型写数据 仅支持保存寄存器
     * @param master
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static void writeValue(ModbusMaster master, int slaveId, int offset, int dataType, int value) {


        BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
      //高低字节颠倒 高位
//      BaseLocator<Number> hr2 = BaseLocator.holdingRegister(slaveId, offset, dataType);

        try {
            master.setValue(locator, value);
            //test
            log.info(" writeValue :  " + master.getValue(locator)+ "成功");

        } catch (ModbusTransportException | ErrorResponseException e) {
            log.info("writeValue error :" + e);
        }
    }

    /**
     * @Description: 根据类型写数据 仅支持保存寄存器
     * @param master
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static void writeValue(ModbusMaster master, int slaveId, int offset, int dataType, Number [] data) {
        BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
        try {
            for (Number aData : data) {
                master.setValue(locator, aData);
            }

        } catch (ModbusTransportException | ErrorResponseException e) {
            log.info("writeValue error :" + e);
        }

    }


    /**
     * 返回所需的单个信号值 06
     * @param master
     * @param slaveId
     * @param dataType
     * @param offset
     * @param numberOfRegister
     * @return
     */
    public static Number getValue(ModbusMaster master, int slaveId, int dataType, int offset, int numberOfRegister) {
        Number flag = 0;
        BaseLocator<?> locator = BaseLocator.createLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, dataType, 16, numberOfRegister);
        try {
            flag = (Number) master.getValue(locator);
        } catch (ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 批量读取    可以批量读取非连续寄存器中数据
     * 这里主要用于连续读取读32位unsigned整数了 read接口那里不支持
     * 与NumberUtil功能类里的Integer[] to int[]搭配起来用比较好
     * @param master
     * @param slaveId
     * @param dataType
     * @param offset 偏移值的数组
     * @return 这里返回的是Long
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static Long[] bathRead(ModbusMaster master, int slaveId, int dataType, int [] offset)
            throws ModbusTransportException, ErrorResponseException {


        batch.setContiguousRequests(true);
        Long[] information = new Long[offset.length];

        //****************批量读取不同寄存器中的单个数据*******************//

        //读取保持寄存器数据
        //根据设置的数据类型读取 这个光伏逆变器仅支持保持寄存器的读写
        for (int i = 0; i < offset.length; i++) {
            batch.addLocator(i, BaseLocator.holdingRegister(slaveId, offset[i], dataType) );
        }
        /*
         *  读取整形中16位中某一位的布尔值
         *  result.getValue(3.1)
         *  batch.addLocator(3.1, BaseLocator.holdingRegisterBit(slaveId, 0, 0));
         */
        results = master.send(batch);
        for (int i = 0; i < offset.length; i++) {
            information[i] = (Long) results.getValue(i);
        }
        return information;
    }

    /**
     * @Description: 批量读取    可以批量读取非连续寄存器中数据
     * 这里主要用于非连续读取读32位有符号整数了
     * read接口那里不支持
     * @param master
     * @param slaveId
     * @param dataType
     * @param offset 偏移值的数组
     * @return 这里返回的是Number包装类
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static Integer[] bathReadByInt(ModbusMaster master, int slaveId, int dataType, int [] offset)
            throws ModbusTransportException, ErrorResponseException {


        batch.setContiguousRequests(true);
        Integer[] information = new Integer[offset.length];

        //****************批量读取不同寄存器中的单个数据*******************//

        //读取保持寄存器数据
        //根据设置的数据类型读取 这个光伏逆变器仅支持保持寄存器的读写
        for (int i = 0; i < offset.length; i++) {
            batch.addLocator(i, BaseLocator.holdingRegister(slaveId, offset[i], dataType) );
        }

        results = master.send(batch);
        for (int i = 0; i < offset.length; i++) {
            information[i] = (Integer) results.getValue(i);
        }
        return information;
    }

    /**
     * @Description: 批量读取    可以批量读取非连续寄存器中数据
     * 这里主要用于非连续读取读32位有符号整数了
     * read接口那里不支持
     * @param master
     * @param slaveId
     * @param dataType
     * @param offset 偏移值的数组
     * @return 这里返回的是Number包装类
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static Short[] bathReadByShort(ModbusMaster master, int slaveId, int dataType, int [] offset)
            throws ModbusTransportException, ErrorResponseException {


        batch.setContiguousRequests(true);
        Short[] information = new Short[offset.length];

        //****************批量读取不同寄存器中的单个数据*******************//

        //读取保持寄存器数据
        //根据设置的数据类型读取 这个光伏逆变器仅支持保持寄存器的读写
        for (int i = 0; i < offset.length; i++) {
            batch.addLocator(i, BaseLocator.holdingRegister(slaveId, offset[i], dataType) );
        }
        /*
         *  读取整形中16位中某一位的布尔值
         *  result.getValue(3.1)
         *  batch.addLocator(3.1, BaseLocator.holdingRegisterBit(slaveId, 0, 0));
         */
        results = master.send(batch);
        for (int i = 0; i < offset.length; i++) {
            information[i] = (Short) results.getValue(i);
        }
        return information;
    }


}


