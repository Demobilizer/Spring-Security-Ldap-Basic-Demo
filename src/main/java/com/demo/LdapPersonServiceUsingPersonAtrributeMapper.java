/**
 * 
 */
package com.demo;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

/**
 * @author Mehul
**/

@Service
public class LdapPersonServiceUsingPersonAtrributeMapper {

	@Autowired
	private LdapTemplate ldapTemplate;
	
	@Autowired
	private PersonAttributeMapper personAttributeMapper;
	
	private JSONObject getJsonObjFromJsonString(String jsonStr) throws ParseException {
		JSONObject jsonOfJsonStr = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		return (JSONObject) jsonParser.parse(jsonStr);
	}
	
	private Person getPersonFromJson(String jsonStr) throws ParseException {
		
		JSONObject jsonOfJsonStr = this.getJsonObjFromJsonString(jsonStr);
		
		JSONObject personJson = (JSONObject) jsonOfJsonStr.get("person");
		
		Gson gson = new Gson();
		Person person = gson.fromJson(personJson.toJSONString(), Person.class);
		
		return person;
	}
	
	private String getRoleForPerson(String jsonStr) throws ParseException {
		
		JSONObject jsonOfJsonStr = this.getJsonObjFromJsonString(jsonStr);
		
		return jsonOfJsonStr.get("role").toString();
	}
	
	private String getDepartment(String jsonStr) throws ParseException {
		
		JSONObject jsonOfJsonStr = this.getJsonObjFromJsonString(jsonStr);
		
		return jsonOfJsonStr.get("department").toString();
	}
	
	/*
	 * public List<String> getListOfUniqueMembers(String department, String role) {
	 * AndFilter filter = new AndFilter(); filter.and(new
	 * EqualsFilter("objectClass", "organizationalUnit")); //filter.and(new
	 * EqualsFilter("objectClass", "groupOfUniqueNames")); filter.and(new
	 * EqualsFilter("ou", department)); //filter.and(new EqualsFilter("cn", role));
	 * return ldapTemplate.search( "", filter.encode(), new
	 * AttributesMapper<String>() { public String mapFromAttributes(Attributes
	 * attrs) throws NamingException { //return (String)
	 * attrs.get("uniqueMember").get(); return (String)
	 * attrs.get("uniqueMember").getAll().toString(); } }); }
	 */

	/**
	 * @param person 
	 * @return 
	 * @throws ParseException 
	 */
	public String addUser(String jsonStr) throws ParseException {
		
		Person person = this.getPersonFromJson(jsonStr);
		
		Name dn = personAttributeMapper.buildDn(person.getEmailId());
		//String dn = person.getEmailId();
		ldapTemplate.bind(dn, null, personAttributeMapper.buildAttributes(person));
		return person.getEmailId() + " created successfully";
	}

	/**
	 * @return List<Person>
	 */
	public List<com.demo.Person> getAllUsers() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<Person> people = ldapTemplate.search(query().where("objectclass").is("person"),
				new PersonAttributeMapper());
		
		return people;
	}

	/**
	 * @param person
	 * @return personAttributeMapper
	 */
	public String updateUser(com.demo.Person person) {
		Name dn = personAttributeMapper.buildDn(person.getEmailId());
		ldapTemplate.rebind(dn, null, personAttributeMapper.buildAttributes(person));
		return person.getEmailId() + " updated successfully";
	}

	/**
	 * @param emailId
	 * @return 
	 */
	public String removeUser(String emailId) {
		Name dn = personAttributeMapper.buildDn(emailId);
		// ldapTemplate.unbind(dn, true); //Remove recursively all entries
		ldapTemplate.unbind(dn);
		return emailId + " removed successfully";
	}

	/**
	 * @param uniqueMember
	 * @throws ParseException 
	 */
	public String addUniqueMember(String jsonStr) throws ParseException {

		// get all entries of Group = department & groupOfUniqeNames = role

		String department = this.getDepartment(jsonStr);
		String role = this.getRoleForPerson(jsonStr);
		Person person = this.getPersonFromJson(jsonStr);

		Name personDn = personAttributeMapper.buildDnPerson(person.getEmailId());
		Name groupDn = personAttributeMapper.buildDnForOu(department, role);
		System.out.println("person dn -------- "+personDn);
		// ldapTemplate.rebind(groupDn, null,
		// personAttributeMapper.buildGroupAttributes(personDn, role));

		Attribute attr = new BasicAttribute("uniqueMember", personDn.toString());
		ModificationItem item = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
		ldapTemplate.modifyAttributes(groupDn, new ModificationItem[] { item });

		return groupDn + " updated successfully";

	}
	
	
	
}
