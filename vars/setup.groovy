#!/usr/bin/env groovy

def call(config) {
	def container

	pipeline {
		agent any
		
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
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						NEXUS_IMAGE = configF.nexus.NEXUS_IMAGE
						println "Imagen para subir a Nexus: " + NEXUS_IMAGE						
						println "----------------------------------" 
						
						sh 'docker build -t ' + NEXUS_IMAGE + ' .'
					}
				}
			}
			
			stage("Nexus") {
				steps {
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						NEXUS_URL = configF.nexus.NEXUS_URL
						println "URL Nexus: " + NEXUS_URL
						NEXUS_REPOSITORY = configF.nexus.NEXUS_REPOSITORY
						println "Reporitorio Nexus: " + NEXUS_REPOSITORY
						NEXUS_CREDENTIAL_ID = configF.nexus.NEXUS_CREDENTIAL_ID
						println "Credenciales Nexus: " + NEXUS_CREDENTIAL_ID
						NEXUS_IMAGE = configF.nexus.NEXUS_IMAGE
						println "Imagen para subir a Nexus: " + NEXUS_IMAGE						
						println "----------------------------------" 
		
						sh 'docker image ls'
						
						withDockerRegistry(credentialsId: 'nexusCredenciales', url: 'http://192.168.1.57:8083') {							  
							sh 'docker tag ' + NEXUS_IMAGE + ' ' + NEXUS_URL + NEXUS_REPOSITORY + NEXUS_IMAGE
							sh 'docker push ' + NEXUS_URL + NEXUS_REPOSITORY + NEXUS_IMAGE
						}
	
					}
				
				}
			}
			
		}
	}
}
