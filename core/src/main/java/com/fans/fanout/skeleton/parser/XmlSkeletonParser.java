package com.fans.fanout.skeleton.parser;

import com.fans.fanout.skeleton.ServiceSkeleton;
import com.fans.fanout.support.dom.DomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * xml方式 service 骨架解析器
 *
 * @author ：fsp
 * @date ：2020/4/18 10:24
 */
public class XmlSkeletonParser implements SkeletonParser {

    Logger logger = LoggerFactory.getLogger(XmlSkeletonParser.class);

    private Document doc;

    private InputStream inputStream;

    public XmlSkeletonParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public XmlSkeletonParser parseDoc() throws Exception {
        if (inputStream == null) {
            throw new RuntimeException("service配置文件不存在");
        }
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse(inputStream);
        return this;
    }

    public Map<String, ServiceSkeleton> parseSkeleton() throws Exception {
        logger.info("开始解析service配置...");

        Map<String, ServiceSkeleton> skeletonMap = new HashMap();
        NodeList list = doc.getElementsByTagName("service");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            Element element = (Element) node;
            String name = element.getAttribute("name");
            String port = element.getAttribute("port");
            String apiFullDes = DomUtil.getText(element, "api");
            String implFullDes = DomUtil.getText(element, "impl");
            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(port)
                    || StringUtils.isEmpty(apiFullDes) || StringUtils.isEmpty(implFullDes)) {
                logger.info("service 配置不完整，元素 name:{}, port:{}, api:{}, impl:{} 必填",
                        name, port, apiFullDes, implFullDes);
                throw new RuntimeException("service配置元素不完整");
            }

            //判断继承关系
            Class apiClass = Class.forName(apiFullDes);
            Class implClass = Class.forName(implFullDes);
            if (!apiClass.isAssignableFrom(implClass)) {
                logger.info("service配置接口继承关系错误，apiClass:{} implClass:{}",
                        apiFullDes, implClass);
                throw new RuntimeException("service配置接口继承关系错误");
            }

            ServiceSkeleton skeleton = new ServiceSkeleton.Builder().name(name).port(Integer.parseInt(port))
                    .apiClass(apiClass).implObj(implClass.newInstance()).build();
            if (skeletonMap.get(name) != null) {
                logger.info("service配置名称重复，name:{}", name);
                throw new RuntimeException("service配置名称重复");
            }
            skeletonMap.put(name, skeleton);
        }

        logger.info("service配置解析完成，共读取service {} 个", skeletonMap.size());
        return skeletonMap;
    }
}
