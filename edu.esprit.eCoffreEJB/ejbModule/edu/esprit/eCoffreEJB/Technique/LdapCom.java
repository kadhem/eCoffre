package edu.esprit.eCoffreEJB.Technique;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.Entities.UTI_G;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;
import edu.esprit.eCoffreEJB.impl.CCFNManagement;

@Singleton
public class LdapCom {

	@EJB
	CCFNManagement ccfnManagement;
	
	Hashtable<String, String> env;
	private DirContext ctx;
	
	public LdapCom() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DirContext createContext()
	{
		CCFN ccfn=ccfnManagement.getCCFN();
		String url="ldap://"+ccfn.getUrlLdapServer()+":"+ccfn.getPortLdapServer()+"/"+ccfn.getDnLdapServer();
        String login=ccfn.getLoginManager();
        String password=ccfn.getPasswdManager();
        
//        String keystore = System.getProperty("java.home") + "\\\\lib\\\\security\\\\cacerts";
//        System.setProperty("javax.net.ssl.trustStore",keystore);
        
       env = new Hashtable<String, String>();
       env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        env.put(Context.SECURITY_PROTOCOL, "ssl");
       env.put(Context.PROVIDER_URL, url);
       env.put(Context.SECURITY_AUTHENTICATION, "simple");
       env.put(Context.SECURITY_PRINCIPAL, "cn="+login+","+ccfn.getDnLdapServer());
       env.put(Context.SECURITY_CREDENTIALS, password);
        
     // Create the initial context
        try {
            ctx = new InitialDirContext(env);
            System.out.println("Ldap context created !");
            return ctx;
        } catch (NamingException e) {
            System.out.println("Failed to create ldap context! : ");
            return null;
        }
	}
	
	public Map<String, Object> Login(String login, String passwd) throws NamingException
	{
		System.out.println("login");
		Map<String, Object> map=new HashMap<String, Object>();
		try {
		    SearchControls sc = new SearchControls();
		    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(mail="+login+")";
			String base="ou=People";
			NamingEnumeration<SearchResult> results = ctx.search(base, filter, sc);
			System.out.println("0000");
	        while(results.hasMoreElements())
	        {
	        	System.out.println("0001");
	        	SearchResult sr = results.next();
                Attributes attributes = sr.getAttributes();
                Attribute a = attributes.get("userPassword");
                if (a != null) {
                	System.out.println("0004 : "+new String((byte[])a.get())+" password passé :"+passwd);
                    String password = (new String((byte[])a.get()));
                    if(encryptPassword(passwd).equals(password))
                    {
                    	System.out.println("0005");
                    	a = attributes.get("employeeType");
                    	System.out.println(a.get().toString());
                    	if(a.get().toString().equals("utis"))
                    	{
                    		a = attributes.get("uid");
                        	int idUti= Integer.parseInt((String) a.get());
                    		a = attributes.get("givenName");
                        	String firstName= a.get().toString();
                        	a = attributes.get("sn");
                        	String lastName= a.get().toString();
                        	a = attributes.get("telephoneNumber");
                        	String tel= a.get().toString();
                        	a = attributes.get("postalAddress");
                        	String address= a.get().toString();
                        	a = attributes.get("description");
                        	boolean valide = Boolean.valueOf(a.get().toString());
                        	UTI_S utiS=new UTI_S(idUti, lastName, firstName, login, password, valide, tel, address);
                        	map.put("message", "found");
                        	map.put("user", utiS);
                    	}
                    	else if(a.get().toString().equals("adminf"))
                    	{
                    		a = attributes.get("uid");
                    		int idUti= Integer.parseInt((String) a.get());
                    		a = attributes.get("givenName");
                        	String firstName= a.get().toString();
                        	a = attributes.get("sn");
                        	String lastName= a.get().toString();
                        	a = attributes.get("description");
                        	boolean valide = Boolean.valueOf(a.get().toString());
                        	UTI_F utiF = new UTI_F(idUti, lastName, firstName, login, password, valide);
                        	map.put("message", "found");
                        	map.put("user", utiF);
                    	}
                    	else
                    	{
                    		a = attributes.get("uid");
                    		int idUti= Integer.parseInt((String) a.get());
                    		a = attributes.get("givenName");
                        	String firstName= a.get().toString();
                        	a = attributes.get("sn");
                        	String lastName= a.get().toString();
                        	a = attributes.get("description");
                        	boolean valide = Boolean.valueOf(a.get().toString());
                        	UTI_G utiG = new UTI_G(idUti, lastName, firstName, login, password, valide);
                        	map.put("message", "found");
                        	map.put("user", utiG);
                    	}
                        return map;
                    }
                    else
                    {
                    	System.out.println("0002");
                    	map.put("message", "!found");
                        return map;
                    }
                } else {
                	System.out.println("0003");
                    System.out.println("Cannot get data");
                    map.put("message", "!found");
                    return map;
                }
	        }
		    // Close the context when we're done
		    ctx.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("message", "error");
			return map;
		}
		map.put("message", "error");
		return map;
		
	}
    
