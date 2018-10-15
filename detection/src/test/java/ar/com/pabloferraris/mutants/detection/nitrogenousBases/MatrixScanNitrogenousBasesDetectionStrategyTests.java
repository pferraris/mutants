package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;
import ar.com.pabloferraris.mutants.detection.nitrogenousBases.MatrixScanNitrogenousBasesDetectionStrategy;

public class MatrixScanNitrogenousBasesDetectionStrategyTests {

	static DetectionStrategy strategy;

	@BeforeClass
	public static void initialize() {
		strategy = new NitrogenousBasesDetectionStrategyBuilder()
				.withStrategy(MatrixScanNitrogenousBasesDetectionStrategy.class)
				.withCount(2)
				.withSize(3)
				.build();
	}

	@Test
	public void findTwoSequenciesOfThreeCharacters() {
		String[] source = {
				"CABD",
				"BCAC",
				"BBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertTrue("Result must be true", result);
	}

	@Test
	public void cannotFindAnySequence() {
		String[] source = {
				"CABD",
				"BAAC",
				"DBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertFalse("Result must be false", result);
	}

	@Test
	public void findOnlyOneSequenceOfThreeCharacters() {
		String[] source = {
				"CABD",
				"BCAC",
				"DBCD",
				"BBCC"
		};
		boolean result = strategy.detect(source);
		assertFalse("Result must be false", result);
	}
}
