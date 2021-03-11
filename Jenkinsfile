#!/usr/bin/env groovy
@Library('spring-data-examples')_
def config = readYaml (file: 'spring-data-examples/cursofile')

//creamos el map env                                              
config.keySet().each {                       
    //env."${it}" = config[it] 
	line -> println line
}
 
setup config
