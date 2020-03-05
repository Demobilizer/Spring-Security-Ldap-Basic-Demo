/**
 * 
 */
package com.demo;

import java.security.Principal;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

/**
 * @author Mehul
**/

@RestController
public class LdapCrudController {

	@Autowired
	private LdapPersonServiceUsingDirContext ldapPersonService;
	
	@Autowired
	private LdapPersonServiceUsingPersonAtrributeMapper ldapService;
	
	  @GetMapping("/")
	  public String index(Principal principal) throws NamingException {
		  
		  // to add USER to LDAP server:  	ldapPersonService.addUser();
		  // to retrive USER from LDAP server:  	ldapPersonService.getAllUsers();
	    return "Welcome to the home page! "+principal.getName();
	  }
	  
	  @PostMapping("/add-user-dir-context")
	  public String addUserToLdapServer(@RequestBody Person person) {
		  ldapPersonService.addUser();
		  return "Person added to LDAP Server!";
	  }
	  
	  @GetMapping("/get-users-dir-context")
	  public List<Attributes> getAllUsersFromLdapServer() throws NamingException {
		  return ldapPersonService.getAllUsers();
	  }
	  
	  @PostMapping("/add-user")
	  public String addUserToLdapServer1(@RequestBody String jsonStr) throws ParseException {
		  
					  
		  ldapService.addUser(jsonStr);
		  
		  // to add the unique member ingroupOfUniqueNames
		  
		  ldapService.addUniqueMember(jsonStr);
		  return "Person added to LDAP Server!";
	  }
	  
	  @GetMapping("/get-users")
	  public List<Person> getAllUsersFromLdapServer1() throws NamingException {
		  return ldapService.getAllUsers();
	  }
	  
	  @PutMapping("/update-user")
	  public String updateUserToLdapServer(@RequestBody Person person) {
		  ldapService.updateUser(person);
		  return "updated!!";
	  }
	  
	  @DeleteMapping("/remove-user/{emailId}")
	  public List<Person> removeUserFromLdapServer(@PathVariable String emailId) 
			  throws NamingException {
		  ldapService.removeUser(emailId);
		  return ldapService.getAllUsers();
	  }
	  
}
