package ar.com.pabloferraris.mutants.detection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Ensures a valid DNA sample, sequentially checking a list of validations.
 * There are 3 built-in validations for:
 * - Empty matrix
 * - Empty line
 * - Distinct size in line
 * 
 * @author Pablo Ferraris
 */
public final class DnaValidator {

	private static final DnaValidator instance; 
	
	static {
		instance = new DnaValidator();
	}
	
	/**
	 * The Singleton instance reference
	 * 
	 * @return the instance of DnaValidation Singleton
	 */
	public static DnaValidator getInstance() {
		return instance;
	}
	
	private List<DnaValidation> validations;
	
	private DnaValidator() {
		validations = new ArrayList<DnaValidation>();
		
		// Built in validations

		putValidation(
				dna -> dna != null,
				DnaException.nullMatrix()
		);

		putValidation(
				dna -> dna.length > 0,
				DnaException.emptyMatrix()
		);
		
		putValidation(
				dna -> dna[0].length() > 0,
				DnaException.emptyLine()
		);

		putValidation(
				dna -> !Arrays.stream(dna).skip(1).anyMatch(x -> x.length() != dna[0].length()),
				DnaException.distinctSize()
		);

		Pattern validNB = Pattern.compile("^[ATCGatcg]*$");
		putValidation(
				dna -> Arrays.stream(dna).allMatch(x -> validNB.matcher(x).matches()),
				DnaException.invalidNitrogenousBase()
		);
	}

	/**
	 * Allows append a new validation to the Singleton
	 * 
	 * @param predicate is the condition that must be satisfied
	 * @param exception that will be thrown if the condition fails
	 */
	public void putValidation(Predicate<String[]> predicate, DnaException exception) {
		validations.add(new DnaValidation(predicate, exception));
	}
	
	/**
	 * Ensures that a DNA sample meets all validations 
	 * 
	 * @param dna sample to validate
	 * @throws DnaException if any condition fails
	 */
	public void ensureValidDna(String[] dna) throws DnaException {
		for (DnaValidation validation : validations) {
			validation.eval(dna);
		}
	}
}
