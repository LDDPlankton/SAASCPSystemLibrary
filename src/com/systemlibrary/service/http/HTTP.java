package com.systemlibrary.service.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.systemlibrary.pkg.errormanager.ErrorManager;
import com.systemlibrary.system.JarFileUtil;

public class HTTP
{
	private ErrorManager errorManager = null;
	
	public HTTP()
	{
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public String getVhostTemplate()
	{
		String vhost_template = "";
		
		//STEP 1. LOAD HTTP TEMPLATE FILE
		try
		{
			JarFileUtil util = new JarFileUtil();
			InputStream is = util.loadResource("ApacheVhostTemplate.conf");
			if(is == null)
			{
				this.errorManager.setErrorMessage("Error: Unable to establish stream to ApacheVhostTemplate.conf");
				return null;				
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is) );
			String line = "";
	
			while( (line=br.readLine()) != null)
			{
				vhost_template += line + "\n";
			}
			is.close();
			return vhost_template.trim();
		}
		catch (IOException | NullPointerException e)
		{
			this.errorManager.setErrorMessage( String.format("Error: Could not open ApacheVhostTemplate.conf [%s]", e.getMessage()) );
			return null;
		}
	}
	
	public String generateVhost(String user, String domain, String ip, int port)
	{
		String template = this.getVhostTemplate();
		if(template == null)
			return null;
		template = template.replace("[IP]", ip);
		template = template.replace("[PORT]", String.valueOf(port));
		template = template.replace("[USER]", user);
		template = template.replace("[DOMAIN]", domain);
		return template;
	}

	public boolean isVhostExist(String domain)
	{
		if(new File(String.format("/etc/httpd/conf.d/%s.conf", domain)).isFile())
			return true;
		else
			return false;
	}
					
	public boolean addVhost(String user, String domain, String ip, int port)
	{
		if(this.isVhostExist(domain))
		{
			this.errorManager.setErrorMessage("This vhost already exists!");
			return false;
		}
		
		String vhost = this.generateVhost(user, domain, ip, port);
		if(vhost == null)
			return false;
		String vhost_file = String.format("/etc/httpd/conf.d/%s.conf", domain);
		File fd1 = new File(vhost_file);
		try
		{
			FileWriter fw = new FileWriter(fd1);
			fw.write(vhost);
			fw.close();
			return true;
		}
		catch (IOException e)
		{
			this.errorManager.setErrorMessage( String.format("Unable to write to %s: ", vhost_file, e.getMessage()) );
			return false;
		}		
	}
	
	public boolean deleteVhost(String domain)
	{
		if(!this.isVhostExist(domain))
		{
			this.errorManager.setErrorMessage("This vhost does not exist!");
			return false;
		}
		String vhost_file = String.format("/etc/httpd/conf.d/%s.conf", domain);
		boolean status = new File(vhost_file).delete();
		if(!status)
		{
			this.errorManager.setErrorMessage("Unable to delete vhost!");
			return false;			
		}
		return true;
	}
	

	
	
}
