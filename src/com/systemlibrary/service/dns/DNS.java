package com.systemlibrary.service.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.systemlibrary.pkg.errormanager.ErrorManager;
import com.systemlibrary.system.JarFileUtil;

public class DNS
{
	private ErrorManager errorManager = null;
	private DNSZone zone;
	
	public DNS()
	{
		this.errorManager = new ErrorManager();
		this.zone = new DNSZone();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}


	public boolean addDNS(String domain, String email, String ns1, String ns2, String primary_ip)
	{
		//TRY TO ADD DNS RECORD TO INTERNAL DATA STRUCTURE
		boolean status = this.zone.addDNS(domain, email, ns1, ns2, primary_ip);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.zone.getErrorInformation().getErrorMessage());
			return false;
		}
		
		status = this.zone.saveRecord();		
		return status;
	}

	public boolean removeDNS(String domain)
	{
		boolean status = this.zone.removeRecord(domain);
		if(!status)
		{
			this.errorManager.setErrorMessage(this.zone.getErrorInformation().getErrorMessage());
			return false;			
		}
		return status;
	}
	
	public DNSZone fetchDNSInfo(String domain)
	{
		DNSZone dns = new DNSZone();
		boolean status = dns.loadRecord(domain);
		if(!status)
		{
			this.errorManager.setErrorMessage( this.zone.getErrorInformation().getErrorMessage() );
			return null;
		}
		return dns;
	}
	
	public boolean editDNS(String domain)
	{
		//LOAD RECORD
		boolean status = this.zone.loadRecord(domain);
		if(!status)
		{
			this.errorManager.setErrorMessage( this.zone.getErrorInformation().getErrorMessage() );
			return false;
		}
		return true;
	}
	
	public boolean rebuildnamedconf()
	{
		File dir = new File("/var/named");
		String named_template = "";
		String zone_list = "";
		
		//STEP 1. LOAD NAMED TEMPLATE FILE
		try
		{
			JarFileUtil util = new JarFileUtil();
			InputStream is = util.loadResource("NamedTemplate.conf");
			if(is == null)
			{
				this.errorManager.setErrorMessage("Error: Unable to establish stream to NamedTemplate.conf");
				return false;				
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is) );
			String line = "";
	
			while( (line=br.readLine()) != null)
			{
				named_template += line + "\n";
			}
			is.close();
		}
		catch (IOException | NullPointerException e)
		{
			this.errorManager.setErrorMessage( String.format("Error: Could not open NamedTemplate.conf [%s]", e.getMessage()) );
		}
	
		//STEP 2. FIND ZONES TO ADD TO NAMED CONFIG
		if(dir.isDirectory())
		{
			File[] file_list = dir.listFiles();
			for(File f : file_list)
			{
				if(!f.isFile())
					continue;
				
				String name = f.getName().replace(".db", "");
				String parts[] = f.getName().split("\\.");
				if(parts.length > 1 && parts[parts.length-1].equals("db") )
				{
					String zone_entry = "zone \"" + name + "\" {\n"
							+ "type master;\n"
							+ "file \"/var/named/" + name + ".db\";\n"
							+ "};\n";
					zone_list += zone_entry;
				}
			}
		}
		
		//STEP 3. REPLACE + ADD ZONES
		named_template = named_template.replaceAll("REPLACE_ZONE_LIST_HERE", zone_list);
		
		//STEP 4. WRITE TO FILE
		try
		{
			FileWriter fileWrite = new FileWriter("/etc/named.conf");
			fileWrite.write(named_template);
			fileWrite.close();
		}
		catch (IOException e)
		{
			this.errorManager.setErrorMessage( "Error: DNSZone::rebuildnamedconf() We were unable to write /etc/named.conf" );
			return false;
		}

	
		return true;
	}
	
}
