package org.turnxcel.turnxcel.xmlreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

@Component
public class XmlParserT4 {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserT4.class);
    private int numericKey = 1;

    public Map<String, String> readXML(MultipartFile file) {
        Map<String, String> xmlDataMap = new HashMap<>();

        try {
            logger.info("Reading XML file: {}", file.getOriginalFilename());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file.getInputStream());
            doc.getDocumentElement().normalize();
            extractParameterData(doc.getDocumentElement(), xmlDataMap, "");
        } catch (Exception e) {
            logger.error("Error reading XML: {}", e.getMessage());
        }

        return xmlDataMap;
    }

    private void extractParameterData(Node node, Map<String, String> xmlDataMap, String currentPath) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            NodeList childNodes = element.getChildNodes();
            if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
                String parameterValue = node.getTextContent().trim();
                String parameterXPath = buildXPath(element);
                String parameterId = buildId(element, currentPath);
                if (parameterId.startsWith(" << ")) {
                    parameterId = parameterId.substring(1);
                }
                xmlDataMap.put(String.valueOf(numericKey++), parameterXPath + ";" + parameterValue + ";" + parameterId);
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                extractParameterData(childNodes.item(i), xmlDataMap, buildId(element, currentPath));
            }
        }
    }

    private String buildId(Element element, String currentPath) {
        String idAttribute = element.getAttribute("id");
        if (idAttribute.isEmpty()) {
            return currentPath;
        }
        return currentPath + " << " + getLocalName(element.getTagName()) + " id=" + idAttribute;
    }

    private String buildXPath(Element element) {
        StringBuilder xpathBuilder = new StringBuilder();
        Node currentNode = element;

        while (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
            Element currentElement = (Element) currentNode;
            String tagName = currentElement.getTagName();
            xpathBuilder.insert(0, "/" + tagName);
            currentNode = currentElement.getParentNode();
        }

        return xpathBuilder.toString();
    }

    protected String getLocalName(String qName) {
        int colonIndex = qName.indexOf(':');
        return colonIndex == -1 ? qName : qName.substring(colonIndex + 1);
    }
}
