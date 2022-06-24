package org.seed.mybatis.maven.util;


/**
 * ID生成器
 */
public class IDWorker {

    private static IDWorker instance;

    //因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。

    //机器ID  2进制5位  32位减掉1位 31个
    private static long workerId;
    //机房ID 2进制5位  32位减掉1位 31个
    private static long datacenterId;
    //代表一毫秒内生成的多个id的最新序号  12位 4096 -1 = 4095 个
    private static long sequence;
    //设置一个时间初始值    2^41 - 1   差不多可以用69年
    private static long twEpoch = 1585644268888L;
    //5位的机器id
    private static long workerIdBits = 5L;
    //5位的机房id
    private static long datacenterIdBits = 5L;
    //每毫秒内产生的id数 2 的 12次方
    private static long sequenceBits = 12L;
    // 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
    private static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private static long workerIdShift = sequenceBits;
    private static long datacenterIdShift = sequenceBits + workerIdBits;
    private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);
    //记录产生时间毫秒数，判断是否是同1毫秒
    private static long lastTimestamp = -1L;

    static {
        instance = new IDWorker(1, 1, 1);
    }

    public static long getWorkerId() {
        return workerId;
    }

    public static long getDatacenterId() {
        return datacenterId;
    }

    private static long getTimestamp() {
        return System.currentTimeMillis();
    }


    public static IDWorker getInstance() {
        return instance;
    }

    private IDWorker(long workerId, long datacenterId, long sequence) {

        // 检查机房id和机器id是否超过31 不能小于0
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {

            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    // 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
    public static synchronized long nextId() {
        // 这儿就是获取当前时间戳，单位是毫秒
        long timestamp = timeNow();
        if (timestamp < lastTimestamp) {

            System.err.printf(
                    "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {

            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;
            //当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0;
        }
        // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
        lastTimestamp = timestamp;
        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twEpoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }

    public static synchronized String nextIdHex() {
        return Long.toHexString(nextId());
    }


    /**
     * 根据数据库号和表号生成
     *
     * @param dbNum 数据库编号
     * @param tbNum 数据表编号
     * @return 第一位为数据库号，第二、第三位为表号；从第四位起为ID
     */
    public static synchronized String nextScatterIdHex(int dbNum, int tbNum) {
        String appendZero = "";
        if (tbNum < 16) {
            appendZero = "0";
        }
        return Integer.toHexString(dbNum) + appendZero + Integer.toHexString(tbNum) + Long.toHexString(nextId());
    }


    /**
     * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
     *
     * @param lastTimestamp
     * @return
     */
    private static long tilNextMillis(long lastTimestamp) {

        long timestamp = timeNow();

        while (timestamp <= lastTimestamp) {
            timestamp = timeNow();
        }
        return timestamp;
    }

    //获取当前时间戳
    private static long timeNow() {
        return System.currentTimeMillis();
    }




}
