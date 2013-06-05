package org.rpi.config;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;

public class Config {
	
	
	public static String mediapalyer_name = "Default Room";
	public static String debug = "None";

	public static String version = "0.0.0.1";
	public static String logfile = "mediamonitor.log";
	public static String loglevel;
	public static String logconsole;
	
	
	public static Level getLogFileLevel()
	{
		return getLogLevel(loglevel);
	}
	
	public static Level getLogConsoleLevel()
	{
		return getLogLevel(logconsole);
	}
	
	private static Level getLogLevel(String s)
	{
		if(s==null)
			return Level.DEBUG;
		
		if(s.equalsIgnoreCase("DEBUG"))
		{
			return Level.DEBUG;
		}
		
		else if (s.equalsIgnoreCase("ALL"))
		{
			return Level.ALL;
		}
		else if (s.equalsIgnoreCase("ERROR"))
		{
			return Level.ERROR;
		}
		
		else if (s.equalsIgnoreCase("FATAL"))
		{
			return Level.FATAL;
		}
		
		else if (s.equalsIgnoreCase("INFO"))
		{
			return Level.INFO;
		}
		
		else if (s.equalsIgnoreCase("OFF"))
		{
			return Level.OFF;
		}
		
		else if (s.equalsIgnoreCase("TRACE"))
		{
			return Level.TRACE;
		}
		
		else if (s.equalsIgnoreCase("WARN"))
		{
			return Level.WARN;
		}
		return Level.DEBUG;
	}


	
	public static int converStringToInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			
		}
		return -99;
	}
}
