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
	     	stage('Build') {
				steps {
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						proyectsArray = configF.setup.proyectsArray 
						println "Lista de arrays subproyectos: " + proyectsArray
						println "----------------------------------"  
				
						
						withMaven (maven: 'maven-3.6.3') {
							for (proyect in proyectsArray) {
								println proyect
								sh 'mvn clean install -f ' + proyect
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

			// Lanzamos en paralelo la comprobacion de dependencias y los mutation test
			stage('Mutation Test') {
				// Lanzamos los mutation test
				
				steps {	
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						ficheroPom = configF.setup.ficheroPom 
						println "Fichero pom del proyecto padre: " + ficheroPom
						println "----------------------------------"  
						
						withMaven (maven: 'maven-3.6.3') {		
							sh 'mvn org.pitest:pitest-maven:mutationCoverage -f ' + ficheroPom				
						}
					}
				}
				
			}
			
			// Analizamos con SonarQube el proyecto y pasamos los informes generados (test, cobertura, mutation)
			stage('SonarQube analysis') {
				steps {	
					script {
						println "----------------------------------" 
						configF = readYaml (file: config)
						ficheroPom = configF.setup.ficheroPom 
						println "Fichero pom del proyecto padre: " + ficheroPom
						println "----------------------------------"  
						
						withSonarQubeEnv(credentialsId: 'sonarQubeCredenciales', installationName: 'local') {
							withMaven (maven: 'maven-3.6.3') {
								sh 'mvn sonar:sonar -f web/pom.xml \
								-Dsonar.sourceEncoding=UTF-8 \
								-Dsonar.junit.reportPaths=target/surefire-reports'
							}
							/*withMaven (maven: 'maven-3.6.3') {
								sh 'mvn sonar:sonar -f ' + ficheroPom + ' \
								-Dsonar.sourceEncoding=UTF-8 \
								-Dsonar.junit.reportPaths=target/surefire-reports'
							}*/
						}
					}
				}
			}
			
			// Esperamos hasta que se genere el QG y fallamos o no el job dependiendo del estado del mismo
			stage("Quality Gate") {
				steps {
					timeout(time: 5, unit: 'MINUTES') {
						// Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
						// true = set pipeline to UNSTABLE, false = don't
						// Requires SonarQube Scanner for Jenkins 2.7+
						waitForQualityGate abortPipeline: true
					}
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
												
						for (proyect2 in proyectsArray2) {						
							
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
						}
					}
				
				}
			}
			
		}
	}
}
