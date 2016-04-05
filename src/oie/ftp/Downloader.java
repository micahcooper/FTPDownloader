package oie.ftp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;


public class Downloader {

	String host, user;
	int port;
	Session session;
	Channel channel;
	ChannelSftp sftp;

	public Downloader() {
		System.out.println("new sftp");
	}

	public Downloader(Session session) {
		this.session = session;
		System.out.println("new overloaded downloader");
	}

	public void connect() {
		try {
			session.connect();
			System.out.println("here");
			this.channel = session.openChannel("sftp");
			channel.connect();
			this.sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void download(String source, String destination){
		SftpProgressMonitor monitor = new MyProgressMonitor();
		String filename = "";
		
		if( source.endsWith("/") ){
			System.out.println("directory");
			
			try {
				Vector<ChannelSftp.LsEntry> list = sftp.ls(source);
				int mode = ChannelSftp.OVERWRITE;
				
				for( int i=0; i<list.size(); i++)
					if( isFile( (ChannelSftp.LsEntry)list.get(i) )){
						System.out.println( ((ChannelSftp.LsEntry)list.get(i)).getFilename() );
						filename = ((ChannelSftp.LsEntry)list.get(i)).getFilename();
						sftp.get(source+filename, destination, monitor, mode);
					}
			} catch (SftpException e) {
				System.out.println(e.getMessage());
			}
		}
		else
			try {
				int mode = ChannelSftp.OVERWRITE;
				sftp.get(source, destination, monitor, mode);
			} catch (SftpException e) {
				System.out.println(e.getMessage());
			}
		//session.disconnect();
	
	}
	
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	public Session setupSession(String host, String user, String password) {
		JSch secureChannel;

		try {
			secureChannel = new JSch();
			
			//secureChannel.setKnownHosts(filepath + "\\src\\known-host");
			secureChannel.addIdentity(password);
			this.session = secureChannel.getSession(user, host, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications", 
	                  "publickey,keyboard-interactive,password");
			session.setConfig(config);
			return session;
		} catch (Exception e) {
			System.out.println( e.getMessage() );
		}
		return null;
	}
	
	 
	  private boolean isFile(ChannelSftp.LsEntry entry)
	  {
		  System.out.println( entry.getLongname() );
		  return (!entry.getAttrs().isDir() && !entry.getAttrs().isLink() );
	  }
}