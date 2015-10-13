package com.systemlibrary.service.email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.ld.libcmdexecutor.CommandExecutor;
import com.ld.sharedlibrary.lib.ErrorManager;
import com.systemlibrary.system.UnixSystem;

public class LocalEmail
{
	private ErrorManager errorManager = null;
	
	public LocalEmail()
	{
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	/*
	 * This function will parse a domains passwd file for email accounts in it's /etc folder to build a List<>
	 * 
	 * @param	passwd_file		The path to the passwd file.
	 * @return	List<String>
	 */
	public List<String> getPasswdList(String passwd_file)
	{
		List<String> infoList = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(passwd_file) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				infoList.add(line.trim());
			}
			return infoList;
		}
		catch (Exception e)
		{
			this.errorManager.setErrorMessage("Unable to get PasswdList: " + e.getMessage());
			return null;
		}
	}
	/*
	 * This function will parse a domains shadow file for email accounts in it's /etc folder to build a List<>
	 * 
	 * @param	shadow_file		The path to the shadow file.
	 * @return	List<String>
	 */
	public List<String> getShadowList(String shadow_file)
	{
		List<String> infoList = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(shadow_file) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				infoList.add(line.trim());
			}
			return infoList;
		}
		catch (Exception e)
		{
			this.errorManager.setErrorMessage("Unable to get ShadowList: " + e.getMessage());
			return null;
		}
	}
	
	/*
	 * This function will update a domains Passwd file.
	 * 
	 * @param	passwd_file		The path to the passwd file.
	 * @param	updateList		The list of lines to write.
	 * @return	Boolean
	 */
	public boolean updatePasswdFile(String passwd_file, List<String> updateList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(passwd_file) );
			for(String i : updateList)
			{
				String tmp = String.format("%s\n", i);
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update PasswdList: " + e.getMessage());
			return false;
		}		
	}
	
	/*
	 * This function will update a domains Shadow file.
	 * 
	 * @param	shadow_file		The path to the shadow file.
	 * @param	updateList		The list of lines to write.
	 * @return	Boolean
	 */
	public boolean updateShadowFile(String shadow_file, List<String> updateList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(shadow_file) );
			for(String i : updateList)
			{
				String tmp = String.format("%s\n", i);
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update ShadowList: " + e.getMessage());
			return false;
		}		
	}
	
	public boolean addLocalEmailAccount(String username, String password, String domain)
	{
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			System.out.println(String.format("We could not find any user associated with %s", domain));
			return false;
		}
		
		//GET UID/GID
		int[] uid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysUser);
		int uid = uid_gid_pair[0];
		int gid = uid_gid_pair[1];

		//BUILD PATH TO PASSWD + SHADOW FILE
		String passwd_file = String.format("/home/%s/etc/%s/passwd", domain, domain);
		String shadow_file = String.format("/home/%s/etc/%s/shadow", domain, domain);
		
		//GET ENCRYPTED PWD
		String strcmd = String.format("doveadm pw -s MD5-CRYPT -p %s", password);
		CommandExecutor cmd = new CommandExecutor();
		status = cmd.runCommand(strcmd, 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error generating Password: ", cmd.getErrorMessage() ));
			return false;
		}
		String enc_pwd = cmd.getCommandOutput();

		//BUILD PWD/SHADOW LIST
		List<String> passwdList = this.getPasswdList(passwd_file);
		List<String> shadowList = this.getShadowList(shadow_file);

		//CHECK IF USER EXISTS IN PASSWD + SHADOW LIST AND IF SO ... EXIT
		for(String i : passwdList)
		{
			String parts[] = i.split(":");
			String luser = parts[0];
			if(luser.equals(username))
			{
				this.errorManager.setErrorMessage(String.format("Error: %s@%s exists [passwd]!", username, domain));
				return false;
			}
			
		}
		for(String i : shadowList)
		{
			String parts[] = i.split(":");
			String luser = parts[0];
			if(luser.equals(username))
			{
				this.errorManager.setErrorMessage(String.format("Error: %s@%s exists [shadow]!", username, domain));
				return false;
			}			
		}
		
		//NOW WE CAN GENERATE THE NEW PASSWD/SHADOW HASH TO BE ADDED
		String str_passwd = String.format("%s:x:%d:%d::/home/%s/mail/%s/%s:/sbin/nologin", username, uid, gid, domain, domain, username);
		String str_shadow = String.format("%s:%s:::::::", username, enc_pwd);
		passwdList.add(str_passwd);
		shadowList.add(str_shadow);

		//UPDATE PWD + SHADOW FILE
		status = this.updatePasswdFile(passwd_file, passwdList);
		if(!status)
			return false;
		status = this.updateShadowFile(shadow_file, shadowList);
		if(!status)
			return false;
		
		return true;
	}
	
	public boolean deleteLocalEmailAccount(String username, String domain)
	{
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			System.out.println(String.format("We could not find any user associated with %s", domain));
			return false;
		}
		
		//GET UID/GID
		int[] uid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysUser);
		int uid = uid_gid_pair[0];
		int gid = uid_gid_pair[1];

		//BUILD PATH TO PASSWD + SHADOW FILE
		String passwd_file = String.format("/home/%s/etc/%s/passwd", domain, domain);
		String shadow_file = String.format("/home/%s/etc/%s/shadow", domain, domain);

		//BUILD PWD/SHADOW LIST
		List<String> passwdList = this.getPasswdList(passwd_file);
		List<String> shadowList = this.getShadowList(shadow_file);
		
		//CHECK IF USER EXIST [IN SHADOW/PASSWD FILE] + REMOVE USER
		boolean foundPasswd = false;
		boolean foundShadow = false;
		String str_pwd = "";
		String str_shadow = "";
		for(String i : passwdList)
		{
			String parts[] = i.split(":");
			String luser = parts[0];
			if(luser.equals(username))
			{
				foundPasswd = true;
				str_pwd = i;
			}
		}
		for(String i : shadowList)
		{
			String parts[] = i.split(":");
			String luser = parts[0];
			if(luser.equals(username))
			{
				foundShadow = true;
				str_shadow = i;
			}
		}
		if(foundPasswd)
			passwdList.remove(str_pwd);
		if(foundShadow)
			shadowList.remove(str_shadow);
		
		//UPDATE PWD + SHADOW FILE
		status = this.updatePasswdFile(passwd_file, passwdList);
		if(!status)
			return false;
		status = this.updateShadowFile(shadow_file, shadowList);
		if(!status)
			return false;
		
		return true;
	}
	
}
