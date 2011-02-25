package org.ec.bos;

import java.io.IOException;
import org.xml.sax.SAXException;

public class TestXML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XMLGenerator algo = new XMLGenerator("salidaBOS.txt");
		
		try {
			algo.generate();
		} catch (SAXException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
