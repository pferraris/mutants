package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;
import ar.com.pabloferraris.mutants.detection.nitrogenousBases.UnravelNitrogenousBasesDetectionStrategy;

class UnravelNitrogenousBasesDetectionStrategyTests {

	static DetectionStrategy strategy;
	static Method unravel;

	@BeforeAll
	static void initialize() {
		strategy = new NitrogenousBasesDetectionStrategyBuilder()
				.withStrategy(UnravelNitrogenousBasesDetectionStrategy.class)
				.withCount(2)
				.withSize(3)
				.build();
		try {
			unravel = UnravelNitrogenousBasesDetectionStrategy.class.getDeclaredMethod("unravel", String[].class, int.class);
			unravel.setAccessible(true);
		} catch (Exception ex) {
			unravel = null;
		}
	}

	@SuppressWarnings("unchecked")
	static List<String> unravelProxy(String[] source, int minSize) {
		try {
			return (List<String>) unravel.invoke(strategy, source, minSize);
		} catch (Exception ex) {
			return null;
		}
	}

	@Test
	@DisplayName("Unravel DNA matrix")
	void unravel() {
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
		String[] result = unravelProxy(dna, 1).toArray(new String[0]);

		assertArrayEquals(expected, result);
	}

	@Test
	@DisplayName("Unravel matrix with minSize = 2")
	void unravelWithMinSize() {
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
		String[] result = unravelProxy(dna, minSize).toArray(new String[0]);

		assertArrayEquals(expected, result);
	}

	@Test
	@DisplayName("Find at least 2 sequences of at least 3 equals characters in a string list")
	void findSequencies() {
		String[] source = {
				"CABD",
				"BCAC",
				"BBCD",
				"BBCC" };
		boolean result = strategy.detect(source);
		assertTrue(result, "Must be true");
	}
	
	@Test
	@DisplayName("Cannot find any sequence of at least 3 equals characters in a string list")
	void cannotFindSequencies() {
		String[] source = {
				"CABD",
				"BAAC",
				"DBCD",
				"BBCC" };
		boolean result = strategy.detect(source);
		assertFalse(result, "Must be false");
	}
}
