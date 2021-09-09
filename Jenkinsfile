#!/usr/bin/env groovy

pipeline {
    agent any	
	
	environment {
        // Puede ser nexus3 o nexus2
        NEXUS_VERSION = "nexus3"
        // Puede ser http o https
        NEXUS_PROTOCOL = "http"
        // Dónde se ejecuta tu Nexus
        NEXUS_URL = "192.168.1.66:8081"
        // Repositorio donde subiremos el artefacto
        NEXUS_REPOSITORY = "springs-data-examples-web/"
        // Identificación de credencial de Jenkins para autenticarse en Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexusCredenciales"
    }
	
    stages {
        stage('Setup') {
            steps {
                git url:'https://github.com/Sacm89-Code/spring-data-examples.git', branch: 'web'
            }
        } 
		
		// Compilamos el proyecto y almacenamos los test unitarios y de integracion
       	stage('Build') {
        	steps {
				script {
			
					List proyectsArray = ["web/example/pom.xml", "web/projection/pom.xml", "web/querydsl/pom.xml"]
					
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
				withMaven (maven: 'maven-3.6.3') {		
					sh 'mvn org.pitest:pitest-maven:mutationCoverage -f web/pom.xml'				
				}
			}
			
        }
		
        // Analizamos con SonarQube el proyecto y pasamos los informes generados (test, cobertura, mutation)
        stage('SonarQube analysis') {
        	steps {
		    	withSonarQubeEnv(credentialsId: 'sonarqube_token', installationName: 'sonarqube') {
					withMaven (maven: 'maven-3.6.3') {
						sh 'mvn sonar:sonar -f web/pom.xml \
                        -Dsonar.sourceEncoding=UTF-8 \
                        -Dsonar.junit.reportPaths=target/surefire-reports'
					}
			}
		}
	}        
		
		stage("Nexus") {
			steps {
				script {
					List proyectsArray2 = ["example", "projection", "querydsl"]
					
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
