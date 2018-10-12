package ar.com.pabloferraris.mutants.detection;

/**
 * Represents a builder that can make instances of detection strategies
 * @author Pablo Ferraris
 */
public interface DetectionStrategyBuilder {
	
	/**
	 * @return DetectionStrategy built instance 
	 */
	DetectionStrategy build();
}
