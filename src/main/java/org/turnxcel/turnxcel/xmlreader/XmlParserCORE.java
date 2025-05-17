package org.turnxcel.turnxcel.xmlreader;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.commons.setup.Variables;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;

public class XmlParserCORE {
    static Logger log = Logger.getLogger(org.turnxcel.turnxcel.xmlreader.XmlParserT5.class);
    static int numericKey = 1;
//    static String xmlFilePath = "C:\\Users\\aniket.jain\\Box\\Work\\Learnings\\EclipseWorkspace\\UltimateTesterDev\\InputData\\core.xml";
    public static void ReadXML(String[] args) {
        try {
//        	SetupExecutor.Log4JProperty.configureLog4j();
            String xmlFilePath = Variables.XMLFilePath;
            log.info("Going to read the Configuration XML at File Path: " + xmlFilePath);
            log.info("--------------------------------------------");
            File inputFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            extractParameterData(doc.getDocumentElement(), "");
            for (Map.Entry<String, String> entry : Variables.XMLDataMap.entrySet()) {
                String[] values = entry.getValue().split(";", 3);
                log.info("XML Key: " + entry.getKey());
                log.info("XML Name: " + (values.length > 0 ? values[0] : ""));
                log.info("XML Value: " + (values.length > 1 ? values[1] : ""));
                log.info("XML Identifier (ID): " + (values.length > 2 ? values[2] : ""));
                log.info("--------------------------------------------");
            }
//            Converter.ConfigXmlToExcel.WriteExcel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractParameterData(Node node, String currentPath) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            NodeList childNodes = element.getChildNodes();
            if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
                String parameterValue = node.getTextContent().trim();
                
                if(parameterValue.contains(".")) {
                	  String[] parts = String.valueOf(parameterValue).split("\\.");
                	if (parts.length == 2 && parts[1].equals("0")) {
                        parameterValue = parts[0];
                    }
                }
                String parameterId = buildId(element, currentPath);
                String paramName = getParamName(element, currentPath);
                if(parameterId.startsWith(" << ")) {
                	parameterId = parameterId.substring(1);
                }
                Variables.XMLDataMap.put(String.valueOf(numericKey++), paramName+";"+parameterValue + ";" + parameterId);
            }
            for (int i = 0; i < childNodes.getLength(); i++) {
                extractParameterData(childNodes.item(i), buildId(element, currentPath));
            }
        }
    }
    
    private static String getParamName(Element element, String currentPath) {
    	String nameArribute = element.getAttribute("name");
    	log.info("Name Attribute: "+nameArribute);
    	if(nameArribute.isEmpty()) {
    		return currentPath;
    	}
    	return nameArribute;
    	
    }

    private static String buildId(Element element, String currentPath) {
        String idAttribute = element.getAttribute("id");
        if (idAttribute.isEmpty()) {
            return currentPath;
        }
        return currentPath + " << " + getLocalName(element.getTagName()) + " id=" + idAttribute;
    }

    protected static String getLocalName(String qName) {
        int colonIndex = qName.indexOf(':');
        return colonIndex == -1 ? qName : qName.substring(colonIndex + 1);
    }
}