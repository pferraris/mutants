package ar.com.pabloferraris.mutants.detection;

public class DnaException extends Exception {
	private static final long serialVersionUID = -8629749391921167060L;

	public static final int EMPTY_MATRIX = 1001;
	public static final int EMPTY_LINE = 1002;
	public static final int DISTINCT_SIZE = 1003;
	
	public static DnaException emptyMatrix() {
		return new DnaException("DNA matrix must have lines with nitrogenous bases", EMPTY_MATRIX);
	}

	public static DnaException emptyLine() {
		return new DnaException("All lines in DNA must have nitrogenous bases", EMPTY_LINE);
	}

	public static DnaException distinctSize() {
		return new DnaException("All lines in DNA must have same quantity of nitrogenous bases", DISTINCT_SIZE);
	}

	private int causeCode;

	/**
	 * @param message the detail message
	 * @param causeCode the cause code that origins this exception
	 */
	public DnaException(String message, int causeCode)
	{
		super(message);
		this.causeCode = causeCode; 
	}
	
	/**
	 * @return the cause code that origins this exception
	 */
	public int getCauseCode() {
		return causeCode;
	}
}