	private String getUserDN(String id) {
        return new StringBuffer()
                .append("uid=")
                .append(id)
                .append(",")
                .append("ou=People")
                .toString();
    }
	
	public boolean addUser(Utilisateur utilisateur)
    
    	{
			// Create a container set of attributes
			Attributes container = new BasicAttributes();
			// Create the objectclass to add
			Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("inetOrgPerson");
			
			// Assign the username, first name, and last name
			String cnValue = new StringBuffer(utilisateur.getFirstName())
			.append(" ")
			.append(utilisateur.getLastName())
			.toString();
			
//			Attribute serialNumber = new BasicAttribute("serialNumber", String.valueOf(utiS.getID_UTI()));
			Attribute cn = new BasicAttribute("cn", cnValue);
			Attribute givenName = new BasicAttribute("givenName", utilisateur.getFirstName());
			Attribute sn = new BasicAttribute("sn", utilisateur.getLastName());
			Attribute mail = new BasicAttribute("mail", utilisateur.getUserName());
			Attribute description = new BasicAttribute("description", String.valueOf(utilisateur.isValide()));
			
			Attribute type = null;
			Attribute phone = null;
			Attribute adress = null;
			if(utilisateur instanceof UTI_S)
			{
				type = new BasicAttribute("employeeType", "utis");
				phone = new BasicAttribute("telephoneNumber", ((UTI_S) utilisateur).getTel());
		        adress = new BasicAttribute("postalAddress", ((UTI_S) utilisateur).getAdresse());
			}
			else if(utilisateur instanceof UTI_F)
			{
				type = new BasicAttribute("employeeType", "adminf");
			}
			else
			{
				type = new BasicAttribute("employeeType", "adming");
			}
	        
	        
			// Add password
			Attribute userPassword = new BasicAttribute("userpassword", encryptPassword(utilisateur.getPassword()));
			
			// Add these to the container
			container.put(objClasses);
//			container.put(serialNumber);
			container.put(cn);
			container.put(sn);
			container.put(givenName);
			container.put(mail);
			container.put(description);
			container.put(type);
			container.put(userPassword);
			if(phone!=null && adress!=null)
			{
				container.put(phone);
				container.put(adress);
			}
			
			// Create the entry
			try {
				ctx.createSubcontext(getUserDN(String.valueOf(utilisateur.getIdUti())), container);
				return true;
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
    
	public boolean deleteUser(int idUti) 
    {
        try {
            ctx.destroySubcontext(getUserDN(String.valueOf(idUti)));
            return true;
        } catch (NamingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
	public boolean editUser(UTI_S utiS)
    {
    	try
        {
            ModificationItem[] mods = new ModificationItem[7];
            String cnValue = new StringBuffer(utiS.getFirstName())
			.append(" ")
			.append(utiS.getLastName())
			.toString();
            Attribute mod0 = new BasicAttribute("userPassword", encryptPassword(utiS.getPassword()));
            Attribute mod1 = new BasicAttribute("postalAddress", utiS.getAdresse());
            Attribute mod2 = new BasicAttribute("mail", utiS.getUserName());
            Attribute mod3 = new BasicAttribute("telephoneNumber", utiS.getTel());
            Attribute mod4 = new BasicAttribute("givenName", utiS.getFirstName());
            Attribute mod5 = new BasicAttribute("sn", utiS.getLastName());
            Attribute mod6 = new BasicAttribute("cn", cnValue);
            
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod1);
            mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod2);
            mods[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod3);
            mods[4] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod4);
            mods[5] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod5);
            mods[6] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod6);

            System.out.println("avant modification en ldap");
            ctx.modifyAttributes(getUserDN(String.valueOf(utiS.getIdUti())), mods);
            System.out.println("après modification en ldap");
            return true;
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
	
	public boolean enableOrDisableUser(Utilisateur utilisateur)
	{
		try
        {
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod0 = new BasicAttribute("description", String.valueOf(utilisateur.isValide()));
            System.out.println("valide : "+utilisateur.isValide());
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);

            System.out.println("avant modification en ldap");
            ctx.modifyAttributes(getUserDN(String.valueOf(utilisateur.getIdUti())), mods);
            System.out.println("après modification en ldap");
            return true;
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
	}
    
    public String encryptPassword(String passwd)
    {
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(passwd.getBytes());
			 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	String result = "{SHA512}" + hexString;
	    	System.out.println("userpassword in LDAP:" + result);
	    	return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static void main(String args[]){
    	
    	Hashtable<String, String> env;
    	DirContext ctx = null;
    	
    	String url="ldap://192.168.1.251:389/dc=esprit,dc=com";
        String login="cn=Manager,dc=esprit,dc=com";
        String password="ldap";
        
//        String keystore = System.getProperty("java.home") + "\\\\lib\\\\security\\\\cacerts";
//        System.setProperty("javax.net.ssl.trustStore",keystore);
        
       env = new Hashtable<String, String>();
       env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        env.put(Context.SECURITY_PROTOCOL, "ssl");
       env.put(Context.PROVIDER_URL, url);
       env.put(Context.SECURITY_AUTHENTICATION, "simple");
       env.put(Context.SECURITY_PRINCIPAL, login);
       env.put(Context.SECURITY_CREDENTIALS, password);
        
     // Create the initial context
        try {
            ctx = new InitialDirContext(env);
            System.out.println("Ldap context created !");
        } catch (NamingException e) {
            System.out.println("Failed to create ldap context! : ");
        }
    	
    	System.out.println("login");
		Map<String, Object> map=new HashMap<String, Object>();
		try {
		    SearchControls sc = new SearchControls();
		    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(mail=k)";
			String base="ou=People";
			NamingEnumeration<SearchResult> results = ctx.search(base, filter, sc);
			System.out.println("0000");
	        while(results.hasMoreElements())
	        {
	        	System.out.println("0001");
	        	SearchResult sr = results.next();
                Attributes attributes = sr.getAttributes();
                Attribute a = attributes.get("userPassword");
                if (a != null) {
                	System.out.println("0004 : "+new String((byte[])a.get()));
                    password = (new String((byte[])a.get()));
                    if("k".equals(password))
                    {
                    	System.out.println("0005"+attributes.get("givenName")+"*"+attributes.get("employeeType"));
                    }
                    else
                    {
                    	System.out.println("0002");
                    }
                } else {
                	System.out.println("0003");
                    System.out.println("Cannot get data");
                }
	        }
		    // Close the context when we're done
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Attributes container = new BasicAttributes();
		// Create the objectclass to add
		Attribute objClasses = new BasicAttribute("objectClass");
		objClasses.add("inetOrgPerson");
		
		// Assign the username, first name, and last name
		String cnValue = new StringBuffer("test")
		.append(" ")
		.append("test")
		.toString();
		
//		Attribute serialNumber = new BasicAttribute("serialNumber", String.valueOf(utiS.getID_UTI()));
		Attribute cn = new BasicAttribute("cn", cnValue);
		Attribute givenName = new BasicAttribute("givenName", "test");
		Attribute sn = new BasicAttribute("sn", "test");
		Attribute mail = new BasicAttribute("mail", "test");
        Attribute phone = new BasicAttribute("telephoneNumber", "258");
        Attribute adress = new BasicAttribute("postalAddress", "gr");
        
		// Add password
		Attribute userPassword = new BasicAttribute("userpassword", "test");
		
		// Add these to the container
		container.put(objClasses);
//		container.put(serialNumber);
		container.put(cn);
		container.put(sn);
		container.put(givenName);
		container.put(mail);
		container.put(userPassword);
		container.put(phone);
		container.put(adress);
		
		
		// Create the entry
		try {
			ctx.createSubcontext("uid="+2+",ou=People", container);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//      ILdapComLocal a=new LdapCom();
//      a.createContext();
//      System.out.println("1");
//      a.encryptPassword("root");
//      HashMap<String, String> hashMap=new HashMap<String, String>();
//      hashMap.put("userPassword", a.encryptPassword("root"));
//      a.editUser("kadhemk@gmail.com",hashMap);
//      a.deleteUser("test");
//      try {
//		System.out.println(a.Login("kadhemk@gmail.com","test"));
//	} catch (NamingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
      
//		a.addUser("kadhemk@gmail.com", "test", "test", "test","22936773");
    }
    
    
}