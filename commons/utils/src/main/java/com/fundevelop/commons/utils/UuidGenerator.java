package com.fundevelop.commons.utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * 使用UUID算法生成全球唯一ID.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 20:59
 */
public class UuidGenerator {
    protected static TimeBasedGenerator timeBasedGenerator;

    static {
        ensureGeneratorInitialized();
    }

    protected static void ensureGeneratorInitialized() {
        if (timeBasedGenerator == null) {
            synchronized (UuidGenerator.class) {
                if (timeBasedGenerator == null) {
                    timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
                }
            }
        }
    }

    public static String getNextId() {
        return timeBasedGenerator.generate().toString();
    }

    private UuidGenerator(){}
}
