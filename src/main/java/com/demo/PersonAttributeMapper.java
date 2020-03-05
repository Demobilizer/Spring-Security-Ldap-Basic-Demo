/**
 * 
 */
package com.demo;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Mehul
**/

@Component
public class PersonAttributeMapper implements AttributesMapper<Person> {

	public static final String BASE_DN = "dc=ldap-authe-demo,dc=com";
	
	@Override
	public Person mapFromAttributes(Attributes attributes) throws NamingException {
		Person person = new Person();
		person.setEmailId(null != attributes.get("uid") ? attributes.get("uid").get().toString() : null);
		person.setCn(null != attributes.get("cn") ? attributes.get("cn").get().toString() : null);
		person.setSn(null != attributes.get("sn") ? attributes.get("sn").get().toString() : null);
		person.setUserPassword(
				null != attributes.get("userPassword") ? attributes.get("userPassword").get().toString() : null);
		return person;
	}
	
	public Attributes buildAttributes(Person p) {

		BasicAttribute ocattr = new BasicAttribute("objectClass");
		ocattr.add("top");
		ocattr.add("person");
		ocattr.add("organizationalPerson");
		ocattr.add("inetOrgPerson");

		Attributes attrs = new BasicAttributes();
		attrs.put(ocattr);
		attrs.put("uid", p.getEmailId());
		attrs.put("cn", p.getCn());
		attrs.put("sn", p.getSn());
		attrs.put("userPassword", p.getUserPassword());
		return attrs;
	}
	
	public Name buildDn(String emailId) {
		return LdapNameBuilder.newInstance().add("ou", "People").add("uid", emailId).build();
	}
	
	public Name buildDnPerson(String emailId) {
		return LdapNameBuilder.newInstance(BASE_DN).add("ou", "People").add("uid", emailId).build();
	}
	
	/*
	 * public Attributes buildGroupAttributes(Name uniqueMember, String cn) {
	 * 
	 * BasicAttribute ocattr = new BasicAttribute("objectClass"); ocattr.add("top");
	 * ocattr.add("groupOfUniqueNames");
	 * 
	 * BasicAttribute ocattr2 = new BasicAttribute("uniqueMember");
	 * 
	 * System.out.println("unique member ---------- "+uniqueMember.toString());
	 * ocattr2.add(uniqueMember.toString());
	 * 
	 * Attributes attrs = new BasicAttributes(); attrs.put(ocattr);
	 * attrs.put(ocattr2); attrs.put("cn",cn);
	 * 
	 * return attrs; }
	 */
	
	public Name buildDnForOu(String dept, String role) {
		return LdapNameBuilder.newInstance().add("ou", dept).add("cn", role).build();
				//.add("uniqueMember", personDn.toString()).build();
	}

}
