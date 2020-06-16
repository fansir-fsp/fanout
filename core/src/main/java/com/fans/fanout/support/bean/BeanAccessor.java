package com.fans.fanout.support.bean;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author ：fsp
 * @date ：2020/6/16 16:55
 */
public class BeanAccessor {

    private static Unsafe UNSAFE;

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("unsafe 初始化失败", e);
        }
    }

    public static void setObject(Object[] elements, int index, Object element) {
        long arrayOffset = UNSAFE.arrayBaseOffset(elements.getClass());
        int indexScale = UNSAFE.arrayIndexScale(elements.getClass());
        int arrayShift = 31 - Integer.numberOfLeadingZeros(indexScale);
        long thisIndexScale = (index << arrayShift) + arrayOffset;

        UNSAFE.putObjectVolatile(elements, thisIndexScale, element);
    }

}
