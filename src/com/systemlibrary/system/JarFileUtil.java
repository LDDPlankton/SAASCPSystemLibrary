package com.systemlibrary.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileUtil
{
	public JarFileUtil()
	{
		
	}
	
	public InputStream loadResource(String resource)
	{
		File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		InputStream is = null;
		
		if(jarFile.isFile())
		{
			try
			{
				JarFile jar = new JarFile(jarFile);
				JarEntry entry = jar.getJarEntry(resource);
				/*
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements())
				{
					JarEntry entryx = entries.nextElement();
					if(entryx.getName().contains(resource))
						System.out.println("R="+entryx.getName());
				}
				*/				
				if(entry==null)
				{
					jar.close();
					return null;
				}
				
				is = jar.getInputStream(entry);		
				return is;
			}
			catch (IOException e)
			{
				return null;
			}
		}
		else
		{
			is = getClass().getResourceAsStream("/APITemplates" + File.separator + resource);
			return is;
		}
	}
}
