package org.brailleblaster.wordprocessor;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brailleblaster.BBIni;
import org.brailleblaster.util.Notify;
import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Normalizer {
	File f;
	Document doc;
	static Logger log = BBIni.getLogger();
	
	public Normalizer(String path){
		this.f = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			this.doc = dBuilder.parse(this.f);
		}
		catch(ConnectException e){
			new Notify("Brailleblaster failed to access necessary materials from online.  Please check your internet connection and try again.");
			e.printStackTrace();
			log.log(Level.SEVERE, "Connections Error", e);
		}
		catch(UnknownHostException e){
			new Notify("Brailleblaster failed to access necessary materials from online.  Please check your internet connection and try again.");
			e.printStackTrace();
			log.log(Level.SEVERE, "Unknown Host Error", e);
		}
		catch (ParserConfigurationException e) {
			new Notify("An error occurred while reading the document. Please check whehther the document contains vaild XML.");
			e.printStackTrace();
			log.log(Level.SEVERE, "Parse Error", e);
		}
		catch (SAXException e) {
			new Notify("An error occurred while reading the document. Please check whehther the document contains vaild XML.");
			e.printStackTrace();
			log.log(Level.SEVERE, "Sax Error", e);
		} 
		catch (IOException e) {
			new Notify("An error occurred while reading the document.");
			e.printStackTrace();
			log.log(Level.SEVERE, "IO Error", e);
		}
	}
	
	public void createNewNormalizedFile(String path){
		if(this.doc != null){
			normalize();
			write(this.doc, path);
		}
	}
	
	private void normalize(){
		doc.normalize();
		removeEscapeChars(doc.getDocumentElement());
	}
	
	private void removeEscapeChars(Element e){
		NodeList list = e.getChildNodes();
		
		for(int i = 0; i < list.getLength(); i++){
			if(list.item(i) instanceof Element){
				removeEscapeChars((Element)list.item(i));
			}
			else if(list.item(i) instanceof Text){
				Text t = (Text)list.item(i);
				String text = t.getTextContent();
				text = text.replace("\n", " ");
				t.setTextContent(text);
			}
		}
	}
	private boolean onlyWhitespace(String text){
		for(int j = 0; j < text.length(); j++){
			if(!Character.isWhitespace(text.charAt(j))){
				return false;
			}
		}
		return true;
	}
	
	public void write(Document document, String path) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Transformer Configuration Exception", e);
		}
		catch (TransformerException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Transformer Exception", e);
		}
 
	
    }
}
