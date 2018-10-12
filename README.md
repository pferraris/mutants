# Mutants Detector

[![Build Status](https://travis-ci.org/pferraris/mutants.svg?branch=master)](https://travis-ci.org/pferraris/mutants)
[![codecov](https://codecov.io/gh/pferraris/mutants/branch/master/graph/badge.svg)](https://codecov.io/gh/pferraris/mutants)

API to detect mutants based on their DNA.

## Using the library

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
