#!/usr/bin/env groovy
pipeline {
    agent any	
	
	environment {
        // Puede ser nexus3 o nexus2
        NEXUS_VERSION = "nexus3"
        // Puede ser http o https
        NEXUS_PROTOCOL = "http"
        // Dónde se ejecuta tu Nexus
        //NEXUS_URL = "172.22.0.2:9084"
        NEXUS_URL = "192.168.1.57:9084"
        // Repositorio donde subiremos el artefacto
        NEXUS_REPOSITORY = "springs-data-examples-web/"
        // Identificación de credencial de Jenkins para autenticarse en Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexusCredenciales"
    }
	
    stages {
        stage('Setup') {
            steps {
                git url:'https://github.com/mirgs/spring-data-examples.git', branch: 'web'
            }
        } 
		
		// Compilamos el proyecto y almacenamos los test unitarios y de integracion
       	stage('Build') {
        	steps {
				script {
			
					List testArray = ["web/example/pom.xml", "web/projection/pom.xml", "web/querydsl/pom.xml"]
					
					withMaven (maven: 'maven-3.6.3') {
						for (i in testArray) {
							sh 'mvn clean install -f i'
						}
					}
				}
    		}
			
			post {
                always {
                    junit 'web/example/target/surefire-reports/*.xml, web/projection/target/surefire-reports/*.xml, web/querydsl/target/surefire-reports/*.xml'
                }
            }
        }		
		
    }
}
