package com.zfl.modbus.gateways;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import com.serotonin.io.serial.SerialParameters;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class SerialPortWrapperImpl extends SerialParameters implements SerialPortWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(SerialPortWrapperImpl.class);
    private SerialPort port;
    private String commPortId;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int flowControlIn;
    private int flowControlOut;

    public SerialPortWrapperImpl(String commPortId, int baudRate, int dataBits, int stopBits, int parity, int flowControlIn,
                                 int flowControlOut) {

        this.commPortId = commPortId;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControlIn = flowControlIn;
        this.flowControlOut = flowControlOut;

        port = new SerialPort(this.commPortId);

    }

    @Override
    public void close() throws Exception {
        port.closePort();
        //listeners.forEach(PortConnectionListener::closed);
        LOG.debug("Serial port {} closed", port.getPortName());
    }

    @Override
    public void open() {
        try {
            port.openPort();
            port.setParams(this.getBaudRate(), this.getDataBits(), this.getStopBits(), this.getParity());
            port.setFlowControlMode(this.getFlowControlIn() | this.getFlowControlOut());

            //listeners.forEach(PortConnectionListener::opened);
            LOG.debug("Serial port {} opened", port.getPortName());
        } catch (SerialPortException ex) {
            LOG.error("Error opening port : {} for {} ", port.getPortName(), ex);
        }
    }

    @Override
    public InputStream getInputStream() {
        return new SerialInputStream(port);
    }

    @Override
    public OutputStream getOutputStream() {
        return new SerialOutputStream(port);
    }

    @Override
    public int getBaudRate() {
        return baudRate;
        //return SerialPort.BAUDRATE_9600;
    }

    @Override
    public int getFlowControlIn() {
        return flowControlIn;
        //return SerialPort.FLOWCONTROL_NONE;
    }

    @Override
    public int getFlowControlOut() {
        return flowControlOut;
        //return SerialPort.FLOWCONTROL_NONE;
    }

    @Override
    public int getDataBits() {
        return dataBits;
        //return SerialPort.DATABITS_8;
    }

    @Override
    public int getStopBits() {
        return stopBits;
        //return SerialPort.STOPBITS_1;
    }

    @Override
    public int getParity() {
        return parity;
        //return SerialPort.PARITY_NONE;
    }
}
