package com.systemlibrary.service.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.systemlibrary.pkg.errormanager.ErrorManager;

public class DNSZone
{
	private ErrorManager errorManager = null;
	private ZoneHeader zone_header;
	private List<ZoneRecord> zonelist;
	private Map<String, String> dns_info;
	private String zone_storage_location;
	private String error_message;
	private String domain;
	private String nameserver1;
	private String nameserver2;
	private String email;
	private String primary_ip;
	private int date_serial;
	
	public DNSZone()
	{
		this.errorManager = new ErrorManager();
		this.zone_header = new ZoneHeader();
		this.zonelist = new ArrayList<ZoneRecord>();
		this.zone_storage_location = "/var/named";						//LOCATION TO STORE + READ ZONE FILES FROM
		this.domain = "";
		this.nameserver1 = "";
		this.nameserver2 = "";
		this.email = "";
		this.primary_ip = "";
		this.date_serial = 0;
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	public boolean saveRecord()
	{
		String zone_content = this.generateZone();
		String filename = this.zone_storage_location + File.separator + this.domain + ".db";
		
		PrintWriter pr = null;
		try
		{
			pr = new PrintWriter(filename);
			pr.write( zone_content );
			pr.close();
			
		}
		catch (FileNotFoundException e)
		{
			this.errorManager.setErrorMessage( String.format("Error: DNSZone::saveRecord() [%s]", e.getMessage()) );
			return false;
		}
		return true;
	}

	public boolean removeRecord(String domain)
	{
		String filename = this.zone_storage_location + File.separator + domain + ".db";
		File myFile = new File(filename);
		if(myFile.exists() && myFile.isFile())
		{
			myFile.delete();
			return true;
		}
		else
		{
			this.errorManager.setErrorMessage( "Error: DNSZone::removeRecord() [File not exist!]" );
			return false;
		}
	}
	
	public boolean addDNS(String domain, String email, String ns1, String ns2, String primary_ip)
	{
		if(this.isZoneExist(domain))
		{
			this.errorManager.setErrorMessage( "Error: This DNS Zone Exists!" );
			return false;
		}
		
		//SET ZONE INFO
		this.domain = domain;
		this.email = email;
		this.nameserver1 = ns1;
		this.nameserver2 = ns2;
		this.primary_ip = primary_ip;
		
		//NS RECORDS
		ZoneRecord r1 = new ZoneRecord(domain+".", 86400, -1, "NS", this.nameserver1+".");
		ZoneRecord r2 = new ZoneRecord(domain+".", 86400, -1, "NS", this.nameserver2+".");
		
		//MX RECORDS
		ZoneRecord r3 = new ZoneRecord(domain+".", 14400, 0, "MX", domain+".");
		
		//CNAME RECORDS
		ZoneRecord r4 = new ZoneRecord("mail", 14400, -1, "CNAME", domain+".");
		ZoneRecord r5 = new ZoneRecord("www", 14400, -1, "CNAME", domain+".");
		
		//A RECORDS
		ZoneRecord r6 = new ZoneRecord(domain+".", 14400, -1, "A", this.primary_ip);
		ZoneRecord r7 = new ZoneRecord("localhost", 14400, -1, "A", "127.0.0.1");		
		
		//ADD RECORDS
		zonelist.add(r1);
		zonelist.add(r2);
		zonelist.add(r3);
		zonelist.add(r4);
		zonelist.add(r5);
		zonelist.add(r6);
		zonelist.add(r7);
		
		//DETERMINE IF WE ADD NS A RECORDS
		String[] parts1 = ns1.split("\\.");
		String[] parts2 = ns2.split("\\.");
		String[] parts1n = new String[parts1.length-1];
		String[] parts2n = new String[parts2.length-1];
		for(int i = 1; i < parts1.length; i++)
			parts1n[i-1] = parts1[i];
		for(int i = 1; i < parts2.length; i++)
			parts2n[i-1] = parts2[i];
		String ns1tmp = String.join(".", parts1n);
		String ns2tmp = String.join(".", parts2n);
		if(ns1tmp.contains(domain))
		{
			zonelist.add( new ZoneRecord(parts1[0], 14400, -1, "A", this.primary_ip) );
		}
		if(ns1tmp.contains(domain))
		{
			zonelist.add( new ZoneRecord(parts2[0], 14400, -1, "A", this.primary_ip) );
		}
		
		return true;
	}
	
	public boolean isZoneExist(String record)
	{
		String filename = this.zone_storage_location + File.separator + this.domain + ".db";
		File myFile = new File(filename);
		if(myFile.exists())
			return true;
		else
		{
			this.errorManager.setErrorMessage( "Error: DNSZone::isZoneExist() [The file does not exist]" );
			return false;
		}
	}

	public boolean loadRecord(String record)
	{
		String filename = this.zone_storage_location + File.separator + record + ".db";
		int line_number = 1;
		String zone = "";
		
		//LOAD DNS ZONE INTO LIST
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";
			while( (line=br.readLine()) != null)
			{
				zone += line;
			}
			br.close();
		}
		catch (IOException e)
		{
			this.errorManager.setErrorMessage( String.format("Error: DNSZone::loadRecord() [%s]", e.getMessage()) );
			return false;
		}
		
		//FIND ZONE HEADER + LOAD
		int header_serial_start = zone.indexOf("(")+1;									//FIND START OF ( + MOVE PAST IT
		int header_serial_end = zone.indexOf(")") - 1;									//FIND END OF ) AND MOVE BEFORE IT
		String zone_header = zone.substring(0, header_serial_end).trim();				//ALL OF ZONE HEADER
		this.zone_header.load(zone);
		
		//FIND ZONE RECORDS
		this.zonelist.clear();															//ENSURE ZONE LIST IS CLEAR
		String zone_records = zone.substring(header_serial_end+2).trim();				//MOVE PAST ')'
		String records[] = zone_records.split("\n");
		for(String i: records)
		{
			//SPLIT BY \t TO GET FIELDS
			String split_record[] = i.split("\t");
			
			//GET RECORD VALUES
			String domain = split_record[0].trim();
			int ttl = Integer.valueOf(split_record[1].trim());
			String record_type = split_record[3].trim();
			String record_value = "";
			int mx_level = -1;
			
			//HANDLE BASED ON RECORD_TYPE
			if(record_type.equals("MX") )
			{
				mx_level = Integer.valueOf(split_record[4].trim());
				record_value = split_record[5].trim();
			}
			else
				record_value = split_record[4].trim();

			//ADD ZONE RECORD TO OUR LIST OF ZONES
			ZoneRecord zr = new ZoneRecord(domain, ttl, mx_level, record_type, record_value);
			this.zonelist.add(zr);
		}
		return true;
	}
					
	public String generateZone()
	{
		this.zone_header.create(this.domain, this.nameserver1, this.email, 14400);
		String zone = this.zone_header.generateHeader();
		
		for(ZoneRecord i : this.zonelist)
		{
			//BUILD BASIC ZONE LINE
			zone += i.getHostRecord() + "\t" + i.getTTL() + "\tIN\t" + i.getRecordType() + "\t";
			
			//DO NOT INCLUDE MX LEVEL FOR ANYHTING OTHER THAN MX RECORDS
			if(i.getRecordType().equals("MX"))
				zone += i.getMXLevel() + "\t";
				
			//ADD LAST PART OF DNS LINE [VALUE]
			zone += i.getRecordValue() + "\n";
		}
		return zone;
	}
	
	public boolean editDNS(String domain)
	{
		this.loadRecord(domain);
		return true;
	}
		
}