package com.systemlibrary.service.email;

public class MailBoxAliasDSO
{
	public String emailSrc;
	public String emailDest;
	
	public MailBoxAliasDSO()
	{
		this.emailSrc = "";
		this.emailDest = "";
	}
	
	/*
	 * This function will build the line to write to the file from the contents of this DSO.
	 * Ex: info@mydomain.com	info@mydomain.com
	 * 
	 * @return	String
	 */
	public String generateContentToWriteToFile()
	{
		String line = String.format("%s\t%s", this.emailSrc, this.emailDest);
		return line;
	}
	
}
