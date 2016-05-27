package oie.ftp;

import com.jcraft.jsch.Session;

public class startJob {
	private Reader read;
	private String host, user,password,src_dir,dst_dir,dst_filename;
	Session session;
	Downloader downloader;
	
	public static void main(String[] args) {
		String paramsLoc = "";
		
		//if( args.length > 0 ){
			paramsLoc = args[0];
			new startJob(paramsLoc);
		//}
		//else
			System.out.println("Failed to provide cmd args");
	}

	public startJob(String paramsLoc) {
		getDownloadDetails(paramsLoc);

		try{
			downloader = new Downloader(host,user,password);
			downloader.connect();
		}catch(Exception e){System.out.println(e.getMessage());}
		downloader.download(src_dir, dst_dir, dst_filename);
		downloader.getSession().disconnect();
	}

	private boolean getDownloadDetails(String paramsLoc){
		read = new Reader(paramsLoc);
		parseParams( read.read() );
		
		return true;
	}

	private boolean parseParams( String params ){
		String[] param = params.split(",");
		dst_filename=null;
		
		if(param.length > 4){
			host = param[0];
			user = param[1];
			password = param[2];
			src_dir = param[3];
			dst_dir = param[4];
		}
		if(param.length > 5)
			dst_filename = param[5];
		
		System.out.println("####" + dst_filename);
		
		
		return true;
	}
	
	 
}
