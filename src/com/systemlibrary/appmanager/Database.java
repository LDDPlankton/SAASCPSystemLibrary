package com.systemlibrary.appmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.systemlibrary.pkg.errormanager.ErrorManager;
import com.systemlibrary.service.mysql.MySQL;

public class Database
{
	private ErrorManager errorManager = null;
	private MySQL mysql = null;
	
	public Database()
	{
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean establishMySQLConnection()
	{
		String rootPwd = this.getMySQLRootPassword();
		if(rootPwd == null)
		{
			this.errorManager.setErrorMessage("Error reading mysql root password from /root/.my.cnf");
			return false;
		}
		this.mysql = new MySQL(rootPwd);
		return true;
	}
	
	public String getMySQLRootPassword()
	{
		String pwd = "";
		
		try
		{
			//READ MY.CNF FOR KEY/VAL PAIRS
			BufferedReader in = new BufferedReader( new FileReader("/root/.my.cnf") );
			String line;
			while( (line=in.readLine()) != null)
			{
				String[] parts = line.split("=");
				if(parts.length != 2)
					continue;
				String key = line.split("=")[0];
				String val = line.split("=")[1].replaceAll("\"", "");
				
				//FOUND KEY NEEDED
				if(key.equals("pass"))
				{
					pwd = val;
					return pwd;
				}
			}
			in.close();
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public boolean addMySQLUser(String user, String host, String password)
	{
		boolean status = mysql.addMySQLUser(user, host, password);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Adding MySQL User: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}
	
	public boolean deleteMySQLUser(String user, String host)
	{
		boolean status = mysql.deleteMySQLUser(user, host);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Deleting MySQL User: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}
	
	public boolean addMySQLDatabase(String database)
	{
		boolean status = mysql.addMySQLDatabase(database);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Adding MySQL DB: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}
	
	public boolean deleteMySQLDatabase(String database)
	{
		boolean status = mysql.deleteMySQLDatabase(database);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Deleting MySQL DB: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}
	
	public boolean addMySQLUserToDB(String user, String host, String database)
	{
		boolean status = mysql.addMySQLUserToDB(user, host, database);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Adding MySQL User To DB: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}

	public boolean deleteMySQLUserToDB(String user, String host, String database)
	{
		boolean status = mysql.deleteMySQLUserFromDB(user, host, database);
		if(!status)
		{
			this.errorManager.setErrorMessage("Error Deleting MySQL User From DB: " + mysql.getErrorInformation());
			return false;
		}
		return true;
	}

	
}
