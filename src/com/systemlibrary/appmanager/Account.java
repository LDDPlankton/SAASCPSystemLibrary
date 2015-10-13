package com.systemlibrary.appmanager;

import com.systemlibrary.pkg.errormanager.ErrorManager;
import com.systemlibrary.service.dns.DNS;
import com.systemlibrary.service.http.HTTP;
import com.systemlibrary.system.LinuxService;
import com.systemlibrary.system.LinuxUser;
import com.systemlibrary.system.UnixSystem;

public class Account 
{
	private ErrorManager errorManager = null;
	private HTTP http;
	private LinuxUser linuxUser;
	private DNS dns;
	
	public Account()
	{
		this.errorManager = new ErrorManager();
		this.http = new HTTP();
		this.linuxUser = new LinuxUser();
		this.dns = new DNS();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean addAccount(String user, String passwd, String email, String domain, String ns1, String ns2, String ip)
	{
		boolean status;
		
		//ADD LINUX SYSTEM ACCOUNT
		status = this.linuxUser.addSystemAccount(user, passwd, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.linuxUser.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//ADD VHOST
		status = this.http.addVhost(user, domain, ip, 80);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.http.getErrorInformation().getErrorMessage());
			return false;
		}

		//ADD DNS
		status = this.dns.addDNS(domain, email, ns1, ns2, ip);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.dns.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//REBUILD DNS
		status = this.dns.rebuildnamedconf();
		if(!status)
		{
			this.errorManager.setErrorMessage(this.dns.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//RESTART SERVICES
		LinuxService.reloadHttpd();
		LinuxService.reloadNamed();
		LinuxService.reloadPostFix();
		
		return true;
	}
	
	public boolean deleteAccount(String user, String domain)
	{
		boolean status;
		
		//DELETE LINUX SYSTEM ACCOUNT
		status = this.linuxUser.deleteSystemAccount(user, domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.linuxUser.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//DELETE VHOST
		status = this.http.deleteVhost(domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.http.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//DELETE DNS
		status = this.dns.removeDNS(domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.dns.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//REBUILD DNS
		status = this.dns.rebuildnamedconf();
		if(!status)
		{
			this.errorManager.setErrorMessage(this.dns.getErrorInformation().getErrorMessage());
			return false;
		}
		
		//DELETE HOMEDIR
		UnixSystem.removeDirectory(String.format("/home/%s", domain));
		
		//RESTART SERVICES
		LinuxService.reloadHttpd();
		LinuxService.reloadNamed();
		LinuxService.reloadPostFix();
		
		return true;
	}
}
