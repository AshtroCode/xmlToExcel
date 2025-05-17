package org.turnxcel.turnxcel.xmlreader;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.commons.setup.Variables;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class XmlParserT5 {
    static Logger log = Logger.getLogger(XmlParserT5.class);
    static int numericKey = 1;
    static String idNameParsingLogic="";
//	static String xmlFilePath="C:\\Users\\aniket.jain\\Box\\Work\\Learnings\\EclipseWorkspace\\UltimateTesterDev\\InputData\\test 5G.xml";

    public static void ReadXML(String[] args) throws IOException, SAXException, ParserConfigurationException {
//    	SetupExecutor.Log4JProperty.configureLog4j();
    	String xmlFilePath = Variables.XMLFilePath;
    	try {
            log.info("Going to read the Configuration XML at File Path: " + xmlFilePath);
            log.info("--------------------------------------------");
            File inputFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            extractParameterDatafor5G(doc.getDocumentElement(), "");
        } catch (SAXParseException s) {
            if(s.getMessage().contains("The markup in the document following the root element must be well-formed")) {
            	createTempXML();
            	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new File (Variables.InputDataDirectory+"temp.xml"));
                doc.getDocumentElement().normalize();
                extractParameterDatafor5G(doc.getDocumentElement(), "");
            }
            else {
                throw new RuntimeException(s);
            }
        }
    	finally {
            for (Map.Entry<String, String> entry : Variables.XMLDataMap.entrySet()) {
                String[] values = entry.getValue().split(";", 3);
                log.info("XML Key: " + entry.getKey());
                log.info("XML Xpath: " + (values.length > 0 ? values[0] : ""));
                log.info("XML Value: " + (values.length > 1 ? values[1] : ""));
                log.info("XML Identifier (ID): " + (values.length > 2 ? values[2] : ""));
                log.info("--------------------------------------------");
            }
    	}
    }

    private static void extractParameterDatafor5G(Node node, String currentPath) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            NodeList childNodes = element.getChildNodes();
            String parameterValue="";
            if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
                parameterValue = node.getTextContent().trim();
                if(parameterValue.contains(".")) {
                	  String[] parts = String.valueOf(parameterValue).split("\\.");
                	if (parts.length == 2 && parts[1].equals("0")) {
                        parameterValue = parts[0];
                    }
                }
                String parameterXPath = buildXPath(element);
                String parameterId = buildIDfor5G(element, currentPath, parameterValue);
//                log.info("ID: "+parameterId);
//                log.info("XPath: "+parameterXPath);
                
                if (parameterXPath.startsWith("/SampleRootElementAdded")) {
//                	log.info("Xpath starting with SampleRootElement, going to replace it");
                	parameterXPath = parameterXPath.replace("/SampleRootElementAdded/", "/");
//                	log.info("Updated Xpath = "+parameterXPath);
                }
                Variables.XMLDataMap.put(String.valueOf(numericKey++), parameterXPath+";"+parameterValue + ";" + parameterId);
            }
            for (int i = 0; i < childNodes.getLength(); i++) {
            	extractParameterDatafor5G(childNodes.item(i), buildIDfor5G(element, currentPath, parameterValue));
            }
        }
    }

    private static String buildIDfor5G(Element element, String currentPath, String value) {
//        log.info("Current path: "+currentPath);
        String ParsingLogic = getParentIdNameWithValuefor5G(element, element.getTagName(), value);
           return ParsingLogic;
    }
    
    private static String buildXPath(Element element) {
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
    protected static String getLocalName(String qName) {
        int colonIndex = qName.indexOf(':');
//        log.info("Parameter name: "+qName);
        return colonIndex == -1 ? qName : qName.substring(colonIndex + 1);
    }
    protected static String getParentIdNameWithValuefor5G(Element element, String qName, String value) {
        if(qName.equals("id") || qName.equals("name") || qName.equals("common-ike-child-id")) {
//            log.info("ParsingLogic: "+qName+"="+value);
            idNameParsingLogic = element.getParentNode().getNodeName()+" "+ qName+"="+value;
            return idNameParsingLogic;
        }
        else {
        	return idNameParsingLogic;
        }
    }
    
    private static void createTempXML() throws IOException {
    	log.info("Multiple root elements found, going to create a temporary xml with one parent node");
    	FileReader inputFile = new FileReader(Variables.XMLFilePath);
    	@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(inputFile);
        String fileContents = "";
        String line = reader.readLine();
        while (line != null) {
//        	log.info("Line content: "+line);
        	if(line.contains("xc:operation")) {
//        		log.info("Contains operation type at starting index: "+line.indexOf(" xc:operation"));
        		line = line.substring(0, line.indexOf(" xc:operation"))+">";
//        		log.info("Revised line: "+line);
        	}
            fileContents += line;
            line = reader.readLine();
        }
//        log.info("File Contents: "+fileContents); 
    	String wrappedData = "<SampleRootElementAdded>"+fileContents+"</SampleRootElementAdded>";
//    	log.info("Added root element: "+wrappedData);
    	FileWriter writer = new FileWriter(Variables.InputDataDirectory+"temp.xml");
    	writer.write(wrappedData);
    	Variables.XMLFilePath = Variables.InputDataDirectory+"temp.xml";
    	log.info("New XML FilePath: "+Variables.XMLFilePath);
    	writer.close();
    }
}