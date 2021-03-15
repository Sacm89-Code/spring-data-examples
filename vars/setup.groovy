#!/usr/bin/env groovy

def call(config) {

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
						
					//withMaven (maven: 'maven-3.6.3') {
					
					
							sh 'sudo docker build -t spring-data-examples:2.0-SNAPSHOT .'
							
							
					//}
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
												
						/*for (proyect2 in proyectsArray2) {						
							
							pom = readMavenPom file: "web/" + proyect2 + "/pom.xml";
							filesByGlob = findFiles(glob: "web/" + proyect2 + "/target/*.${pom.packaging}");
							echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
							artifactPath = filesByGlob[0].path;
							artifactExists = fileExists artifactPath;
							if(artifactExists) {
								echo "*** File: ${artifactPath}, group: ${pom.parent.groupId}, packaging: ${pom.packaging}, version ${pom.parent.version}";
								nexusArtifactUploader(
									nexusVersion: NEXUS_VERSION,
									protocol: NEXUS_PROTOCOL,
									nexusUrl: NEXUS_URL,
									groupId: pom.groupId,
									version: pom.parent.version,
									repository: NEXUS_REPOSITORY,
									credentialsId: NEXUS_CREDENTIAL_ID,
									artifacts: [
										[artifactId: pom.artifactId,
										classifier: '',
										file: artifactPath,
										type: pom.packaging],
										[artifactId: pom.artifactId,
										classifier: '',
										file: "pom.xml",
										type: "pom"]
									]
								);
							} else {
								error "*** File: ${artifactPath}, could not be found";
							}
						}*/
					}
				
				}
			}
			
		}
	}
}
