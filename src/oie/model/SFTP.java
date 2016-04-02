package oie.model;
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the sftp protocol support.
 *   $ CLASSPATH=.:../build javac Sftp.java
 *   $ CLASSPATH=.:../build java Sftp
 * You will be asked username, host and passwd. 
 * If everything works fine, you will get a prompt 'sftp>'. 
 * 'help' command will show available command.
 * In current implementation, the destination path for 'get' and 'put'
 * commands must be a file, not a directory.
 *
 */

import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class SFTP {
	String host, user;
	int port;
	Session session;

	public SFTP() {
		System.out.println("new sftp");
	}

	public SFTP(Session session) {
		this.session = session;
		System.out.println("new overloaded sftp");
		SFTP_connect();
	}

	public void SFTP_connect() {
		try {
			session.connect();
			System.out.println("here");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp) channel;

			java.io.InputStream in = System.in;
			java.io.PrintStream out = System.out;

			java.util.Vector cmds = new java.util.Vector();
			byte[] buf = new byte[1024];
			int i;
			String str;
			int level = 0;

			while (true) {
				out.print("sftp> ");
				cmds.removeAllElements();
				i = in.read(buf, 0, 1024);
				if (i <= 0)
					break;

				i--;
				if (i > 0 && buf[i - 1] == 0x0d)
					i--;
				// str=new String(buf, 0, i);
				// System.out.println("|"+str+"|");
				int s = 0;
				for (int ii = 0; ii < i; ii++) {
					if (buf[ii] == ' ') {
						if (ii - s > 0) {
							cmds.addElement(new String(buf, s, ii - s));
						}
						while (ii < i) {
							if (buf[ii] != ' ')
								break;
							ii++;
						}
						s = ii;
					}
				}
				if (s < i) {
					cmds.addElement(new String(buf, s, i - s));
				}
				if (cmds.size() == 0)
					continue;

				String cmd = (String) cmds.elementAt(0);
				// String cmd="get testme.txt";

				if (cmd.equals("quit")) {
					sftp.quit();
					break;
				}
				if (cmd.equals("exit")) {
					sftp.exit();
					break;
				}

				if (cmd.equals("cd") || cmd.equals("lcd")) {
					if (cmds.size() < 2)
						continue;
					String path = (String) cmds.elementAt(1);
					try {
						if (cmd.equals("cd"))
							sftp.cd(path);
						else
							sftp.lcd(path);
					} catch (SftpException e) {
						System.out.println(e.toString());
					}
					continue;
				}

				if (cmd.equals("ls") || cmd.equals("dir")) {
					String path = ".";
					if (cmds.size() == 2)
						path = (String) cmds.elementAt(1);
					try {
						java.util.Vector vv = sftp.ls(path);
						if (vv != null) {
							for (int ii = 0; ii < vv.size(); ii++) {
								Object obj = vv.elementAt(ii);
								if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
									out.println(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
								}
							}
						}
					} catch (SftpException e) {
						System.out.println(e.toString());
					}
					continue;
				}

				if (cmd.equals("get") || cmd.equals("get testme.txt")) {
					if (cmds.size() != 2 && cmds.size() != 3)
						continue;
					String p1 = (String) cmds.elementAt(1);
					String p2 = ".";
					if (cmds.size() == 3)
						p2 = (String) cmds.elementAt(2);
					try {
						SftpProgressMonitor monitor = new MyProgressMonitor();
						if (cmd.startsWith("get")) {
							int mode = ChannelSftp.OVERWRITE;
							if (cmd.equals("get-resume")) {
								mode = ChannelSftp.RESUME;
							} else if (cmd.equals("get-append")) {
								mode = ChannelSftp.APPEND;
							}
							sftp.get(p1, p2, monitor, mode);
						} else {
							int mode = ChannelSftp.OVERWRITE;
							if (cmd.equals("put-resume")) {
								mode = ChannelSftp.RESUME;
							} else if (cmd.equals("put-append")) {
								mode = ChannelSftp.APPEND;
							}
							sftp.put(p1, p2, monitor, mode);
						}
					} catch (SftpException e) {
						System.out.println(e.toString());
					}
					continue;
				}

				if (cmd.equals("nine")) {
					String p1 = "testme.txt";
					String p2 = ".";
					try {
						SftpProgressMonitor monitor = new MyProgressMonitor();
						int mode = ChannelSftp.OVERWRITE;
						sftp.get(p1, p2, monitor, mode);
					} catch (SftpException e) {
						System.out.println(e.toString());
					}
					continue;
				}

				out.println("unimplemented command: " + cmd);
			}
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.exit(0);
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		@Override
		public String getPassword() {
			return passwd;
		}

		@Override
		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			return foo == 0;
		}

		String passwd;
		JTextField passwordField = new JPasswordField(20);

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptPassword(String message) {
			Object[] ob = { passwordField };
			int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passwd = passwordField.getText();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		@Override
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}

	public static class MyProgressMonitor implements SftpProgressMonitor {
		ProgressMonitor monitor;
		long count = 0;
		long max = 0;

		@Override
		public void init(int op, String src, String dest, long max) {
			this.max = max;
			monitor = new ProgressMonitor(null, ((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": " + src, "", 0,
					(int) max);
			count = 0;
			percent = -1;
			monitor.setProgress((int) this.count);
			monitor.setMillisToDecideToPopup(1000);
		}

		private long percent = -1;

		@Override
		public boolean count(long count) {
			this.count += count;

			if (percent >= this.count * 100 / max) {
				return true;
			}
			percent = this.count * 100 / max;

			monitor.setNote("Completed " + this.count + "(" + percent + "%) out of " + max + ".");
			monitor.setProgress((int) this.count);

			return !(monitor.isCanceled());
		}

		@Override
		public void end() {
			monitor.close();
		}
	}

	private static String help = "      Available commands:\n" + "      * means unimplemented command.\n"
			+ "cd path                       Change remote directory to 'path'\n"
			+ "lcd path                      Change local directory to 'path'\n"
			+ "chgrp grp path                Change group of file 'path' to 'grp'\n"
			+ "chmod mode path               Change permissions of file 'path' to 'mode'\n"
			+ "chown own path                Change owner of file 'path' to 'own'\n"
			+ "df [path]                     Display statistics for current directory or\n"
			+ "                              filesystem containing 'path'\n"
			+ "help                          Display this help text\n" + "get remote-path [local-path]  Download file\n"
			+ "get-resume remote-path [local-path]  Resume to download file.\n"
			+ "get-append remote-path [local-path]  Append remote file to local file\n"
			+ "hardlink oldpath newpath      Hardlink remote file\n"
			+ "*lls [ls-options [path]]      Display local directory listing\n"
			+ "ln oldpath newpath            Symlink remote file\n"
			+ "*lmkdir path                  Create local directory\n"
			+ "lpwd                          Print local working directory\n"
			+ "ls [path]                     Display remote directory listing\n"
			+ "*lumask umask                 Set local umask to 'umask'\n"
			+ "mkdir path                    Create remote directory\n" + "put local-path [remote-path]  Upload file\n"
			+ "put-resume local-path [remote-path]  Resume to upload file\n"
			+ "put-append local-path [remote-path]  Append local file to remote file.\n"
			+ "pwd                           Display remote working directory\n"
			+ "stat path                     Display info about path\n" + "exit                          Quit sftp\n"
			+ "quit                          Quit sftp\n" + "rename oldpath newpath        Rename remote file\n"
			+ "rmdir path                    Remove remote directory\n"
			+ "rm path                       Delete remote file\n"
			+ "symlink oldpath newpath       Symlink remote file\n"
			+ "readlink path                 Check the target of a symbolic link\n"
			+ "realpath path                 Canonicalize the path\n"
			+ "rekey                         Key re-exchanging\n"
			+ "compression level             Packet compression will be enabled\n"
			+ "version                       Show SFTP version\n" + "?                             Synonym for help";
}