package com.systemlibrary.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test
{
	public Test()
	{
		
	}
	
	public void run()
	{
		String read_line;

		try
		{
			InputStream is = getClass().getResourceAsStream("/APITemplates/NamedTemplate.conf");
			BufferedReader brtpl = new BufferedReader( new InputStreamReader(is) );
			
			while( (read_line = brtpl.readLine()) != null)
			{
				System.out.println(read_line);
			}
			
			brtpl.close();
			is.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}
}
