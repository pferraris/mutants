package ar.com.pabloferraris.mutants.detection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.DnaException;

class DetectorTests {

	static Detector detector;

	@BeforeAll
	static void initialize() {
		detector = new Detector();
	}

	@Test
	@DisplayName("Detects a mutant")
	void detectMutant() throws DnaException {
		String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		boolean expected = true;
		boolean result = detector.isMutant(dna);
		assertEquals(expected, result, "Must detect a mutant based in his DNA");
	}

	@Test
	@DisplayName("Detects a human")
	void detectHuman() throws DnaException {
		String[] dna = { "ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG" };
		boolean expected = false;
		boolean result = detector.isMutant(dna);
		assertEquals(expected, result, "Must detect a human based in his DNA");
	}

	@Test
	@DisplayName("Null DNA")
	void nullDna() {
		String[] dna = null;
		DnaException ex = assertThrows(DnaException.class, () -> detector.isMutant(dna));
		assertEquals(1000, ex.getCauseCode(), "Cause code must be 1000");
	}

	@Test
	@DisplayName("Empty DNA")
	void emptyDna() {
		String[] dna = {};
		DnaException ex = assertThrows(DnaException.class, () -> detector.isMutant(dna));
		assertEquals(1001, ex.getCauseCode(), "Cause code must be 1001");
	}

	@Test
	@DisplayName("Empty first line in DNA")
	void emptyFirstLine() {
		String[] dna = { "", "" };
		DnaException ex = assertThrows(DnaException.class, () -> detector.isMutant(dna));
		assertEquals(1002, ex.getCauseCode(), "Cause code must be 1002");
	}

	@Test
	@DisplayName("Diferent size in lines")
	void diferentSizeInLines() {
		String[] dna = { "ATGCGA", "CA", "TTATGT", "AGAAGG", "CCCCTA", "TC" };
		DnaException ex = assertThrows(DnaException.class, () -> detector.isMutant(dna));
		assertEquals(1003, ex.getCauseCode(), "Cause code must be 1003");
	}
}
