Spring-Security-Ldap-Basic-Demo:

1) to add pperson:
		
		Post Req: http://localhost:8080/add-user

		in Authoeization: select basic auth
		provide credentials

		in body: Row Json as per follow

		{
			"person":{
					    "cn": "test5",
					    "sn": "testing5",
					    "emailId": "testing5@again.test5",
					    "userPassword": "test5"
					 },
			"role":"admin/user",
			"department":"Developers/HR"
		}


