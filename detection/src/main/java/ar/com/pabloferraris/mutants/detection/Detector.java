package ar.com.pabloferraris.mutants.detection;

import ar.com.pabloferraris.mutants.detection.nitrogenousBases.NitrogenousBasesDetectionStrategyBuilder;

/**
 * Singleton used to check whether a DNA sample belongs to a mutant or not
 *
 * @author Pablo Ferraris
 */
public class Detector {

	private DetectionStrategy strategy;

	/**
	 * @return the strategy use for detection
	 */
	public DetectionStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(DetectionStrategy strategy) {
		this.strategy = strategy;
	}

	public Detector() {
		strategy = new NitrogenousBasesDetectionStrategyBuilder()
				.withCount(2)
				.withSize(4)
				.build();
	}

	/**
	 * Analyzes a DNA sample to verify whether or not it is a mutant
	 *
	 * @param dna sample to analyze
	 * @return boolean true for a mutant, otherwise false
	 */
	public boolean isMutant(String[] dna) throws DnaException {
		DnaValidator.getInstance().ensureValidDna(dna);
		return strategy.detect(dna);
	}
}
