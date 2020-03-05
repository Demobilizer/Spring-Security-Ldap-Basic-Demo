/**
 * 
 */
package com.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Service;

/**
 * @author Mehul
**/

@Service
public class LdapPersonServiceUsingDirContext {

	public List<Attributes> getAllUsers() throws NamingException
	{
		
		Properties initialProperties = new Properties();
		initialProperties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		initialProperties.put(Context.PROVIDER_URL, "ldap://localhost:10389");
		initialProperties.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		initialProperties.put(Context.SECURITY_CREDENTIALS, "secret");
		DirContext  context = new InitialDirContext(initialProperties);
		
		String searchFilter="(objectClass=inetOrgPerson)";
		String[] requiredAttributes={"sn","cn","uid"};
		SearchControls controls=new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(requiredAttributes);
		NamingEnumeration users=context.search("ou=People,dc=ldap-authe-demo,dc=com", searchFilter, controls);
		SearchResult searchResult=null;
		String commonName=null;
		String surName=null;
		String employeeNum=null;
		List<Attributes> attribList = new ArrayList<Attributes>();
		while(users.hasMore())
		{
			
			searchResult=(SearchResult) users.next();
			Attributes attr=searchResult.getAttributes();
			System.out.println("attr ------------ "+attr.toString());
			commonName=attr.get("cn").get(0).toString();
			surName=attr.get("sn").get(0).toString();
			employeeNum=attr.get("uid").get(0).toString();
			System.out.println("Name = "+commonName);
			System.out.println("Surname  = "+surName);
			System.out.println("uid = "+employeeNum);
			System.out.println("-------------------------------------------");
			attribList.add(attr);
			
		}
		return attribList;
	}
	
	public void addUser() {
		
		try
		  {
			Properties initialProperties = new Properties();
			initialProperties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			initialProperties.put(Context.PROVIDER_URL, "ldap://localhost:10389");
			initialProperties.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
			initialProperties.put(Context.SECURITY_CREDENTIALS, "secret");
			DirContext  context = new InitialDirContext(initialProperties);
			
			  Attributes attributes =new BasicAttributes();
			  Attribute attribute =new BasicAttribute("objectClass");
			  attribute.add("inetOrgPerson");
			  attribute.add("organizationalPerson");
			  attribute.add("person");
			  
			  attributes.put(attribute);
			  Attribute sn =new BasicAttribute("sn");
			  sn.add("test sn");
			  Attribute cn =new BasicAttribute("cn");
			  cn.add("test cn");
			  
			  attributes.put(sn);
			  attributes.put(cn);
			 
			 attributes.put("userPassword", "test");
			 
			 //System.out.println("attribs ----- "+attributes.toString());
			 
			 // Success Key: Never forget to add uid/cn to following method's first param!!
			 // it actually denots first dn of generating element!
			 
			 context.createSubcontext("uid=test11,ou=People,dc=ldap-authe-demo,dc=com",attributes);
			  
			  System.out.println(" success");
		 }
		  catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
}
