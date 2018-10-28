package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import ar.com.pabloferraris.mutants.detection.nitrogenousBases.UnravelNitrogenousBasesDetectionStrategy;

public class UnravelNitrogenousBasesDetectionStrategyTests {

	static UnravelNitrogenousBasesDetectionStrategy strategy;

	@BeforeClass
	public static void initialize() {
		strategy = new UnravelNitrogenousBasesDetectionStrategy();
		strategy.setSequenceCount(2);
		strategy.setSequenceSize(3);
	}

	@Test
	public void unravelDnaMatrix() {
		String[] dna = {
				"ABC",
				"DEF",
				"GHI"
		};
		String[] expected = {
				"ABC", "DEF", "GHI",
				"ADG", "BEH", "CFI",
				"AEI", "A", "DH", "DB", "G", "GEC",
				"BF", "HF",	"C", "I"
		};
		String[] result = strategy.unravel(dna, 1).toArray(new String[0]);

		assertArrayEquals(expected, result);
	}

	@Test
	public void unravelDnaMatrixWithMinSize() {
		String[] dna = {
				"ABC",
				"DEF",
				"GHI"
		};
		String[] expected = {
				"ABC", "DEF", "GHI",
				"ADG", "BEH", "CFI",
				"AEI", "DH", "DB", "GEC",
				"BF", "HF"
		};
		int minSize = 2;
		String[] result = strategy.unravel(dna, minSize).toArray(new String[0]);

		assertArrayEquals(expected, result);
	}

	@Test
	public void findTwoSequenciesOfThreeCharacters() {
		String[] source = {
				"CABD",
				"BCAC",
				"BBCD",
				"BBCC" };
		boolean result = strategy.detect(source);
		assertTrue("Result must be true", result);
	}
	
	@Test
	public void cannotFindAnySequence() {
		String[] source = {
				"CABD",
				"BAAC",
				"DBCD",
				"BBCC" };
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
