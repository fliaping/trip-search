package com.fliaping.trip.extracter;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payne on 5/30/16.
 */
public class ConfReader {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    public Document document ;
    public ConfReader(String filePath){
        try {
        //DOM parser instance
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        //parse an XML file into a DOM tree
            document = builder.parse(new File(filePath));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public List<ExtaRule> getItemRules(String name){
        List<ExtaRule> rulesList = null;
        if (null != document){
            Element rootElement = document.getDocumentElement();
            NodeList nodes = rootElement.getChildNodes();
            NodeList nodeList = rootElement.getElementsByTagName("item");
            if(nodeList != null)
            {
                for (int i = 0 ; i < nodeList.getLength(); i++)
                {
                    Element element = (Element)nodeList.item(i);
                    String method = element.getTagName();
                    if(element.getAttribute("name").equals(name)){

                        Element rules = (Element) element.getElementsByTagName("rules").item(0);
                        NodeList ruleNodeList = element.getElementsByTagName("arule");

                        rulesList = new ArrayList<ExtaRule>();

                        for (int j = 0 ; j < ruleNodeList.getLength(); j++){
                            Element rule = (Element) ruleNodeList.item(0);
                            rulesList.add(new ExtaRule(rule));
                        }
                    }
                }
            }
        }
        return rulesList;
    }

    public class ExtaRule{
        private String method;
        private String values[];

        public ExtaRule(Element arule){
            if(null != arule){
                method = arule.getAttribute("method");

                //System.out.println("method:"+method);
                NodeList aruleList =  arule.getElementsByTagName("value");

                if(null != aruleList && aruleList.getLength() > 0){
                    values = new String[aruleList.getLength()];
                    for (int i = 0 ; i < aruleList.getLength(); i++){
                        values[i] = aruleList.item(i).getTextContent();
                    }
                }
            }

        }

        public String getMethod() {
            return method;
        }

        public String[] getValues() {
            return values;
        }
    }
}
