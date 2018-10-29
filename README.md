# Mutants Detector

[![Build Status](https://travis-ci.org/pferraris/mutants.svg?branch=master)](https://travis-ci.org/pferraris/mutants)
[![codecov](https://codecov.io/gh/pferraris/mutants/branch/master/graph/badge.svg)](https://codecov.io/gh/pferraris/mutants)

API to detect mutants based on their DNA.

## Endpoints

### __Check if service is available:__
``` http
GET /ping
```
_Returns:_
``` js
200 OK
pong
```

### __Get stats of detections performed:__
``` http
GET /stats
```
_Returns:_
``` js
200 OK
{
    "count_mutant_dna": 40,
    "count_mutant_dna": 100,
    "ratio": 0.4
}
```

### __Get stats of detections performed:__
``` http
POST /mutant
```
_Body example:_
``` js
{
  "dna": [ "ATGCGA", "CTGTTG", "TTATGT", "AGAAGG", "CCTCTA", "TCACTG" ]
}
```
_Returns:_
``` js
200 OK // If DNA correspond to a Mutant
403 FORBIDDEN // If DNA correspond to a Human
```

# Testing Online

A demo solution coul be found hosted on:
```
http://mutants.southcentralus.cloudapp.azure.com/
```

# Testing locally with Docker

To test in an easy way, the solution provides a file docker-compose.yml with the necessary configuration to compile and execute the complete solution with all its dependencies.

## Prerequisites

- Docker engine 18.06
- Docker compose 1.22

Clone the repository:
```
git clone https://github.com/pferraris/mutants.git
cd mutants
```

Build the images:
```
docker-compose build
```

Pull auxiliar images for Elasticsearch, RabbitMQ and Memcached:
```
docker-compose pull
```

Up the containers and follow logs:
```
docker-compose up
```

__This makes the REST API available on port 8080, and executes the background worker.__

For stopping press CTRL+c for detach from console. Then for remove containers execute:
```
docker-compose down
```

# Testing locally with Maven

## Prerequisites

- JDK 8
- Maven 3.5
- RabbitMQ broker for async operations
- Elasticsearch instance for persistence
- Memcached instance for improve speed

Clone the repository:
```
git clone https://github.com/pferraris/mutants.git
cd mutants
```

Compile and install modules with maven:
```
mvn clean install
```

Set the environment variables with the connection strings to the services (following are the default values)
```
RABBIT_CONNECTIONSTRING = amqp://admin:admin@localhost
ELASTIC_CONNECTIONSTRING = http://localhost:9200
MEMCACHED_CONNECTIONSTRING = localhost:11211
```

Run REST API in background, then run Worker process:
```
cd rest
mvn jetty:run &
cd ../worker
mvn exec:java
```

For stop processes, kill worker process with CTRL-C and execute:
```
cd ../rest
mvn jetty:stop
```

You can also use two independent consoles. Remember to set environment variables in both.

# Using the library in your project

__detection__ is an artifact writen in Java 8. It performs validations over the DNA sample and determine whether a DNA sample belongs to a mutant or not.

The way to do it is by discovering sequences of repeated nitrogenous bases in a DNA matrix.

The sequences can be found horizontally, vertically and obliquely.

Currently there are 2 strategies to perform this search. By default, the matrix is scanned from left to right and from top to bottom, looking for sequences in 4 directions which ensures a total sweep.

The other strategy consists in unraveling the matrix in its horizontal, vertical and oblique lines, and then searching the sequences in the obtained lines.

### Importing the clases to use

```java
import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.DnaException;
```

### Example code

```java
// The DNA sample
String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };

// Instance a Detector with default strategy and values:
// Method = Nitrogenous bases sequences
// Strategy = Matrix scan
// Sequence count = 2
// Secuence size = 4
Detector detector = new Detector();
boolean result;

// Perform detection
try {
    result = detector.isMutant(dna);
} catch (DnaException e) {
    e.printStackTrace();
}

// Using the result...
```
