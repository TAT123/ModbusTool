package com.zfl.util;


import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @ClassName NumberUtil
 * @Description TODO
 * @Author hello world
 * @DATE 2019/7/3  14:14
 **/
public class NumberUtil {
    public static final int SHORT_MAX = Short.MAX_VALUE - Short.MIN_VALUE + 1;

    /**
     * 将short类型转换为int类型。
     * 如果short值为正数则返回当前值。
     * 如果short值为负数则返回当前值-short最小值+short最大值。
     *
     * 注：
     * 	formatInt(10)	return 10
     * 	formatInt(-10)	return (-10) - (-32768) + 32767 + 1
     *
     * @param num
     * @return
     */
    public static int formatInt(short num) {
        int result = num;
        if(num < 0){
            result = num - Short.MIN_VALUE + Short.MAX_VALUE + 1;
        }
        return result;
    }
    /**
     * 将short数组转换为int类型
     * short[0]为高16位，short[1]为低16位。
     * short[0]左移16位，short[1]根据formatInt(short num)计算
     *
     * @param arr
     * @return 计算后的short[0] + short[1]
     */
    public static int formatInt(short[] arr) {
        int data_0 = arr[0] << 16;
        int data_1 = formatInt(arr[1]);
        return data_0 + data_1;
    }
    /**
     * 将int值转换为short数组。
     * 当前值/Short_MAX的值为数组[0]的值，
     * 如果当前值%Short_MAX的值大于short最大值
     * 则数组[1]的值为当前值取余SHort_MAX的值-short最大值+short最小值-1
     * 如果前值%Short_MAX的值不大于short最大值
     * 则数组[1]的值为当前值取余SHort_MAX的值
     * @param num
     * @return
     */
    public static short[] formatShort(int num) {
        short[] arr = new short[2];
        arr[0] = (short) (num / SHORT_MAX);
        int remainder = num % SHORT_MAX;
        if(remainder > Short.MAX_VALUE) {
            arr[1] = (short) (remainder - Short.MAX_VALUE + Short.MIN_VALUE - 1);
        }else {
            arr[1] = (short) remainder;
        }
        return arr;
    }

    /**
     * 将data字节型数据转换为0~255  Unsigned 8
     * @param data
     * @return
     */
    public static int getUnsignedByte(byte data) {
        return data&0xFF;
    }

    /**
     * 将data字节型数据转换为0~65535  Unsigned 16
     * @param data
     * @return
     */
    public static int getUnsignedShort(short data) {
        return data&0x0FFFF;
    }

    /**
     * 将int数据转换为0~4294967295  Unsigned 32
     * @param data
     * @return
     */
    public static long getUnsignedInt(int data) {
        return data&0x0FFFFFFFF;
    }
    public static long getUnsigned(int value) {
        return Integer.toUnsignedLong(value);
    }

    /**
     * long转成无符号数
     * @param value
     * @return
     */
    public static final BigDecimal longParseUnsigned(long value) {
        if (value >= 0) {
            return new BigDecimal(value);
        }
        long lowValue = value & 0x7fffffffffffffffL;
        return BigDecimal.valueOf(lowValue).add(BigDecimal.valueOf(Long.MAX_VALUE)).add(BigDecimal.valueOf(1));
    }

    /**
     * Long[] to long[] 专门用于转换数组类型
     * @param data
     * @return
     */

    public static long [] longParse(Long [] data) {
        return Arrays.stream(data).mapToLong(Long::valueOf).toArray();
    }

    /**
     * Integer[] to int[]
     * 想要转换成int[]类型，就得先转成IntStream,
     * 这里就通过mapToInt()把Stream<Integer>调用Integer::valueOf来转成IntStream
     * 而IntStream中默认toArray()转成int[]
     * @param data
     * @return
     */
    public static int [] integerToInt(Integer [] data) {
        return Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
    }

    /**
     * Short[] to int[]
     * 想要转换成int[]类型，就得先转成IntStream,
     * 这里就通过mapToInt()把Stream<Integer>调用Integer::valueOf来转成IntStream
     * 而IntStream中默认toArray()转成int[]
     * @param data
     * @return
     */
    public static int[] shortToInt(Short [] data) {
        return Arrays.stream(data).mapToInt(Short::shortValue).toArray();
    }



    /**
     * 前两步同上，此时是Stream<Integer>。
     * 然后使用Stream的toArray，传入IntFunction<A[]> generator。
     * 这样就可以返回Integer数组。不然默认是Object[]。
     * @param data
     * @return
     */
    public static Integer[] intToInteger(int [] data) {
        return Arrays.stream(data).boxed().toArray(Integer[]::new);
    }








}
