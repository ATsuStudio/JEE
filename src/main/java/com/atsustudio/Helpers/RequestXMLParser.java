package com.atsustudio.Helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RequestXMLParser {

	public static Map<String, Object> ParseRequest(HttpServletRequest request, String DOMRoot) throws IOException {

		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		InputStream stream = new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8")));

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(DOMRoot);

			Map<String, Object> data = new HashMap<>();

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				branchRecourse(nNode, data);
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void branchRecourse(Node node, Map<String, Object> data) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) node;
			NodeList childNodes = eElement.getChildNodes();

			// has child elements
			if (childNodes.getLength() - 1 > 0) {
				
				if(isRow(childNodes)) {
					List<Map<String, Object>> subList = new ArrayList<>();
					for (int i = 0; i < childNodes.getLength() - 1; i++) {
						Node childNode = childNodes.item(i);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							Map<String, Object> subMap = new HashMap<>();
							branchRecourse(childNode, subMap);
							subList.add(subMap);
						}
					}
					data.put(eElement.getTagName(), subList);
				}
				else {
					for (int i = 0; i < childNodes.getLength() - 1; i++) {
						Node childNode = childNodes.item(i);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							branchRecourse(childNode, data);
						}
					}
				}
			} else {
				data.put(eElement.getTagName(), eElement.getTextContent());
			}
		}
	}
	private static boolean isRow(NodeList childNodes) {
		if (childNodes.getLength() - 1 > 2) {
			Node fNode = childNodes.item(1);
			Node sNode = childNodes.item(3);
			Element fElement = (Element) fNode;
			Element sElement = (Element) sNode;
			if(fElement.getTagName() == sElement.getTagName()) {
				return true;
			}
		}
		return false;
	}
}
