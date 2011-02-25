package test;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLGenerator {
	private String fName;
	
	private XMLSerializer serializer;
	private OutputFormat outFormat;
	
	private File inputFile;
	private File outputFile;
	private FileOutputStream output;
	private FileInputStream input;
	
	public XMLGenerator(String fName){
		this.fName = fName;
		//String[] fOut = fName.split("."); //Must FIX this
		String fOutName = ("salida1.xml");
		this.outputFile = new File(fOutName);
		this.inputFile  = new File(this.fName);
		this.outFormat  = new OutputFormat("XML","ISO-8859-1",true);
		
		try{
			this.output = new FileOutputStream(outputFile);
			this.input  = new FileInputStream(inputFile);
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		this.outFormat.setIndent(1);
		this.outFormat.setIndenting(true);
		this.outFormat.setDoctype(null,"users.dtd");
		
		this.serializer = new XMLSerializer(output,outFormat);
	}
	
	@SuppressWarnings("deprecation")
	public void generate() throws IOException,SAXException{
		BufferedInputStream bReader = new BufferedInputStream(input);
		DataInputStream dReader		= new DataInputStream(bReader);
		ContentHandler cHandler		= serializer.asContentHandler();
		
		String line;
		String[] lineArray;
		String subLine;
		String[] subLineArray;
		
		cHandler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		AttributesImpl attsData = new AttributesImpl();
		
		while(dReader.available() != 0){
			line = dReader.readLine().trim();
			lineArray = line.split("\\s+");
			
			atts.clear();
			atts.addAttribute("", "", "SECTOR", "CDATA", lineArray[3]);
			atts.addAttribute("", "", "NHITS", "CDATA", lineArray[5]);
			atts.addAttribute("", "", "NEXTIND", "CDATA", lineArray[8]);
			cHandler.startElement("", "EVENT", "EVENT", atts);
			
			int n = Integer.parseInt(lineArray[5]);
			for(int i = 0; i < n; i++){
				subLine = dReader.readLine().trim();
				subLineArray = subLine.split("\\s+");
				String[] Data  = {subLineArray[1],subLineArray[3],subLineArray[5]};
				
				attsData.clear();
				attsData.addAttribute("", "", "ID", "CDATA", Data[0]);
				attsData.addAttribute("", "", "ADC","CDATA", Data[1]);
				attsData.addAttribute("", "", "TDC","CDATA", Data[2]);
				cHandler.startElement("", "", "DATA", attsData);
				cHandler.endElement("", "", "DATA");
			}
			subLine = dReader.readLine().trim();
		}
		cHandler.endElement("", "", "EVENT");
		cHandler.endDocument();
		output.close();
		input.close();
	}
		
	public String getFileName(){
		return this.fName;
	}
}
