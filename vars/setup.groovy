#!/usr/bin/env groovy

def call(config) {

	pipeline {
		agent any
		//agent { dockerfile true }
		
		/*tools {
			docker 'docker-image'
		}*/
		
		stages {
			
			stage('Setup') {
				steps {
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						giturl = configF.setup.setup_url 
						println "URL GIT: " + giturl
						gitbranch = configF.setup.setup_branch						  
						println "RAMA GIT: " + gitbranch
						println "----------------------------------"        
						
						git url: giturl, branch: gitbranch
					}
				}
			}
		
			// Compilamos el proyecto y almacenamos los test unitarios y de integracion
	     	stage('Build-Dockerfile') {
				steps {
						sh 'docker build -t spring-data-examples:2.0-SNAPSHOT .'
				}
			}
			
			stage("Nexus") {
				steps {
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						println "Configuraciones para utilizar Nexus"
						proyectsArray2 = configF.nexus.proyectsArray2
						println "Lista de arrays de los subproyectos: " + proyectsArray2
						NEXUS_VERSION = configF.nexus.NEXUS_VERSION
						println "Version Nexus: " + NEXUS_VERSION
						NEXUS_PROTOCOL = configF.nexus.NEXUS_PROTOCOL
						println "Protocolo Nexus: " + NEXUS_PROTOCOL
						NEXUS_URL = configF.nexus.NEXUS_URL
						println "URL Nexus: " + NEXUS_URL
						NEXUS_REPOSITORY = configF.nexus.NEXUS_REPOSITORY
						println "Reporitorio Nexus: " + NEXUS_REPOSITORY
						NEXUS_CREDENTIAL_ID = configF.nexus.NEXUS_CREDENTIAL_ID
						println "Credenciales Nexus: " + NEXUS_CREDENTIAL_ID
						println "----------------------------------"  
												
						sh 'docker push 127.0.0.1:9084/repository/' + NEXUS_REPOSITORY + 'spring-data-examples:2.0-SNAPSHOT'
						
						
						}
					}
				
				}
			}
			
		}
	}
}
