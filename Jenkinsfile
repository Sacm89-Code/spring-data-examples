#!/usr/bin/env groovy
@Library('spring-data-examples')_

pipeline {
    agent any
	
    stages {
        stage('Setup') {
            steps {
				echo '---------------------------------'
				echo ${cursofile.setup_url()}
				echo '---------------------------------'
                //git url:'https://github.com/mirgs/spring-data-examples.git', branch: 'web'
            }
        } 
	
		// Compilamos el proyecto y almacenamos los test unitarios y de integracion
/*     	stage('Build') {
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
                always {*/
                    //junit 'web/example/target/surefire-reports/*.xml, web/projection/target/surefire-reports/*.xml, web/querydsl/target/surefire-reports/*.xml'
               /* }
            }
        }*/

		// Lanzamos en paralelo la comprobacion de dependencias y los mutation test
        /*stage('Mutation Test') {
			// Lanzamos los mutation test
			
			steps {				
				withMaven (maven: 'maven-3.6.3') {		
					sh 'mvn org.pitest:pitest-maven:mutationCoverage -f web/pom.xml'				
				}
			}
			
        }*/
		
        // Analizamos con SonarQube el proyecto y pasamos los informes generados (test, cobertura, mutation)
        /*stage('SonarQube analysis') {
        	steps {				
				withSonarQubeEnv(credentialsId: 'sonarQubeCredenciales', installationName: 'local') {
					withMaven (maven: 'maven-3.6.3') {
						sh 'mvn sonar:sonar -f web/pom.xml \
						-Dsonar.sourceEncoding=UTF-8 \
						-Dsonar.junit.reportPaths=target/surefire-reports'
					}
				}
			}
		}*/
		
		// Esperamos hasta que se genere el QG y fallamos o no el job dependiendo del estado del mismo
		/*stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
					// Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
					// true = set pipeline to UNSTABLE, false = don't
                    // Requires SonarQube Scanner for Jenkins 2.7+
                    waitForQualityGate abortPipeline: true
                }
            }
        } */       
		
		/*stage("Nexus") {
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
		}*/
		
    }
}
