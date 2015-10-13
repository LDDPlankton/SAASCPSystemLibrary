package com.systemlibrary.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class UnixSystem 
{
	public UnixSystem()
	{
		
	}
	
	/*
	 * This function will attempt to determine if a particular Unix/Linux User Exists.
	 * 
	 * @param	User		The user to check if exists in the passwd file.
	 * @return	Boolean
	 */
	public static boolean isUnixUserExist(String user)
	{
		try
		{
			BufferedReader br = new BufferedReader( new FileReader("/etc/passwd") );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				String[] lineParts = line.split(":");
				if(lineParts.length < 6)
					continue;
				
				//GET USER INFO
				String luser = lineParts[0];
				String luid = lineParts[2];
				String lgid = lineParts[3];
				String lhome = lineParts[5];
				
				//CHECK IF USER == A LOCAL USER
				if(user.equals(luser))
					return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/*
	 * This function will return the username from a Particular Domain.
	 * 
	 * @param	domain		The domain to search for the username against in /etc/passwd.
	 * @return	Boolean
	 */
	public static String getUserFromDomain(String domain)
	{
		try
		{
			BufferedReader br = new BufferedReader( new FileReader("/etc/passwd") );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				String[] lineParts = line.split(":");
				if(lineParts.length < 6)
					continue;
				
				//GET USER INFO
				String luser = lineParts[0];
				String luid = lineParts[2];
				String lgid = lineParts[3];
				String lhome = lineParts[5];
				
				//SPLIT
				String[] homeParts = lhome.substring(1, lhome.length()).split("/");	//SPLITING /home/domain2 for example AFTER removing first '/'
				
				//IF HOME PATH HAS 2 PARTS, FIRST BEING HOME AND SECOND BEING THE DOMAIN
				if(homeParts.length == 2 && homeParts[0].equals("home") && homeParts[1].equals(domain))
					return luser;
			}
			return null;
		}
		catch (Exception e)
		{
			return null;
		}		
	}
	
	/*
	 * This function will return the username if it exists, or will return null.
	 * 
	 * @param	domain		The domain to search for the username against in /etc/passwd.
	 * @return	Boolean
	 */
	public static String getUserFromPasswdFile(String user)
	{
		try
		{
			BufferedReader br = new BufferedReader( new FileReader("/etc/passwd") );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				String[] lineParts = line.split(":");
				if(lineParts.length < 6)
					continue;
				
				//GET USER INFO
				String luser = lineParts[0];
				String luid = lineParts[2];
				String lgid = lineParts[3];
				String lhome = lineParts[5];
				
				//SPLIT
				String[] homeParts = lhome.substring(1, lhome.length()).split("/");	//SPLITING /home/domain2 for example AFTER removing first '/'
				
				//IF HOME PATH HAS 2 PARTS, FIRST BEING HOME AND SECOND BEING THE DOMAIN
				if(luser.equals(user))
					return luser;
			}
			return null;
		}
		catch (Exception e)
		{
			return null;
		}		
	}
	
	/*
	 * This function will return the UID/GID Information for a particular User.
	 * 
	 * @param	user	The user to return the UID/GID Combination.
	 * @return	int[]
	 */
	public static int[] getUnixUIDGIDPair(String user)
	{
		int[] ret = new int[] {-1,-1};
		try
		{
			BufferedReader br = new BufferedReader( new FileReader("/etc/passwd") );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				String[] lineParts = line.split(":");
				if(lineParts.length < 6)
					continue;
				
				//GET USER INFO
				String luser = lineParts[0];
				String luid = lineParts[2];
				String lgid = lineParts[3];
				String lhome = lineParts[5];
				
				//CHECK IF USER == A LOCAL USER
				if(user.equals(luser))
				{
					ret[0] = Integer.valueOf(luid);
					ret[1] = Integer.valueOf(lgid);
					return ret;
				}
			}
			return ret;
		}
		catch (Exception e)
		{
			return null;
		}	
	}
	
	/*
	 * This function will delete a System Directory.
	 * 
	 * @param	dir		The directory to delete.
	 */
	public static void removeDirectory(String dir)
	{
		File myDir = new File(dir);
		File[] fileList = myDir.listFiles();
		for(File f : fileList)
		{
			if(f.getName().equals(".") || f.getName().equals(".."))
				continue;
			if(f.isDirectory())
			{
				removeDirectory(f.getAbsolutePath());
				f.delete();								//DELETE AFTER SUBFOLDERS REMOVED!
			}
			else
			{
				f.delete();
			}
		}
		myDir.delete();
	}

}
