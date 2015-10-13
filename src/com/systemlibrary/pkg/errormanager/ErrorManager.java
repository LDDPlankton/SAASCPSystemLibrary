package com.systemlibrary.pkg.errormanager;

public class ErrorManager implements InterfaceError
{
	private boolean isError;
	private String errorMessage;
	
	public ErrorManager()
	{
		this.isError = false;
		this.errorMessage = "";
	}

	public void reset()
	{
		this.isError = false;
		this.errorMessage = "";
	}
	
	public boolean isError()
	{
		return this.isError;
	}

	public String getErrorMessage()
	{
		return this.errorMessage;
	}

	public void setErrorMessage(String msg)
	{
		this.errorMessage = msg;
		this.isError = true;
	}
}
