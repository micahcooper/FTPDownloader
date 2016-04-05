package oie.ftp;

import com.jcraft.jsch.Session;

public class startJob {
	private Reader read;
	private String host, user,password;
	private String filename,destination;
	Session session;
	Downloader downloader;
	
	public static void main(String[] args) {
		String paramsLoc = "";
		
		if( args.length > 0 ){
			paramsLoc = args[0];
			new startJob(paramsLoc);
		}
		else
			System.out.println("Failed to provide cmd args");
	}

	public startJob(String paramsLoc) {
		getDownloadDetails(paramsLoc);

		downloader = new Downloader();
		try{
			downloader.setupSession(host,user,password);
			downloader.connect();
		}catch(Exception e){System.out.println(e.getMessage());}
		downloader.download(filename, destination);
		downloader.getSession().disconnect();
	}

	private boolean getDownloadDetails(String paramsLoc){
		read = new Reader(paramsLoc);
		parseParams( read.read() );
		
		return true;
	}

	private boolean parseParams( String params ){
		String[] param = params.split(",");
		
		host = param[0];
		user = param[1];
		password = param[2];
		filename = param[3];
		destination = param[4];
		
		return true;
	}
	
	 
}
