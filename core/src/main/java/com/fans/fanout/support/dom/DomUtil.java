package com.fans.fanout.support.dom;

import org.w3c.dom.Element;

/**
 * dom解析 工具
 *
 * @author ：fsp
 * @date ：2020/4/18 10:20
 */
public class DomUtil {

    private DomUtil() {
    }

    public static String getText(Element element, String tag) {
        return element
                .getElementsByTagName(tag)
                .item(0)  // get the first element of the tag name.
                .getTextContent();
    }
}
