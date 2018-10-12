package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;
import ar.com.pabloferraris.mutants.detection.nitrogenousBases.MatrixScanNitrogenousBasesDetectionStrategy;

class MatrixScanNitrogenousBasesDetectionStrategyTests {

	static DetectionStrategy strategy;

	@BeforeAll
	static void initialize() {
		strategy = new NitrogenousBasesDetectionStrategyBuilder()
				.withStrategy(MatrixScanNitrogenousBasesDetectionStrategy.class)
				.withCount(2)
				.withSize(3)
				.build();
	}

	@Test
	@DisplayName("Find at least 2 sequences of at least 3 equals characters in a string list")
	void findSequencies() {
		String[] source = {
				"CABD",
				"BCAC",
				"BBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertTrue(result, "Must be true");
	}

	@Test
	@DisplayName("Cannot find sequences of at least 3 equals characters in a string list")
	void cannotFindSequencies() {
		String[] source = {
				"CABD",
				"BAAC",
				"DBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertFalse(result, "Must be false");
	}

	@Test
	@DisplayName("Find only 1 sequence of at least 3 equals characters in a string list")
	void onlyOneSequencies() {
		String[] source = {
				"CABD",
				"BCAC",
				"DBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertFalse(result, "Must be false");
	}
}
