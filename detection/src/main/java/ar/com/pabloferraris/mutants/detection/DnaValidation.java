package ar.com.pabloferraris.mutants.detection;

import java.util.function.Predicate;

public class DnaValidation {

	private Predicate<String[]> condition;
	private DnaException exception;
	
	public DnaValidation(Predicate<String[]> condition, DnaException exception) {
		this.condition = condition;
		this.exception = exception;
	}
	
	public void eval(String[] dna) throws DnaException {
		if (!condition.test(dna)) {
			throw exception;
		}
	}
}
