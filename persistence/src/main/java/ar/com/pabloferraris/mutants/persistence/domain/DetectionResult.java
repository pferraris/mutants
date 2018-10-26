package ar.com.pabloferraris.mutants.persistence.domain;

public class DetectionResult {
	private String[] dna;
	private boolean mutant;
	
	/**
	 * @return the dna
	 */
	public String[] getDna() {
		return dna;
	}
	
	/**
	 * @param dna the dna to set
	 */
	public void setDna(String[] dna) {
		this.dna = dna;
	}
	
	/**
	 * @return the mutant
	 */
	public boolean isMutant() {
		return mutant;
	}
	
	/**
	 * @param mutant the mutant to set
	 */
	public void setMutant(boolean mutant) {
		this.mutant = mutant;
	}
}
