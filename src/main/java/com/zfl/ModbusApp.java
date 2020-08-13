package com.zfl;


import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.zfl.modbus.ds.Config;
import com.zfl.modbus.gateways.ModbusMasterGateWay;
import com.zfl.modbus.locator.InverterLocator;
import com.zfl.modbus.locator.InverterSmaLocator;
import com.zfl.util.ModbusUtil;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * @ClassName ModbusApp
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/16  19:51
 **/
@SpringBootApplication
//@PropertySource("classpath:application.yml")
//@EnableConfigurationProperties
public class ModbusApp {


    private static ModbusMaster master;

    private static Config config;

    //物理层设置
    //linux port
//    @Value("${syspara.sport}")
    private String PORT_NAME = "COM3" ;
    //windows port
//    private static final String PORT_NAME = "COM3";
    private int PORT_BAUDRATE = 9600;
    private int PORT_DATABITS = 8;
    private int PORT_PARITY = 0;//null 无奇偶校验
    private int PORT_STOPBITS = 1;

   @Autowired
   InverterLocator inverterLocator;
   @Autowired
   InverterSmaLocator locator;

   private Config initConfig(){
        config = new Config();
        config.setProperty("Modbus-protocol", "RTU");

        //Serial rtu test
//        config.setProperty("port", PORT_NAME);
        System.out.println(PORT_NAME);
        config.setProperty("baudrate", String.valueOf(PORT_BAUDRATE));
        config.setProperty("data-bits", String.valueOf(PORT_DATABITS));
        config.setProperty("parity",String.valueOf(PORT_PARITY));
        config.setProperty("stop-bits", String.valueOf(PORT_STOPBITS));

        //TCP Test
        config.setProperty("host", "192.168.137.243");
        config.setProperty("tcpport", String.valueOf(502));
        config.setProperty("encapsulated", "true");
        return config;
    }

    private void initModbus(Config configuration) {
        //初始化config 并初始化 ModbusMaster
        master = ModbusMasterGateWay.getInstance(configuration);
        try {
            master.init();
        } catch (ModbusInitException e) {
            e.printStackTrace();
        }
    }


