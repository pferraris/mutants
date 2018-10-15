package ar.com.pabloferraris.mutants.detection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.DnaException;

public class DetectorTests {

	static Detector detector;

	@BeforeClass
	public static void initialize() {
		detector = new Detector();
	}

	@Test
	public void detectsMutant() throws DnaException {
		String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		boolean result = detector.isMutant(dna);
		assertTrue("Result must be true", result);
	}

	@Test
	public void detectsHuman() throws DnaException {
		String[] dna = { "ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG" };
		boolean result = detector.isMutant(dna);
		assertFalse("Result must be false", result);
	}

	@Test
	public void nullDna() {
		String[] dna = null;
		try {
			detector.isMutant(dna);
		} catch (DnaException ex) {
			assertEquals("Cause code must be 1000", 1000, ex.getCauseCode());
		}
	}

	@Test
	public void emptyDna() {
		String[] dna = {};
		try {
			detector.isMutant(dna);
		} catch (DnaException ex) {
			assertEquals("Cause code must be 1001", 1001, ex.getCauseCode());
		}
	}

	@Test
	public void emptyFirstLine() {
		String[] dna = { "", "" };
		try {
			detector.isMutant(dna);
		} catch (DnaException ex) {
			assertEquals("Cause code must be 1002", 1002, ex.getCauseCode());
		}
	}

	@Test
	public void diferentSizeInLines() {
		String[] dna = { "ATGCGA", "CA", "TTATGT", "AGAAGG", "CCCCTA", "TC" };
		try {
			detector.isMutant(dna);
		} catch (DnaException ex) {
			assertEquals("Cause code must be 1003", 1003, ex.getCauseCode());
		}
	}
}
