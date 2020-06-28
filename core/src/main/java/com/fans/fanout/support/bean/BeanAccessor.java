package com.fans.fanout.support.bean;

import com.fans.fanout.skeleton.annotation.HolderField;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author ：fsp
 * @date ：2020/6/16 16:55
 */
public class BeanAccessor {

    private static Logger log = LoggerFactory.getLogger(BeanAccessor.class);

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

    public static void setObject(Object[] localElements, int index, Object element) {
        Object localElement = localElements[index];
        Field[] localFields = localElement.getClass().getDeclaredFields();
        for (Field localField : localFields) {
            if (localField.getAnnotation(HolderField.class) != null) {
                //替换field
                try {
                    Field field = element.getClass().getDeclaredField(localField.getName());
                    field.setAccessible(true);
                    localField.setAccessible(true);
                    localField.set(localElement, field.get(element));
                } catch (NoSuchFieldException fe) {
                    log.error("成员{}.{} 参数穿透注解c/s声明不一致", element.getClass(), localField.getName());
                } catch (Exception e) {
                    log.error("形参穿透赋值失败，error stack:", e);
                }
            }
        }
    }
}
