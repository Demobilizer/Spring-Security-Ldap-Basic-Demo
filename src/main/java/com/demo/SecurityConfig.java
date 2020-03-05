/**
 * 
 */
package com.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

/**
 * @author Mehul
**/

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	Environment env;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.anyRequest()
			.fullyAuthenticated()
			.and()
			.httpBasic();
			//.formLogin();
	}

	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.ldapAuthentication()
				.userDnPatterns("uid={0},ou=people")
				.groupSearchBase("ou=HR")
				.contextSource()
					//.url("ldap://localhost:8082/dc=springframework,dc=org")
					.url("ldap://localhost:10389/dc=ldap-authe-demo,dc=com")
				.and()
				.passwordCompare()
					//.passwordEncoder(new BCryptPasswordEncoder())
					.passwordEncoder(new LdapShaPasswordEncoder())
					.passwordAttribute("userPassword");
	}
	
	@Bean
    public LdapContextSource contextSource () {
        LdapContextSource contextSource= new LdapContextSource();
        contextSource.setUrl(env.getRequiredProperty("ldap.url"));
        contextSource.setBase(env.getRequiredProperty("ldap.base"));
        contextSource.setUserDn(env.getRequiredProperty("ldap.user"));
        contextSource.setPassword(env.getRequiredProperty("ldap.password"));
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

	
	
	
	/*
	 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
	 * Exception {
	 * auth.ldapAuthentication().userDnPatterns("uid={0},ou=people").groupSearchBase
	 * ("ou=groups").contextSource()
	 * .url("ldap://localhost:10389/dc=springframework,dc=com").and().
	 * passwordCompare() .passwordEncoder(new
	 * BCryptPasswordEncoder()).passwordAttribute("userPassword"); }
	 */
	
	/*
	 * private PasswordEncoder newPasswordEncoder() { final BCryptPasswordEncoder
	 * crypt = new BCryptPasswordEncoder(); return new PasswordEncoder() {
	 * 
	 * @Override public String encode(CharSequence rawPassword) {
	 * 
	 * return "{CRYPT}" + crypt.encode(rawPassword); }
	 * 
	 * @Override public boolean matches(CharSequence rawPassword, String
	 * encodedPassword) { // remove {CRYPT} prefix return crypt.matches(rawPassword,
	 * encodedPassword.substring(7)); } }; }
	 */

	/*
	 * private PasswordEncoder sShaPasswordEncoder() { final LdapShaPasswordEncoder
	 * crypt = new LdapShaPasswordEncoder(); return new PasswordEncoder() {
	 * 
	 * @Override public String encode(CharSequence rawPassword) {
	 * 
	 * return "{SHA512}" + crypt.encode(rawPassword); }
	 * 
	 * @Override public boolean matches(CharSequence rawPassword, String
	 * encodedPassword) { // remove {CRYPT} prefix return crypt.matches(rawPassword,
	 * encodedPassword.substring(7)); } }; }
	 */
	
}
