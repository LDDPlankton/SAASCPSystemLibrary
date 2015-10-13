package com.systemlibrary.service.email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.ld.sharedlibrary.lib.ErrorManager;
import com.systemlibrary.system.LinuxService;
import com.systemlibrary.system.UnixSystem;

public class SystemEmail
{
	private ErrorManager errorManager = null;
	
	public SystemEmail()
	{
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	/*
	 * This function will parse our vmail_domains_file into a List<>
	 * 
	 * @return	List<String>
	 */
	public List<String> getPostFixEmailDomains()
	{
		List<String> domainList = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(EmailUtil.vmail_domains_file) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				String parts[] = line.split("\t");
				domainList.add(parts[0].trim());
			}
			return domainList;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/*
	 * This function will parse our vmail_mailbox_file into a List<>.
	 * 
	 * @return	List<MailBoxFileDSO>
	 */
	public List<MailBoxFileDSO> getPostFixEmailMailbox()
	{
		List<MailBoxFileDSO> dsoList = new ArrayList<MailBoxFileDSO>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(EmailUtil.vmail_mailbox_file) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				//SPLIT
				String parts[] = line.split("\t");			//0=info@mydomain.com | 1=mydomain.com/info/
				String emailParts[] = parts[0].split("@");	//0=info | 1=mydomain.com
				//SET NEW OBJECT
				MailBoxFileDSO dso = new MailBoxFileDSO();
				dso.emailAddress = parts[0].trim();
				dso.emailPrefix = emailParts[0];
				dso.emailDomain = emailParts[1];
				dsoList.add(dso);
			}
			return dsoList;
		}
		catch (Exception e)
		{
			return null;
		}		
	}
	
	/*
	 * This function will parse our vmail_mailbox_file into a List<>.
	 * 
	 * @return	List<MailBoxAliaseDSO>
	 */
	public List<MailBoxAliasDSO> getPostFixEmailAliases()
	{
		List<MailBoxAliasDSO> dsoList = new ArrayList<MailBoxAliasDSO>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(EmailUtil.vmail_aliases_file) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				//SPLIT
				String parts[] = line.split("\t");
				//SET NEW OBJECT
				MailBoxAliasDSO dso = new MailBoxAliasDSO();
				dso.emailSrc = parts[0].trim();
				dso.emailDest = parts[1].trim();
				dsoList.add(dso);
			}
			return dsoList;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/*
	 * This function will parse our vmail_mailbox_file into a List<>.
	 * 
	 * @return	List<MailBoxVirtualDSO>
	 */
	public List<MailBoxVirtualDSO> getPostFixEmailVirtual()
	{
		List<MailBoxVirtualDSO> dsoList = new ArrayList<MailBoxVirtualDSO>();
		try
		{
			BufferedReader br = new BufferedReader( new FileReader(EmailUtil.vmail_virtual_uid) );
			String line = "";
			while( (line=br.readLine()) != null)
			{
				//SPLIT
				String parts[] = line.split("\t");
				//SET NEW OBJECT
				MailBoxVirtualDSO dso = new MailBoxVirtualDSO();
				dso.virtualEmail = parts[0].trim();
				dso.virtualUid = Integer.valueOf(parts[1].trim());
				dsoList.add(dso);
			}
			return dsoList;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/*
	 * This function will update our PostFix vmail_domains_file.
	 * 
	 * @param	domainList		The List of data to write to our postfix file.
	 * @return	Boolean
	 */
	public boolean updatePostFixEmailDomains(List<String> domainList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(EmailUtil.vmail_domains_file) );
			for(String i : domainList)
			{
				String tmp = String.format("%s\t#domain\n", i);
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update vmail_domains_file: " + e.getMessage());
			return false;
		}		
	}
	
	/*
	 * This function will update our PostFix vmail_mailbox_file.
	 * 
	 * @param	dsoList		The List of data to write to our postfix file.
	 * @return	Boolean
	 */
	public boolean updatePostFixEmailMailBox(List<MailBoxFileDSO> dsoList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(EmailUtil.vmail_mailbox_file) );
			for(MailBoxFileDSO i : dsoList)
			{
				String tmp = String.format("%s\n", i.generateContentToWriteToFile() );
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update vmail_mailbox_file: " + e.getMessage());
			return false;
		}		
	}
	
	/*
	 * This function will update our PostFix vmail_aliases_file.
	 * 
	 * @param	dsoList		The List of data to write to our postfix file.
	 * @return	Boolean
	 */
	public boolean updatePostFixEmailAliase(List<MailBoxAliasDSO> dsoList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(EmailUtil.vmail_aliases_file) );
			for(MailBoxAliasDSO i : dsoList)
			{
				String tmp = String.format("%s\n", i.generateContentToWriteToFile() );
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update vmail_aliases_file: " + e.getMessage());
			return false;
		}		
	}
	
	/*
	 * This function will update our PostFix vmail_virtual_uid file.
	 * 
	 * @param	dsoList		The List of data to write to our postfix file.
	 * @return	Boolean
	 */
	public boolean updatePostFixEmailVirtual(List<MailBoxVirtualDSO> dsoList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter(EmailUtil.vmail_virtual_uid) );
			for(MailBoxVirtualDSO i : dsoList)
			{
				String tmp = String.format("%s\n", i.generateContentToWriteToFile() );
				bw.write(tmp);
			}
			bw.close();
			return true;
		}
		catch(Exception e)
		{
			this.errorManager.setErrorMessage("Unable to update vmail_virtual_uid: " + e.getMessage());
			return false;
		}		
	}

	public boolean addEmailAccount(String email, String domain)
	{
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			this.errorManager.setErrorMessage( String.format("We could not find any user associated with %s", domain) );
			return false;
		}
		
		//GET UID/GID
		int[] uid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysUser);
		int uid = uid_gid_pair[0];
		int gid = uid_gid_pair[1];
		
		//STEP 1. GET POSTFIX EMAIL DOMAINS + CHECK IF NEED TO UPDATE ALIASES
		List<String> domainList = this.getPostFixEmailDomains();
		if(!domainList.contains(domain))
		{
			domainList.add(domain);
			status = this.updatePostFixEmailDomains(domainList);		//WRITE NEW DOMAIN LIST DATA TO FILE
			if(!status)
				return false;
		}
		
		//STEP 2. CHECK IF EMAIL IS IN OUR MAILBOX FILE
		boolean isInMailBoxFile = false;
		List<MailBoxFileDSO> mailboxFileList = this.getPostFixEmailMailbox();
		for(MailBoxFileDSO i : mailboxFileList)
		{
			if(i.emailAddress.equals(email))
			{
				isInMailBoxFile = true;
				break;
			}
		}
		if(!isInMailBoxFile)
		{
			//SPLIT
			String emailParts[] = email.split("@");	//0=info | 1=mydomain.com
			//SET NEW OBJECT
			MailBoxFileDSO dso = new MailBoxFileDSO();
			dso.emailAddress = email;
			dso.emailPrefix = emailParts[0];
			dso.emailDomain = emailParts[1];
			mailboxFileList.add(dso);
			
			//WRITE UPDATE TO FILE
			status = this.updatePostFixEmailMailBox(mailboxFileList);
			if(!status)
				return false;
		}

		//STEP 3. CHECK IF VMAIL_ALIASES FILE HAS ALIASE ... IF NOT ADD IT
		boolean isInMailAliaseFile = false;
		List<MailBoxAliasDSO> mailboxAliaseList = this.getPostFixEmailAliases();
		for(MailBoxAliasDSO i : mailboxAliaseList)
		{
			if(i.emailSrc.equals(email))
			{
				isInMailAliaseFile = true;
				break;
			}
		}
		if(!isInMailAliaseFile)
		{
			//SET NEW OBJECT
			MailBoxAliasDSO dso = new MailBoxAliasDSO();
			dso.emailSrc = email;
			dso.emailDest = email;
			mailboxAliaseList.add(dso);
			
			//WRITE UPDATE TO FILE
			status = this.updatePostFixEmailAliase(mailboxAliaseList);
			if(!status)
				return false;
		}		
		
		//STEP 4. CHECK THE VIRTUAL UID FILE TO DETERMINE IF AN ENTRY EXISTS ... IF NOT ADD IT
		boolean isInMailVirtualFile = false;
		List<MailBoxVirtualDSO> mailboxVirtualList = this.getPostFixEmailVirtual();
		for(MailBoxVirtualDSO i : mailboxVirtualList)
		{
			if(i.virtualEmail.equals(email))
			{
				isInMailVirtualFile = true;
				break;
			}
		}
		if(!isInMailVirtualFile)
		{
			//SET NEW OBJECT
			MailBoxVirtualDSO dso = new MailBoxVirtualDSO();
			dso.virtualEmail = email;
			dso.virtualUid = uid;
			mailboxVirtualList.add(dso);
			
			//WRITE UPDATE TO FILE
			status = this.updatePostFixEmailVirtual(mailboxVirtualList);
			if(!status)
				return false;
		}
				
		return true;
	}

	public boolean deleteEmailAccount(String email, String domain)
	{
		boolean status;
		
		//GET SYSTEM USER BASED ON DOMAIN
		String sysUser = UnixSystem.getUserFromDomain(domain);
		if(sysUser == null)
		{
			this.errorManager.setErrorMessage( String.format("We could not find any user associated with %s", domain) );
			return false;
		}
		
		//GET UID/GID
		int[] uid_gid_pair = UnixSystem.getUnixUIDGIDPair(sysUser);
		int uid = uid_gid_pair[0];
		int gid = uid_gid_pair[1];
		
		//STEP 1. CHECK IF THERE IS A MAILBOX FILE FOR THIS EMAIL ADDRESS
		boolean isInMailBoxFile = false;
		MailBoxFileDSO filePtr = null;
		List<MailBoxFileDSO> mailboxFileList = this.getPostFixEmailMailbox();
		for(MailBoxFileDSO i : mailboxFileList)
		{
			if(i.emailAddress.equals(email))
			{
				isInMailBoxFile = true;
				filePtr = i;
				break;
			}
		}
		if(isInMailBoxFile)
		{
			mailboxFileList.remove(filePtr);
			status = this.updatePostFixEmailMailBox(mailboxFileList);
			if(!status)
				return false;
		}
		else
		{
			this.errorManager.setErrorMessage( String.format("We could not find any email associated with %s", email) );
			return false;
		}
		
		//STEP 2. CHECK IF VMAIL_ALIASES FILE HAS ALIASE ... REMOVE IF EXIST
		boolean isInMailAliaseFile = false;
		MailBoxAliasDSO aliasePtr = null;
		List<MailBoxAliasDSO> mailboxAliaseList = this.getPostFixEmailAliases();
		for(MailBoxAliasDSO i : mailboxAliaseList)
		{
			if(i.emailSrc.equals(email))
			{
				isInMailAliaseFile = true;
				aliasePtr = i;
				break;
			}
		}
		if(isInMailAliaseFile)
		{
			mailboxAliaseList.remove(aliasePtr);
			status = this.updatePostFixEmailAliase(mailboxAliaseList);
			if(!status)
				return false;
		}	
		
		//STEP 3. CHECK THE VIRTUAL UID FILE TO DETERMINE IF AN ENTRY EXISTS ... IF NOT ADD IT
		boolean isInMailVirtualFile = false;
		MailBoxVirtualDSO virtualPtr = null;
		List<MailBoxVirtualDSO> mailboxVirtualList = this.getPostFixEmailVirtual();
		for(MailBoxVirtualDSO i : mailboxVirtualList)
		{
			if(i.virtualEmail.equals(email))
			{
				isInMailVirtualFile = true;
				virtualPtr = i;
				break;
			}
		}
		if(isInMailVirtualFile)
		{
			mailboxVirtualList.remove(virtualPtr);
			status = this.updatePostFixEmailVirtual(mailboxVirtualList);
			if(!status)
				return false;
		}
		
		//STEP 4. DETERMINE IF WE NEED TO DELETE A DOMAIN FROM OUR DOMAINS FILE
		boolean anymoreDomainsAssociatedWithEmailAddress = false;
		for(MailBoxFileDSO i : mailboxFileList)
		{
			if(i.emailDomain.equals(domain))
			{
				anymoreDomainsAssociatedWithEmailAddress = true;
				break;
			}
		}
		if(!anymoreDomainsAssociatedWithEmailAddress)
		{
			List<String> domainList = this.getPostFixEmailDomains();
			if(domainList.contains(domain))
			{
				domainList.remove(domain);
				status = this.updatePostFixEmailDomains(domainList);		//WRITE NEW DOMAIN LIST DATA TO FILE
				if(!status)
					return false;
			}
		}
		
		return true;
	}
	
	
}
