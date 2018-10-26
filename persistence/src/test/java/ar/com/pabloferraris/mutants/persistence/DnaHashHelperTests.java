package ar.com.pabloferraris.mutants.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DnaHashHelperTests {

	@Test
	public void hashDna() {
		String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		String expected = "bBJuz46gVXO0";
		String result = DnaHashHelper.getHash(dna);
		assertEquals(expected, result);
	}

}
