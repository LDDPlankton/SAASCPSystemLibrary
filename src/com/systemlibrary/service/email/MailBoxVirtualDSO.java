package com.systemlibrary.service.email;

public class MailBoxVirtualDSO
{
	public String virtualEmail;
	public int virtualUid;
	
	public MailBoxVirtualDSO()
	{
		this.virtualEmail = "";
		this.virtualUid = -1;
	}
	
	/*
	 * This function will build the line to write to the file from the contents of this DSO.
	 * Ex: info@mydomain.com	5001
	 * 
	 * @return	String
	 */
	public String generateContentToWriteToFile()
	{
		String line = String.format("%s\t%d", this.virtualEmail, this.virtualUid);
		return line;
	}
	
}
