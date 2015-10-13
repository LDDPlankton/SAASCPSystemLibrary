package com.systemlibrary.system;

import com.ld.libcmdexecutor.CommandExecutor;
import com.systemlibrary.service.email.EmailUtil;

public class LinuxService
{
	public LinuxService()
	{
		
	}
	
	public static boolean reloadHttpd()
	{
		CommandExecutor cmd = new CommandExecutor();
		boolean status;
		
		//POSTMAP GENERATION
		status = cmd.runCommand("systemctl reload httpd", 0, false);
		if(!status)
		{
			System.out.println( String.format("Error Restarting Httpd: ", cmd.getErrorMessage() ) );
			return false;
		}
		
		return true;
	}
	
	public static boolean reloadNamed()
	{
		CommandExecutor cmd = new CommandExecutor();
		boolean status;
		
		//POSTMAP GENERATION
		status = cmd.runCommand("systemctl reload named", 0, false);
		if(!status)
		{
			System.out.println( String.format("Error Restarting Named: ", cmd.getErrorMessage() ) );
			return false;
		}
		
		return true;
	}
	
	public static boolean reloadPostFix()
	{
		CommandExecutor cmd = new CommandExecutor();
		boolean status;
		
		//POSTMAP GENERATION
		status = cmd.runCommand("postmap " + EmailUtil.vmail_aliases_file, 0, false);
		if(!status)
		{
			System.out.println( String.format("Error PostMap[1]: ", cmd.getErrorMessage() ) );
			return false;
		}
		status = cmd.runCommand("postmap " + EmailUtil.vmail_domains_file, 0, false);
		if(!status)
		{
			System.out.println( String.format("Error PostMap[2]: ", cmd.getErrorMessage() ) );
			return false;
		}
		status = cmd.runCommand("postmap " + EmailUtil.vmail_mailbox_file, 0, false);
		if(!status)
		{
			System.out.println( String.format("Error PostMap[3]: ", cmd.getErrorMessage() ) );
			return false;
		}
		status = cmd.runCommand("postmap " + EmailUtil.vmail_virtual_uid, 0, false);
		if(!status)
		{
			System.out.println( String.format("Error PostMap[4]: ", cmd.getErrorMessage() ) );
			return false;
		}
		
		//RESTART POSTFIX
		status = cmd.runCommand("systemctl reload postfix", 0, false);
		if(!status)
		{
			System.out.println( String.format("Error Restarting Postfix: ", cmd.getErrorMessage() ) );
			return false;
		}
		
		return true;
	}
}