    public void testInverter() {
        InverterLocator inverterLocator = new InverterLocator();
        int slaveId = 1;
        initConfig();
        initModbus(config);
//        inverterLocator.getNameMessage(master, slaveId);
//        inverterLocator.getPnMessage(master, slaveId);
//        inverterLocator.getSnMessage(master, slaveId);
//        //TODO
//        inverterLocator.getConnMessage(master, slaveId);
//        inverterLocator.getMaxReactivePower(master, slaveId);
//        inverterLocator.getPower(master, slaveId);
//        inverterLocator.getPvElecMessage(master, slaveId);
//
//
//        inverterLocator.singleMachineMessage(master, slaveId);
//        inverterLocator.runStatusMessage(master, slaveId);
//        inverterLocator.reportWarnOne(master, slaveId);
//        inverterLocator.reportWarnTwo(master, slaveId);
//        inverterLocator.getInputPower(master, slaveId);
//        inverterLocator.getVoltageConnMessage(master, slaveId);
//        inverterLocator.getVoltageMessage(master, slaveId);
//        //TODO test the difference of two functions
////        inverterLocator.getCurrentMessages(master, slaveId);
//        inverterLocator.getCurrentMessage(master , slaveId);
//        inverterLocator.getOutputPowerMessage(master, slaveId);
//        inverterLocator.getPowerFactor(master, slaveId);
//        inverterLocator.getEffectiveMessage(master, slaveId);
//        inverterLocator.getInnerTemperature(master, slaveId);
//        inverterLocator.getResistance(master, slaveId);
//
//        inverterLocator.getDeciveStatus(master, slaveId);
//        inverterLocator.getExceptionCode(master, slaveId);
//        inverterLocator.getOffAndOnTime(master, slaveId);
//        inverterLocator.getPowerGeneration(master, slaveId);
//        //可读可写
//        inverterLocator.getSystemTime(master, slaveId);
//        inverterLocator.getActivePower(master, slaveId);
//        inverterLocator.getReactivePower(master, slaveId);
//
//        inverterLocator.setActivePower(master, slaveId, 1);
//
//        inverterLocator.setActivePowerPercentage(master, slaveId, 50);
////        inverterLocator.setReactivePower(master, slaveId, flag);
////        inverterLocator.stopDevice(master, slaveId, 1);
//        inverterLocator.setReActivePf(master, slaveId, (short)1);


        //smallInverter test
        //只读
        InverterSmaLocator locator = new InverterSmaLocator();
        locator.getInverterCapacity(master, slaveId);
        locator.getOutputMode(master, slaveId);
        locator.getNameMessage(master, slaveId);
        locator.getSystemTime(master, slaveId);
        locator.getCarbonReduction(master, slaveId);
        //TODO pv7、pv8不行
        locator.getPvElecMessage(master, slaveId);
        locator.getVoltageMessage(master, slaveId);
        locator.getCurrentMessage(master, slaveId);
        locator.getVoltageConnMessage(master, slaveId);
        locator.getPowerFactor(master, slaveId);
        locator.getEffectiveMessage(master, slaveId);
        locator.getInnerTemperature(master, slaveId);
        locator.getDeciveStatus(master, slaveId);
        //TODO 有功功率跟无功功率不行
        locator.getOutputPowerMessage(master, slaveId);
        locator.getInputTotalPower(master, slaveId);
        locator.getPowerGeneration(master, slaveId);
        locator.getNowTotalGeneration(master, slaveId);
        locator.getBlockedState(master, slaveId);
        locator.getProtectState(master, slaveId);
        locator.invertGridState(master, slaveId);
        locator.getResistance(master, slaveId);
        locator.getOffAndOnTime(master, slaveId);
        locator.getFrontTotalGeneration(master, slaveId);
        //TODO Mptt待检测
        short [] test = ModbusUtil.readHoldingRegister(master, slaveId,33022, 6);
        System.out.println(Arrays.toString(test));
        locator.getMpptInputPower(master, slaveId);
        locator.getDateSynch(master, slaveId);

        //可读可写
        locator.getReactivePowerMode(master, slaveId);
        locator.getActivePower(master, slaveId);
        locator.getReactivePower(master, slaveId);
        locator.getReactivePowerTime(master, slaveId);
        locator.cosCurve(master, slaveId);
        locator.quCurve(master, slaveId);
        locator.getPowerProtectTime(master, slaveId);
        locator.getPowerProtectPoint(master, slaveId);
        locator.getGridStandardCode(master, slaveId);
        locator.getResistencePoint(master, slaveId);
        locator.getVoltageProPoint(master, slaveId);
        locator.getGridRestartTime(master, slaveId);
        locator.getStartTime(master, slaveId);
        locator.getGridRecoveryTime(master, slaveId);
        locator.getSet(master, slaveId);
        locator.getReactiveCompensate(master, slaveId);


        locator.getOverFrequency(master, slaveId);
        locator.getCurveMode(master, slaveId);
        locator.getMpttScan(master, slaveId);
        locator.systemTimePart(master, slaveId);
        locator.getActiveDerating(master, slaveId);
        locator.getReActiveDerating(master, slaveId);



        locator.reportWarnZero(master, slaveId);
        locator.reportWarnOne(master, slaveId);
        locator.reportWarnTwo(master, slaveId);
        locator.reportWarnTwo(master, slaveId);
        locator.reportWarnThree(master, slaveId);
        locator.reportWarnFour(master, slaveId);
        locator.reportWarnFive(master, slaveId);
        locator.reportWarnSix(master, slaveId);
        locator.reportWarnSeven(master, slaveId);
        locator.reportWarnEight(master, slaveId);
        locator.reportWarnNine(master, slaveId);
        locator.reportWarnSixteen(master, slaveId);

        //写
//        locator.writeSystemTime(master, slaveId, 1);
        locator.setReActiveCompensate(master, slaveId, 1);
        locator.setActiveControl(master, slaveId, 0);
        locator.setActivePowerPercentage(master, slaveId, 10);
        locator.setActivePerGradient(master, slaveId, 1);
        short [] test2 = {(short)1, (short)1};
        locator.setReactivePower(master, slaveId, test2);
        locator.setReActiveAdjustTime(master, slaveId, 5);

        //只写
//        locator.stopDevice(master, slaveId, 1);
//        locator.startDevice(master, slaveId, 0);
//
//        locator.setActivePercentageHigh(master, slaveId, 1);
//        short [] test1 = {0,1};
//        locator.setReactiveHigh(master, slaveId, test1);
//        //TODO 曲线未写 保护时间保护点未写
//        locator.setGridStandardCode(master, slaveId, 10);
////        locator.setResistencePoint(master, slaveId, 1);
////        locator.setGridFaultRestart(master, slaveId, 1);
////        locator.setLowVoltageTran(master, slaveId, 1);
////        locator.setStartTime(master, slaveId, 1);
////        locator.getGridRecoveryTime(master, slaveId);
////        locator.setIslandDetection(master, slaveId, 1);
////        locator.setLvrt(master, slaveId, 1);
//        locator.setReactiveVoltage(master, slaveId, 100 );
//        locator.setQuitVoltage(master, slaveId, 90);
//
//        locator.setVoltageProPoint(master, slaveId, 1);
//
//        //TODO 112开始未完成
//
////        Integer [] data = {2019,8,1,14,28,0};
////        locator.setPartTime(master, slaveId, data);
//
//        locator.setMpttScan(master, slaveId, 0);









    }

    public static void main(String[] args) {
        ModbusApp app = new ModbusApp();
        SpringApplication.run(ModbusApp.class, args);
        System.out.println("test");
        app.testInverter();
    }
}