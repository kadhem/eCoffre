package edu.esprit.eCoffreEJB.Technique;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import javax.ejb.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;

@Singleton
public class Log {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	
	
	public static void main(String[] args) {
		
		Logger log4j = LogManager.getLogger(Log.class 
		        .getName());
		MapMessage mapMessage = new MapMessage();
		mapMessage.put("test", "salut");
		mapMessage.put("id", "2");
		log4j.debug(mapMessage);
//		Handler handler = new FileHandler("test1.log", 1000, 3);
//		Logger.getLogger("test").addHandler(handler);
//		Logger.getLogger("test").info("test test");
		File file=new File("logs/app.log"); 

		try {
			FileInputStream fin = new FileInputStream(file);
			SFTPCom com = new SFTPCom();
			com.createContext();
			com.uploadFile(fin, file.getName());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Scanner read;
		try {
			read = new Scanner(file);
			while (read.hasNextLine()){ 
				String line=read.nextLine(); 
				if(line.contains("id=\"2\""))
				{
					System.out.println(line);
				}
				
			}
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Log() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public void log()
	{
		System.out.println("log");
		Logger log4j = LogManager.getLogger(Log.class 
		        .getName());
		MapMessage mapMessage = new MapMessage();
		mapMessage.put("test", "salut");
		mapMessage.put("id", "2");
		log4j.debug(mapMessage);
//		Handler handler = new FileHandler("test1.log", 1000, 3);
//		Logger.getLogger("test").addHandler(handler);
//		Logger.getLogger("test").info("test test");
//		File file=new File("../logs/app.log"); 
//
//		try {
//			FileInputStream fin = new FileInputStream(file);
//			SFTPCom com = new SFTPCom();
//			com.createContext();
//			com.uploadFile(fin, file.getName());
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		
//		Scanner read;
//		try {
//			read = new Scanner(file);
//			while (read.hasNextLine()){ 
//				String line=read.nextLine(); 
//				if(line.contains("id=\"2\""))
//				{
//					System.out.println(line);
//				}
//				
//			}
//			} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
