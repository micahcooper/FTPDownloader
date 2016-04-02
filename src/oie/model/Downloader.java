package oie.model;
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
		if( source.endsWith("/") ){
			System.out.println("directory");
			
			try {
				Vector<ChannelSftp.LsEntry> list = sftp.ls(".");
				int mode = ChannelSftp.OVERWRITE;
				System.out.println(source+files.get(i));
				sftp.get(source+files.get(i), destination, monitor, mode);
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
	
	private class SFTPFile
	 {
	     private SftpATTRS sftpAttributes;

	     public SFTPFile(LsEntry lsEntry)
	     {
	         this.sftpAttributes = lsEntry.getAttrs();
	     }

	     public boolean isFile()
	     {
	         return (!sftpAttributes.isDir() && !sftpAttributes.isLink());
	     }
	 }
	 
	  private List<SFTPFile> getFiles(String path)
	  {
	      List<SFTPFile> files = null;
	      try
	      {
	          List<?> lsEntries = sftp.ls(path);
	          if (lsEntries != null)
	          {
	              files = new ArrayList<SFTPFile>();
	              for (int i = 0; i < lsEntries.size(); i++)
	              {
	                  Object next = lsEntries.get(i);
	                  if (!(next instanceof LsEntry))
	                  {
	                      // throw exception
	                  }
	                  SFTPFile sftpFile = new SFTPFile((LsEntry) next);
	                  if (sftpFile.isFile())
	                  {
	                	  System.out.println("adding file: "+lsEntries.get(i).toString());
	                      files.add(sftpFile);
	                  }
	              }
	          }
	      }
	      catch (SftpException sftpException)
	      {
	          //
	      }
	      return files;
	  }
}