package com.systemlibrary.appmanager;

import com.systemlibrary.pkg.errormanager.ErrorManager;
import com.systemlibrary.service.email.Email;
import com.systemlibrary.system.LinuxService;

public class Mail
{
	private ErrorManager errorManager = null;
	private Email email;
	
	public Mail()
	{
		this.errorManager = new ErrorManager();
		this.email = new Email();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean addEmailAccount(String email, String password)
	{
		boolean status = this.email.addEmailAccount(email, password);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.email.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//RELOAD POSTFIX
		LinuxService.reloadPostFix();
		
		return true;
	}
	
	public boolean deleteEmailAccount(String email)
	{
		boolean status = this.email.deleteEmailAccount(email);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.email.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//RELOAD POSTFIX
		LinuxService.reloadPostFix();
		
		return true;		
	}
	
	
}
