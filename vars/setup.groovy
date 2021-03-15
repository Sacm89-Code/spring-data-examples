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
					
						nexusPublisher (
							nexusVersion: NEXUS_VERSION,
							protocol: NEXUS_PROTOCOL,
							nexusUrl: NEXUS_URL,
							groupId: '',
							version: '',
							repository: NEXUS_REPOSITORY,
							credentialsId: NEXUS_CREDENTIAL_ID,
							packages: [
								[artifactId: '2.0-SNAPSHOT',
								classifier: '',
								file: 'spring-data-examples',
								type: 'jar']
							]
						);
						
						//nexusPublisher nexusInstanceId: 'nexus_local', nexusRepositoryId: 'spring-data-example-dockerfile', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'spring-data-examples']], mavenCoordinate: [artifactId: 'spring-data-examples', groupId: 'org.springframework.data.examples', packaging: 'jar', version: '2.0.0.BUILD-SNAPSHOT']]], tagName: '2.0-SNAPSHOT' 
						//nexusPublisher nexusInstanceId: 'nexus_local', nexusRepositoryId: 'spring-data-example-dockerfile', packages: []
						
						//sh 'docker push ' + NEXUS_URL + '/repository/' + NEXUS_REPOSITORY  + 'spring-data-examples:2.0-SNAPSHOT'
						//sh 'docker push ' + NEXUS_URL + '/' + NEXUS_REPOSITORY  + 'spring-data-examples:2.0-SNAPSHOT'
						
					}
				
				}
			}
			
		}
	}
}
