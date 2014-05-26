package edu.esprit.eCoffreEJB.Technique;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashFile {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
		
	public StringBuffer getEncryptedFileSHA1(InputStream file) 
	{
		MessageDigest md;
		StringBuffer hexString = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] dataBytes = new byte[1024];
			 
	        int nread = 0; 
	        while ((nread = file.read(dataBytes)) != -1) {
	          md.update(dataBytes, 0, nread);
	        };
	        byte[] mdbytes = md.digest();
	 
	       //convert the byte to hex format method 2
	        hexString = new StringBuffer();
	    	for (int i=0;i<mdbytes.length;i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}
	    	System.out.println("Hex format : " + hexString.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return hexString;
	}
	
	public StringBuffer getEncryptedFileSHA256(InputStream inputStream) 
	{
		MessageDigest md;
		StringBuffer hexString = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] dataBytes = new byte[1024];
			 
	        int nread = 0; 
	        while ((nread = inputStream.read(dataBytes)) != -1) {
	          md.update(dataBytes, 0, nread);
	        };
	        byte[] mdbytes = md.digest();
	 
	       //convert the byte to hex format method 2
	        hexString = new StringBuffer();
	    	for (int i=0;i<mdbytes.length;i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}
	    	System.out.println("Hex format : " + hexString.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return hexString;
	}

	public StringBuffer getEncryptedFileSHA512(InputStream file) 
	{
		MessageDigest md;
		StringBuffer hexString = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
			byte[] dataBytes = new byte[1024];
			 
	        int nread = 0; 
	        while ((nread = file.read(dataBytes)) != -1) {
	          md.update(dataBytes, 0, nread);
	        };
	        byte[] mdbytes = md.digest();
	 
	       //convert the byte to hex format method 2
	        hexString = new StringBuffer();
	    	for (int i=0;i<mdbytes.length;i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}
	    	System.out.println("Hex format : " + hexString.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return hexString;
	}
}
