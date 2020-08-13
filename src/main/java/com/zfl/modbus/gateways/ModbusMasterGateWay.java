package com.zfl.modbus.gateways;

/*
 * @ClassName ModbusMasterGateWay
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/8  9:00
 */
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.zfl.modbus.ds.Config;


public class ModbusMasterGateWay {

    private static ModbusMaster master = null;
    private static String connectionInfo = "No connected";
    private static final String PORT_NAME = "COM3";
    private static final int PORT_BAUDRATE = 9600;
    private static final int PORT_DATABITS = 8;
    private static final int PORT_PARITY = 0;
    private static final int PORT_STOPBITS = 1;
    private static final int PORT_FLOW_CONTROL_IN = 1;
    private static final int PORT_FLOW_CONTROL_OUT = 1;


    /**
     *
     */
    public ModbusMasterGateWay() {
    }

    /**
     * 默认返回一个Rtu格式的master
     * @return
     */
    public static ModbusMaster getInstance() {
        return getInstance(new Config());
    }

    /**
     *
     * @param configuration
     * @return
     */
    public static ModbusMaster getInstance(Config configuration) {
        if (master != null) {
            return master;
        } else {
            //这里默认使用Rtu方式了,若想用TCP,把下面改一下就行 若value为空则返回defaultValue RTU字符串
            String modbusProtocol = configuration.getStringProperty("modbus-protocol", "RTU");
            if ("TCP".equals(modbusProtocol)) {
                configureTcp(configuration);
            } else {
                configureSerial(configuration);
            }
            //private static boolean echo = false;
            int receiveTimeout = configuration.getIntProperty("timeout", 5000);//5 seconds
            int retries = configuration.getIntProperty("retries", 1);
            boolean multiwrites = configuration.getBooleanProperty("multiwrite-always", false);
            master.setTimeout(receiveTimeout);
            master.setRetries(retries);
            master.setMultipleWritesOnly(multiwrites);
            return master;
        }

    }

    /**
     * @return
     */
    public static String connectionInfo() {
        return connectionInfo;

    }

    private static void configureSerial(Config configuration) {
        ModbusFactory factory = new ModbusFactory();
        //SerialParameters params = new SerialParameters();
        String commPortId = configuration.getStringProperty("port", PORT_NAME);
        System.out.println("port name: " + commPortId);
        int baudRate = configuration.getIntProperty("baudrate", PORT_BAUDRATE);
        System.out.println("baudrate: " + baudRate);
        int dataBits = configuration.getIntProperty("data-bits", PORT_DATABITS);
        System.out.println("databits: " + dataBits);
        int parity = configuration.getIntProperty("parity", PORT_PARITY);
        System.out.println("parity: " + parity);
        int stopBits = configuration.getIntProperty("stop-bits", PORT_STOPBITS);
        System.out.println("stopbits: " + stopBits);
        int flowControlIn = configuration.getIntProperty("flow-control-in", PORT_FLOW_CONTROL_IN);
        System.out.println("flowcontrolin: " + flowControlIn);
        int flowControlOut = configuration.getIntProperty("flow-control-out", PORT_FLOW_CONTROL_OUT);
        System.out.println("flowcontrolout: " + flowControlOut);
        SerialPortWrapperImpl params = new SerialPortWrapperImpl(commPortId, baudRate, dataBits, stopBits, parity, flowControlIn,
                flowControlOut);
        master = factory.createRtuMaster(params);
        connectionInfo = "Serial Connection to: " + commPortId;
        System.out.println(connectionInfo());
    }

    private static void configureTcp(Config configuration) {
        ModbusFactory factory = new ModbusFactory();
        IpParameters params = new IpParameters();
        String host = configuration.getStringProperty("host", "localhost");
        System.out.println("host: " + host);
        int tcpport = configuration.getIntProperty("tcpport", 502);
        System.out.println("tcpport: " + tcpport);
        Boolean encap = configuration.getBooleanProperty("encapsulated", false);
        params.setEncapsulated(encap);
        params.setHost(host);
        params.setPort(tcpport);
        master = factory.createTcpMaster(params, true);
        connectionInfo = "TCP Connection to: " + host + ":" + tcpport;
        System.out.println(connectionInfo());
    }


}
