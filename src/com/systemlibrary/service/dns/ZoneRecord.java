package com.systemlibrary.service.dns;

public class ZoneRecord
{
	private String host_record;
	private int ttl;
	private int mx_level;
	private String record_type;
	private String record_value;
	
	public ZoneRecord(String host_record, int ttl, int mx_level, String record_type, String record_value)
	{
		this.host_record = host_record;
		this.ttl = ttl;
		this.mx_level = mx_level;
		this.record_type = record_type;
		this.record_value = record_value;
	}
	
	public String getHostRecord()
	{
		return this.host_record;
	}
	
	public int getTTL()
	{
		return this.ttl;
	}
	
	public int getMXLevel()
	{
		return this.mx_level;
	}
	
	public String getRecordType()
	{
		return this.record_type;
	}
	
	public String getRecordValue()
	{
		return this.record_value;
	}
	
}
