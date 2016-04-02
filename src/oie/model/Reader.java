/**
 * 
 */
package oie.model;

import java.io.File;
import java.io.FileReader;
/**
 * @author micah cooper
 *
 */
public class Reader {
	private File file;
	private String contents;
	private String filename;
	
	/**
	 * 
	 */
	public Reader(String filename) {
		System.out.println("blank reader created");
		this.filename=filename;
		contents="";
	}

	/**
	 * @param filename
	 */
	public String read(){
		//BufferedReader buffer;
		
		try{
			file = new File( filename );
			FileReader read = new FileReader(file);
			int c;
			//System.out.println( "filename: "+file.toString()+" - message: "+read.read() );
			while ((c = read.read()) != -1) {
				contents += (char) c;
	            System.out.print( (char) c );
	         }
			read.close();
			System.out.println("\nfinished");
		}
		catch(Exception e){
			System.out.println( "ERROR: "+e.getMessage() );
		}
		
		return contents;
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
}