package com.wipro.taf.selenium.keywords;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.TreeMap;

public class ReadXMLFile {

    public static void main(String argv[]) {

        //Set<Integer> set_obj = new TreeSet<>();
        try {
            String xml = "<IssueObject><issueStatus><correlationId>000000000000000003191027</correlationId><cim>49587192</cim><caseNum></caseNum><issuedFlag>W</issuedFlag><errorFlag></errorFlag><errorDesc></errorDesc></issueStatus><issueStatus><correlationId>000000000000000003191027</correlationId><cim>49587193</cim><caseNum>000000</caseNum><issuedFlag>K</issuedFlag><errorFlag></errorFlag><errorDesc></errorDesc></issueStatus></IssueObject>";


            TreeMap set_obj = new TreeMap();
            //	String textArea = webDriver.findElement(By.xpath(xPath)).getText();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("issueStatus");

            // iterate the employees
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                NodeList name = element.getElementsByTagName("cim");
                Element line = (Element) name.item(0);
                Integer val = Integer.parseInt(getCharacterDataFromElement(line));
                set_obj.put(i, val);
                System.out.println("svfssfsgdsgfds" + set_obj);
                System.out.println(set_obj.size());

                if (set_obj.size() == 2) {
                    System.out.println(set_obj.size());
                    //	String ilb=set_obj.
                    //conf.addRuntimeData("ILBCIMVALUE", set_obj.get(0));
                } else {

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

}
