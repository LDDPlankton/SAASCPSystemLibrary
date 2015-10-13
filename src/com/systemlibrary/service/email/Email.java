package com.systemlibrary.service.email;

import com.systemlibrary.pkg.errormanager.ErrorManager;

public class Email
{
	private ErrorManager errorManager = null;
	private LocalEmail localEmail;
	private SystemEmail systemEmail;
	
	public Email()
	{
		this.errorManager = new ErrorManager();
		this.localEmail = new LocalEmail();
		this.systemEmail = new SystemEmail();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean addEmailAccount(String email, String password)
	{
		boolean status;
		String[] parts = email.split("@");
		String username = parts[0];
		String domain = parts[1];
		
		//ADD EMAIL TO SYSTEM
		status = this.systemEmail.addEmailAccount(email, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.systemEmail.getErrorInformation().getErrorMessage());
			return false;			
		}
		
		//ADD LOCAL EMAIL ACCOUNT
		status = this.localEmail.addLocalEmailAccount(username, password, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.localEmail.getErrorInformation().getErrorMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean deleteEmailAccount(String email)
	{
		boolean status;
		String[] parts = email.split("@");
		String username = parts[0];
		String domain = parts[1];
		
		//DELETE EMAIL TO SYSTEM
		status = this.systemEmail.deleteEmailAccount(email, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.systemEmail.getErrorInformation().getErrorMessage());
			return false;			
		}
		
		//DELETE LOCAL EMAIL ACCOUNT
		status = this.localEmail.deleteLocalEmailAccount(username, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.localEmail.getErrorInformation().getErrorMessage());
			return false;
		}
		
		return true;
	}
	
	
	
}
