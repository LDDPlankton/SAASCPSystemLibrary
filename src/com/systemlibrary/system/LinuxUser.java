package com.systemlibrary.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

import com.ld.libcmdexecutor.CommandExecutor;
import com.ld.sharedlibrary.lib.ErrorManager;

public class LinuxUser
{
	private ErrorManager errorManager = null;
	
	public LinuxUser()
	{
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean addSystemAccount(String user, String passwd, String domain)
	{
		String basePath = String.format("/home/%s", domain);
		CommandExecutor cmd = new CommandExecutor();
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser != null)
		{
			System.out.println(String.format("This user already exists!"));
			return false;
		}
		
		//GET SYSTEM DOVECOT USER BASED ON DOMAIN
		String sysDoveCotUser = UnixSystem.getUserFromPasswdFile("dovecot");
		if(sysDoveCotUser == null)
		{
			System.out.println(String.format("We could not find any user associated with %s", "dovecot"));
			return false;
		}
		
		//GET UID/GID FROM DOVECOT
		int[] duid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysDoveCotUser);
		int duid = duid_gid_pair[0];
		int dgid = duid_gid_pair[1];
		
		//ADD GROUP
		status = cmd.runCommand(String.format("groupadd %s", user), 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error Adding Group: %s", cmd.getErrorMessage() ));
			return false;
		}

		//ADD USER
		status = cmd.runCommand(String.format("useradd -M -s /sbin/nologin -d %s -g %s %s", basePath, user, user), 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error Adding User: %s", cmd.getErrorMessage() ));
			return false;
		}
		
		//GET ENCRYPTED PWD
		String strcmd = String.format("openssl passwd %s", passwd);
		status = cmd.runCommand(strcmd, 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error generating Encrypted Password: ", cmd.getErrorMessage() ));
			return false;
		}
		String enc_pwd = cmd.getCommandOutput();

		//SET PWD
		status = cmd.runCommand(String.format("usermod -p %s %s", enc_pwd, user), 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error Setting Passwd: %s", cmd.getErrorMessage() ));
			return false;
		}
		
		//GET SYSTEM USER BASED ON DOMAIN
		sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			System.out.println(String.format("Failed to add user due to user not existing!"));
			return false;
		}
		
		//GET UID/GID
		int[] uid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysUser);
		int uid = uid_gid_pair[0];
		int gid = uid_gid_pair[1];
	
		//BUILD FILE OBJ
		File fd1 = new File(basePath);
		File fd2 = new File(basePath + File.separator + "etc");
		File fd3 = new File(basePath + File.separator + "etc" + File.separator + domain);
		File fd4 = new File(basePath + File.separator + "mail");
		File fd5 = new File(basePath + File.separator + "mail" + File.separator + domain);
		File fd6 = new File(basePath + File.separator + "etc" + File.separator + domain + File.separator + "passwd");
		File fd7 = new File(basePath + File.separator + "etc" + File.separator + domain + File.separator + "shadow");
		File fd8 = new File(basePath + File.separator + "public_html");
		
		//MKDIR(S)
		boolean md1 = new File(basePath).mkdir();
		if(!md1)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md1]") );
		boolean md2 = new File(basePath + File.separator + "etc").mkdir();
		if(!md2)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md2]") );
		boolean md3 = new File(basePath + File.separator + "etc" + File.separator + domain).mkdir();
		if(!md3)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md3]") );
		boolean md4 = new File(basePath + File.separator + "mail").mkdir();
		if(!md4)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md4]") );
		boolean md5 = new File(basePath + File.separator + "mail" + File.separator + domain).mkdir();
		if(!md5)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md5]") );
		boolean md8 = new File(fd8.getAbsolutePath()).mkdir();
		if(!md8)
			this.errorManager.setErrorMessage( String.format("Failed creating directories, due to some existing! [md8]") );
		
		//SET PERMISSIONS [EXECUTABLE, OWNER ONLY]
		fd1.setReadable(true, true);	//711
		fd1.setWritable(true, true);
		fd1.setExecutable(true, true);

		fd2.setReadable(true, false);	//755	//751?
		fd2.setWritable(true, true);
		fd2.setExecutable(true, false);
		
		fd3.setReadable(true, false);	//755	//750?
		fd3.setWritable(true, true);
		fd3.setExecutable(true, false);
		
		fd4.setReadable(true, false);	//755
		fd4.setWritable(true, true);
		fd4.setExecutable(true, false);
		
		fd5.setReadable(true, false);	//755	//751 should be?
		fd5.setWritable(true, true);
		fd5.setExecutable(true, false);
		
		fd8.setReadable(true, true);	//711
		fd8.setWritable(true, true);
		fd8.setExecutable(true, true);
		
		//BUILD PATH TO PASSWD + SHADOW FILE
		String passwd_file = String.format("/home/%s/etc/%s/passwd", domain, domain);
		String shadow_file = String.format("/home/%s/etc/%s/shadow", domain, domain);
		
		//CREATE EMPTY SHADOW/PWD FILE
		File ef1 = new File(passwd_file);
		File ef2 = new File(shadow_file);
		try
		{
			ef1.createNewFile();
			ef2.createNewFile();
		}
		catch (IOException e)
		{

		}
		
		//CHOWN FILE/DIRS
		status = this.setUserGroup(fd1.getAbsolutePath(), user, user);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd1.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd2.getAbsolutePath(), user, "dovecot");
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd2.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd3.getAbsolutePath(), user, "dovecot");
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd3.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd4.getAbsolutePath(), user, user);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd4.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd5.getAbsolutePath(), user, user);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd5.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd6.getAbsolutePath(), user, "dovecot");
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd6.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd7.getAbsolutePath(), user, "dovecot");
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd7.getAbsolutePath()));
			return false;
		}
		status = this.setUserGroup(fd8.getAbsolutePath(), user, user);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Failed to chown directories [%s]", fd8.getAbsolutePath()));
			return false;
		}
		
		return true;
	}
	
	public boolean deleteSystemAccount(String user, String domain)
	{
		CommandExecutor cmd = new CommandExecutor();
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			System.out.println(String.format("This user does not exist!"));
			return false;
		}
		
		//DELETE USER
		status = cmd.runCommand(String.format("userdel %s", user), 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error Deleting User: %s", cmd.getErrorMessage() ));
			return false;
		}
		/*
		//DELETE GROUP
		status = cmd.runCommand(String.format("groupdel %s", user), 0, false);
		if(!status)
		{
			this.errorManager.setErrorMessage(String.format("Error Deleting Group: %s", cmd.getErrorMessage() ));
			return false;
		}
		*/
		return true;
	}
	
	public boolean setUserGroup(String file, String user, String group)
	{
		File newFile = new File(file);
		UserPrincipalLookupService service = FileSystems.getDefault().getUserPrincipalLookupService();
		PosixFileAttributeView view = Files.getFileAttributeView(newFile.toPath(), PosixFileAttributeView.class);
			
		try
		{
			//CONVERT STRING TO USER PRINCIPLE
			UserPrincipal userPrinciple = service.lookupPrincipalByName(user);
			
			//CONVERT STRING TO GROUP PRINCIPLE
			GroupPrincipal groupPrinciple = service.lookupPrincipalByGroupName(group);
			
			view.setOwner(userPrinciple);
			view.setGroup(groupPrinciple);
			
			return true;
		}
		catch (IOException e)
		{
			this.errorManager.setErrorMessage("Unable to set user/group perm: " + e.getMessage());
			return false;
		}
	}


	
}
