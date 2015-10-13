package com.systemlibrary.pkg.errormanager;

public interface InterfaceError
{
	public void reset();
    public boolean isError();
    public void setErrorMessage(String msg);
    public String getErrorMessage();
}
