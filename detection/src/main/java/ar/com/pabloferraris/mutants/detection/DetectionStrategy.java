package ar.com.pabloferraris.mutants.detection;

public interface DetectionStrategy {

	/**
	 * Implements the mutant detection algorithm over DNA
	 * 
	 * @param dna sample to analyze
	 * @return boolean true for a mutant, otherwise false
	 */
	boolean detect(String[] dna);
}
