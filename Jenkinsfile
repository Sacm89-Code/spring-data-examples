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
        NEXUS_REPOSITORY = "springs-data-examples-elasticsearch/"
        // Identificación de credencial de Jenkins para autenticarse en Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexusCredenciales"
    }
	
    stages {
        stage('Setup') {
            steps {
                git url:'https://github.com/mirgs/spring-data-examples.git', branch: 'elasticsearch'
            }
        } 
		
		// Compilamos el proyecto y almacenamos los test unitarios y de integracion
       	stage('Build') {
        	steps {
				withMaven (maven: 'maven-3.6.3') {
					sh 'mvn clean install -f elasticsearch/pom.xml'
				}
    		}
			
			post {
                always {
					junit 'elasticsearch/example/target/surefire-reports/*.xml, elasticsearch/reactive/target/surefire-reports/*.xml, elasticsearch/rest/target/surefire-reports/*.xml'
                }
            }
        }
		
        // Lanzamos en paralelo la comprobacion de dependencias y los mutation test
        stage('Mutation Test') {
			// Lanzamos los mutation test
			
			steps {				
				withMaven (maven: 'maven-3.6.3') {	
					sh 'mvn org.pitest:pitest-maven:mutationCoverage -f elasticsearch/pom.xml'				
				}
			}
			
        }
		
        // Analizamos con SonarQube el proyecto y pasamos los informes generados (test, cobertura, mutation)
        stage('SonarQube analysis') {
        	steps {
		    	withSonarQubeEnv(credentialsId: 'sonarQubeCredenciales', installationName: 'local') {
					withMaven (maven: 'maven-3.6.3') {
						sh 'mvn sonar:sonar -f elasticsearch/pom.xml \
						-Dsonar.sourceEncoding=UTF-8 \
						-Dsonar.junit.reportPaths=target/surefire-reports'
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
		
		stage("Nexus Example") {
            steps {
                script {
                    pom = readMavenPom file: "elasticsearch/example/pom.xml";
                    filesByGlob = findFiles(glob: "elasticsearch/example/target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.parent.groupId}, packaging: ${pom.packaging}, version ${pom.parent.version}";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            /*groupId: pom.groupId,*/
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
		
		stage("Nexus Reactive") {
            steps {
                script {
                    pom = readMavenPom file: "elasticsearch/reactive/pom.xml";
                    filesByGlob = findFiles(glob: "elasticsearch/reactive/target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.parent.groupId}, packaging: ${pom.packaging}, version ${pom.parent.version}";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            /*groupId: pom.groupId,*/
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
		
		stage("Nexus Rest") {
            steps {
                script {
                    pom = readMavenPom file: "elasticsearch/rest/pom.xml";
                    filesByGlob = findFiles(glob: "elasticsearch/rest/target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.parent.groupId}, packaging: ${pom.packaging}, version ${pom.parent.version}";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            /*groupId: pom.groupId,*/
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