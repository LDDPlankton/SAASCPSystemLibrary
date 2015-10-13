package com.systemlibrary.service.dns;

import com.ld.libcmdexecutor.CommandExecutor;

public class DNSControl
{
	private DNS dns;
	private String message;
	
	public DNSControl()
	{
		this.dns = new DNS();
		this.message = "";
	}
	
	public String getMessage()
	{
		return this.message;
	}
		
	public boolean addDNS(String domain, String email, String ns1, String ns2, String primary_ip)
	{
		//ADD DNS
		boolean status = this.dns.addDNS(domain, email, ns1, ns2, primary_ip);
		
		//CHECK DNS STATUS
		if(!status)
		{
			this.message = "This domain already has a zone file!";
			return false;
		}
		
		//REBUILD NAMED CONF
		status = dns.rebuildnamedconf();
		if(!status)
		{
			this.message = this.dns.getErrorInformation().getErrorMessage();
			this.dns.removeDNS(domain);
			return false;
		}
		
		//RESTART NAMED
		CommandExecutor cmd = new CommandExecutor();
		status = cmd.runCommand("systemctl restart named", 0, false);
		if(!status)
		{
			this.message = cmd.getErrorMessage();
			return false;
		}
		
		this.message = "The DNS Zone has been added!";
		return true;
	}
	
	public boolean delDNS(String domain)
	{
		boolean status = this.dns.removeDNS(domain);
		if(!status)
		{
			this.message = this.dns.getErrorInformation().getErrorMessage();
			return false;
		}
		
		//REBUILD NAMED CONF
		status = dns.rebuildnamedconf();
		if(!status)
		{
			this.message = this.dns.getErrorInformation().getErrorMessage();
			this.dns.removeDNS(domain);
			return false;
		}
		
		//RESTART NAMED
		CommandExecutor cmd = new CommandExecutor();
		status = cmd.runCommand("systemctl restart named", 0, false);
		if(!status)
		{
			this.message = cmd.getErrorMessage();
			return false;
		}
		
		this.message = "The DNS Zone has been removed!";
		return true;
	}
}
