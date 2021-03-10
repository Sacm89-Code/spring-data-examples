#!/usr/bin/env groovy

#Para el stage Setup
String setup_url() {
	return 'https://github.com/mirgs/spring-data-examples.git'
}

String setup_branch() {
	return 'web'
}

#Para stage Build
List setup_build() {
	List proyectsArray = ["web/example/pom.xml", "web/projection/pom.xml", "web/querydsl/pom.xml"]
	return proyectsArray
}


#Para stage Mutation Test y SonarQube analysis
String mutation_test_ficheroPom() {
	String ficheroPom = web/pom.xml
	return ficheroPom
}

#Para stage Nexus
List nexus_subProject() {
	List proyectsArray2 = ["example", "projection", "querydsl"]
	return proyectsArray2
}
String nexus_version() {
	String NEXUS_VERSION = "nexus3"
	return NEXUS_VERSION
}
String nexus_protocol() {
	String NEXUS_PROTOCOL = "http"
	return NEXUS_PROTOCOL
}
String nexus_url() {
	String NEXUS_URL = "192.168.1.57:9084"
	return NEXUS_URL
}
String nexus_repository() {
	String NEXUS_REPOSITORY = "springs-data-examples-web/"
	return NEXUS_REPOSITORY
}
String nexus_credential_id() {
	String NEXUS_CREDENTIAL_ID = "nexusCredenciales"
	return NEXUS_CREDENTIAL_ID
}
