package com.systemlibrary.service.mysql;

import com.ld.sqlconnector.DatabaseFactory;
import com.ld.sqlconnector.DatabaseInterface;
import com.systemlibrary.pkg.errormanager.ErrorManager;

public class MySQL
{
	private ErrorManager errorManager = null;
	private DatabaseInterface dbi = null;
	private boolean isConnected;
	private String rootPwd;
	
	public MySQL(String rootPwd)
	{
		this.errorManager = new ErrorManager();
		this.isConnected = false;
		this.rootPwd = rootPwd;
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	/*
	 * This function will attempt to connect to the MySQL Server.
	 * 
	 * @return	Boolean
	 */
	public boolean connect()
	{
		this.dbi = DatabaseFactory.getDatabaseConnection("MySQL");
		try
		{
			dbi.setAuthentication("root", this.rootPwd, "");
			this.isConnected = true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to Connect to MySQL Server: " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	public boolean addMySQLDatabase(String database)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}
		
		//ADD DATABASE
		this.dbi.setSQL(String.format("CREATE DATABASE %s;", database));
		
		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to add Database:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		return true;
	}
	
	public boolean deleteMySQLDatabase(String database)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}
		
		//DELETE DATABASE
		this.dbi.setSQL(String.format("DROP DATABASE %s;", database));
		
		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to delete Database:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		return true;
	}
	
	public boolean addMySQLUser(String username, String host, String password)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}

		//CREATE USER
		this.dbi.setSQL("CREATE USER ?@? IDENTIFIED BY ?;");
		this.dbi.addPreparedArgument( username );
		this.dbi.addPreparedArgument( host );
		this.dbi.addPreparedArgument( password );

		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to add MySQL User:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		
		return true;
	}
	
	public boolean deleteMySQLUser(String username, String host)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}
		
		//DELETE MYSQL USER
		//this.dbi.setSQL("DELETE FROM `mysql.user` WHERE `User`=? LIMIT 1;");
		this.dbi.setSQL("DROP USER ?@?;");
		this.dbi.addPreparedArgument( username );
		this.dbi.addPreparedArgument( host );
		
		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to delete MySQL User:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		
		return true;
	}
	
	public boolean addMySQLUserToDB(String username, String host, String database)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}
		
		//GRANT USER PERMISSION
		this.dbi.setSQL(String.format("GRANT CREATE, DROP, ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE, EXECUTE, CREATE TEMPORARY TABLES, TRIGGER, CREATE VIEW, SHOW VIEW, LOCK TABLES ON %s.* TO ?@?;", database));
		this.dbi.addPreparedArgument( username );
		this.dbi.addPreparedArgument( host );
		
		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to add Grant Permissions:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		
		return true;
	}
	
	public boolean deleteMySQLUserFromDB(String username, String host, String database)
	{
		//IF NOT CONNECTED, ESTABLISH CONNECTION.
		if(!this.isConnected)
		{
			boolean status = this.connect();
			if(!status)
				return false;
		}
		
		//REVOKE USER PERMISSION
		this.dbi.setSQL(String.format("REVOKE CREATE, DROP, ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE, EXECUTE, CREATE TEMPORARY TABLES, TRIGGER, CREATE VIEW, SHOW VIEW, LOCK TABLES ON %s.* FROM ?@?;", database));
		this.dbi.addPreparedArgument( username );
		this.dbi.addPreparedArgument( host );
		
		//IF QUERY FAILED
		if(!dbi.executeQuery())
		{
			this.errorManager.setErrorMessage("Unable to delete Grant Permissions:" + this.dbi.getQueryErrorMessage() );
			System.out.println("E=" + dbi.getQueryErrorMessage() );
			return false;
		}
		
		return true;
	}
	
}
