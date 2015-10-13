package com.systemlibrary.service.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.ld.libcmdexecutor.CommandExecutor;

public class EmailUtil
{
	public static String vmail_aliases_file = "/etc/postfix/my_vmail_aliases";	///etc/postfix/
	public static String vmail_domains_file = "/etc/postfix/my_vmail_domains";
	public static String vmail_mailbox_file = "/etc/postfix/my_vmail_mailbox";
	public static String vmail_virtual_uid = "/etc/postfix/my_vmail_virtual_id";
	
	public EmailUtil()
	{
		
	}
	
	public static boolean isFileExist(String name)
	{
		if(new File(name).isFile())
			return true;
		return false;
	}
	
	public static boolean isRequiredFilesExist()
	{
		if(!isFileExist(vmail_aliases_file))
			return false;
		if(!isFileExist(vmail_domains_file))
			return false;
		if(!isFileExist(vmail_mailbox_file))
			return false;
		if(!isFileExist(vmail_virtual_uid))
			return false;
		return true;
	}
	
}
