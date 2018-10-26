package ar.com.pabloferraris.mutants.persistence.domain;

public class Stats {
	private int mutants;
	private int humans;
	
	public Stats(int mutants, int humans) {
		this.mutants = mutants;
		this.humans = humans;
	}
	
	/**
	 * @return the mutants
	 */
	public int getMutants() {
		return mutants;
	}
	
	/**
	 * @return the humans
	 */
	public int getHumans() {
		return humans;
	}
	
	/**
	 * @return the ratio between mutants and humans (mutants / humans)
	 */
	public float getRatio() {
		if (humans == 0) {
			return 0;
		}
		return mutants / humans;
	}
}
