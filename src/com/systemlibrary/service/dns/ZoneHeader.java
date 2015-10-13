package com.systemlibrary.service.dns;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZoneHeader
{
	private int date_serial;
	private int date_revision;
	private int ttl;
	private String domain;
	private String nameserver1;
	private String email;
	
	public ZoneHeader()
	{
		this.date_serial = -1;
		this.date_revision = 10;
	}
	
	public void create(String domain, String nameserver1, String email, int ttl)
	{
		this.domain = domain;
		this.nameserver1 = nameserver1;
		this.email = email;
		this.ttl = ttl;
		this.date_revision = 10;
		
		//GENERATE DATE SERIAL + EMAIL
		this.generateDateSerial();
		this.generateEmail();
	}
	
	public void load(String zone)
	{
		//FIND TTL
		String ttl_line = zone.split("\n")[0];			//$TTL X
		int zone_ttl = Integer.valueOf( ttl_line.split(" ")[1] );
		
		//FIND DOMAIN, TTL, NS, EMAIL
		String header_info1 = zone.split("\n")[1];											//me.com.	86400	IN	SOA	ns1.me.com. joe.me.com. (
		String header_info2 = header_info1.split("\t")[4].replace('(', ' ').trim();			//ns1.me.com. joe.me.com.
		String header_tmp_ns = header_info2.split(" ")[0];									//ns1.me.com.
		String header_tmp_email = header_info2.split(" ")[1];								//joe.me.com.
		String zone_header_domain = header_info1.split("\t")[0].replace('.', ' ').trim();	//DOMAIN
		int zone_header_ttl = Integer.valueOf( header_info1.split("\t")[1] );				//TTL
		String zone_header_ns = header_tmp_ns.substring(0, header_tmp_ns.lastIndexOf("."));	//NS
		String zone_header_email = header_tmp_email.substring(0, header_tmp_email.lastIndexOf("."));	//EMAIL
		
		//FIND SERIAL, REFRESH, ETC
		String refresh_info = zone.substring(zone.indexOf("(")+1, zone.indexOf(")")).trim();
		String ttl_info1 = refresh_info.split("\n")[0].trim();										//2014112710	; serial, todays date
		String ttl_info2 = refresh_info.split("\n")[1].trim();										//86400		; refresh, seconds
		String ttl_info3 = refresh_info.split("\n")[2].trim();										//7200		; retry, seconds
		String ttl_info4 = refresh_info.split("\n")[3].trim();										//3600000		; expire, seconds
		int ttl_serial = Integer.valueOf( ttl_info1.split(";")[0].trim() );
		int ttl_refresh = Integer.valueOf( ttl_info2.split(";")[0].trim() );
		int ttl_retry = Integer.valueOf( ttl_info3.split(";")[0].trim() );
		int ttl_expire = Integer.valueOf( ttl_info4.split(";")[0].trim() );
		
		//CONVERT TTL_SERIAL TO STRING
		String serial_str = String.valueOf(ttl_serial);
		
		//SET FIELD VALUES
		this.ttl = zone_header_ttl;
		this.domain = zone_header_domain;
		this.nameserver1 = zone_header_ns;
		this.email = zone_header_email;
		this.date_serial = Integer.valueOf( serial_str.substring(0, serial_str.length()-2) );
		this.date_revision = Integer.valueOf( serial_str.substring(serial_str.length()-2) );
	}
	
	public void generateEmail()
	{
		this.email = this.email.replace("@", ".").concat(".");;
	}
	
	public void generateDateSerial()
	{
		Date tDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
		String date_string = sdf.format(tDate);
		
		//IF CURRENT DATE == GENERATED DATE [MEANS INCREMENT REVISION]
		if(date_string.equals( String.valueOf(this.date_serial)) )
			this.date_revision += 1;
		else
			this.date_revision = 10;
		
		this.date_serial = Integer.valueOf(date_string + this.date_revision);	//00 FOR REVISION
	}
	
	public String generateHeader()
	{
		String ret = "";
		ret = "$TTL " + this.ttl + "\n";
		ret += this.domain + ".\t86400\tIN\tSOA\t"+ this.nameserver1 + ". " + this.email + " (\n"
				+ "\t" + this.date_serial + "\t; serial, todays date\n"
				+ "\t86400\t\t; refresh, seconds\n"
				+ "\t7200\t\t; retry, seconds\n"
				+ "\t3600000\t\t; expire, seconds\n"
				+ "\t86400\t\t; minimum, seconds\n"
				+ ")\n\r\n";
		return ret;		
	}
}