package oie.ftp;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;


public class Downloader {

	String host, user;
	int port;
	Session session;
	Channel channel;
	ChannelSftp sftp;

	public Downloader(String host, String user, String password) {
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

		} catch (Exception e) {
			System.out.println( e.getMessage() );
		}

	}

	public void connect() {
		try {
			session.connect();
			this.channel = session.openChannel("sftp");
			channel.connect();
			this.sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void download(String src_dir, String dst_dir, String dst_filename){
		SftpProgressMonitor monitor = new MyProgressMonitor();
		String src_filename = "";
		
		if( src_dir.endsWith("/") ){
			System.out.println("directory");
			
			try {
				@SuppressWarnings("unchecked")
				Vector<ChannelSftp.LsEntry> list = sftp.ls(src_dir);
				int mode = ChannelSftp.OVERWRITE;
				
				for( int i=0; i<list.size(); i++)
					if( isFile( (ChannelSftp.LsEntry)list.get(i) )){
						System.out.println( ((ChannelSftp.LsEntry)list.get(i)).getFilename() );
						src_filename = ((ChannelSftp.LsEntry)list.get(i)).getFilename();
						if(dst_filename == null)
							sftp.get(src_dir+src_filename, dst_dir, monitor, mode);
						else
							sftp.get(src_dir+src_filename, dst_dir+"/"+dst_filename, monitor, mode);
					}
			} catch (SftpException e) {
				System.out.println(e.getMessage());
			}
		}
		else
			try {
				int mode = ChannelSftp.OVERWRITE;
				sftp.get(src_dir, dst_dir, monitor, mode);
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
	
	private boolean isFile(ChannelSftp.LsEntry entry) {
		System.out.println( entry.getLongname() );
		return (!entry.getAttrs().isDir() && !entry.getAttrs().isLink() );
	}
}