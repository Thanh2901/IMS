// package com.example.camera_service.service.impl;
//
// import java.io.StringReader;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
//
// import org.springframework.stereotype.Service;
// import org.w3c.dom.Document;
// import org.w3c.dom.Element;
// import org.w3c.dom.Node;
// import org.w3c.dom.NodeList;
// import org.xml.sax.InputSource;
//
// @Service
// public class SoapXmlParserServiceImpl {
//
//    public Map<String, Object> parseSoapResponse(String xmlResponse) {
//        Map<String, Object> responseData = new HashMap<>();
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));
//
//            NodeList bodyChildren =
//                    document.getElementsByTagName("SOAP-ENV:Body").item(0).getChildNodes();
//
//            for (int i = 0; i < bodyChildren.getLength(); i++) {
//                Node responseNode = bodyChildren.item(i);
//
//                if (responseNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element responseElement = (Element) responseNode;
//                    parseChildElements(responseElement, responseData);
//                }
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Error parsing SOAP XML: " + e.getMessage(), e);
//        }
//
//        return responseData;
//    }
//
//    public void parseChildElements(Element element, Map<String, Object> resultMap) {
//        NodeList childNodes = element.getChildNodes();
//        List<Map<String, Object>> scopesList = new ArrayList<>();
//
//        if (getCleanNodeName(element).equals("GetScopesResponse")) {
//            // Xử lý đặc biệt cho GetScopesResponse
//            for (int i = 0; i < childNodes.getLength(); i++) {
//                Node childNode = childNodes.item(i);
//
//                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element scopeElement = (Element) childNode;
//                    if (getCleanNodeName(scopeElement).equals("Scopes")) {
//                        Map<String, Object> scopeMap = new HashMap<>();
//                        NodeList scopeChildren = scopeElement.getChildNodes();
//
//                        for (int j = 0; j < scopeChildren.getLength(); j++) {
//                            Node scopeChild = scopeChildren.item(j);
//                            if (scopeChild.getNodeType() == Node.ELEMENT_NODE) {
//                                Element scopeChildElement = (Element) scopeChild;
//                                scopeMap.put(
//                                        getCleanNodeName(scopeChildElement),
//                                        scopeChildElement.getTextContent().trim());
//                            }
//                        }
//                        if (!scopeMap.isEmpty()) {
//                            scopesList.add(scopeMap);
//                        }
//                    }
//                }
//            }
//            if (!scopesList.isEmpty()) {
//                resultMap.put("GetScopesResponse", scopesList);
//            }
//        }
//    }
//
//    public String getCleanNodeName(Element element) {
//        String nodeName = element.getNodeName();
//        if (nodeName.contains(":")) {
//            nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
//        }
//        return nodeName;
//    }
// }
