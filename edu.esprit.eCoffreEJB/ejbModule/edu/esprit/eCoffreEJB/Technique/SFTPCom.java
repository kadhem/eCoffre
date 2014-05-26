package edu.esprit.eCoffreEJB.Technique;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.impl.CCFNManagement;

@Singleton
public class SFTPCom {

	@EJB
	CCFNManagement ccfnManagement;

	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	public SFTPCom() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#createContext()
	 */
	public boolean createContext() {
		CCFN ccfn = ccfnManagement.getCCFN();
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(ccfn.getLoginFileServer(),
					ccfn.getUrlFileServer(), ccfn.getPortFileServer());
			session.setPassword(ccfn.getPasswdFileServer());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(ccfn.getDirFileServer());
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
			destroyContext();
			return false;
		} catch (SftpException e) {
			e.printStackTrace();
			destroyContext();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#destroyContext()
	 */
	public boolean destroyContext() {
		try {
			if (channelSftp.isConnected()) {
				channelSftp.disconnect();
			}
			if (channel.isConnected()) {
				channel.disconnect();
			}
			if (session.isConnected()) {
				session.disconnect();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createPersonalDir(String dir) {
		try {
			channelSftp.mkdir(dir);
			// changeDir(dir);

			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeDir(String dir) {
		try {
			channelSftp.cd(dir);
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			for (ChannelSftp.LsEntry entry : list) {
				channelSftp.rm(entry.getFilename());
			}
			System.out.println("dir : " + dir);
			channelSftp.cd("..");
			channelSftp.rmdir(dir);
			return true;
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean changeDir(String dir) {
		try {
			channelSftp.cd(dir);
			System.out.println("directory : " + channelSftp.pwd());
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#uploadFile(java.lang.String
	 * , java.lang.String)
	 */
	public Boolean uploadFile(InputStream in, String fileName) {

		try {

//			try {
//				if (in instanceof FileInputStream) {
//					((FileInputStream) in).getChannel().position();
//				} else if (in instanceof SequenceInputStream) {
//					
//				} else {
//					in.reset();
//				}
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if (!fileExist(fileName)) {
				channelSftp.put(in, fileName);
				return true;
			}
			return false;
		} catch (SftpException ex) {
			ex.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		// OutputStream outputStream = null;
		// try {
		// outputStream = new FileOutputStream(new File("d:/testnew1.txt"));
		// int read = 0;
		// byte[] bytes = new byte[1024];
		//
		// while ((read = in.read(bytes)) != -1) {
		// outputStream.write(bytes, 0, read);
		// }
		//
		// System.out.println("Done!");
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// outputStream.close();
		// in.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("fin");
		// return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#downloadFile(java.lang.
	 * String)
	 */
	public Boolean downloadFile(String fileName) {
		try {
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			for (ChannelSftp.LsEntry entry : list) {
				if (fileName.equals(entry.getFilename())) {
					channelSftp.get(entry.getFilename(),
							"d:/temp/" + entry.getFilename());
					return true;
				}
			}
			return false;
		} catch (SftpException ex) {
			System.out.println(ex.getMessage());
			return false;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#deleteFile(java.lang.String
	 * )
	 */
	public Boolean deleteFile(String fileName) {
		try {
			channelSftp.rm(fileName);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#fileExist(java.lang.String)
	 */
	public boolean fileExist(String fileName) {

		try {
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			for (ChannelSftp.LsEntry entry : list) {
				if (fileName.equals(entry.getFilename())) {
					return true;
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#getInputStreamFile(java
	 * .lang.String)
	 */
	public InputStream getInputStreamFile(String fileName) {
		try {
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			for (ChannelSftp.LsEntry entry : list) {
				if (fileName.equals(entry.getFilename())) {
					return channelSftp.get(entry.getFilename());
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#getFileSize(java.lang.String
	 * )
	 */
	public long getFileSize(String fileName) {
		try {
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.*");
			SftpATTRS attrs;
			for (ChannelSftp.LsEntry entry : list) {
				if (entry.getFilename().equals(fileName)) {
					System.out.println("*F*");
					attrs = entry.getAttrs();
					System.out.println("file size : " + attrs.getSize());
					return attrs.getSize();
				}
			}
		} catch (SftpException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.esprit.eCoffreEJB.Technique.ISFTPComLocal#countFile(java.lang.String)
	 */
	public int countFile() throws SftpException {
		int count = 0;
		Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
		for (ChannelSftp.LsEntry entry : list) {
			count++;
		}
		return count;
	}

	public long getUsedSpace() {
		long usedSpace = 0;
		try {
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			SftpATTRS attrs;
			for (ChannelSftp.LsEntry entry : list) {
				attrs = entry.getAttrs();
				if (!attrs.isDir()) {
					System.out.println("fichier : " + entry.getFilename()
							+ " size : " + attrs.getSize());
					usedSpace = usedSpace + attrs.getSize();

				}
			}
			System.out.println("****----****" + usedSpace + "***---***");
			return usedSpace;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return usedSpace;
	}

	public static void main(String[] args) {
		SFTPCom s = new SFTPCom();
		s.createContext();
		try {
			FileInputStream fin = new FileInputStream("d:/test.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
