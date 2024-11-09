package com.minis.web;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 邹茂萍
 * @date 2024-11-09
 */
public class XmlScanComponentHelper {
    public static List<String> getNodeValue(URL xmlPath) {
        final List<String> packages = new ArrayList<>();
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(xmlPath);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        final Element root = document.getRootElement();
        final Iterator<Element> it = root.elementIterator();
        while (it.hasNext()) {
            final Element next = it.next();
            packages.add(next.attributeValue("base-package"));
        }
        return packages;
    }
}
