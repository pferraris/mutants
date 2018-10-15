package ar.com.pabloferraris.mutants.rest.domain;

/**
 * Representa un espécimen humano o mutante
 * @author pablo
 */
public class Specimen {
    private String[] dna;

    /**
     * Gets a matrix with nitrogenous bases of dna
     * @return a matrix with nitrogenous bases of dna
     */
    public String[] getDna() {
        return dna;
    }

    /**
     * Sets a matrix with nitrogenous bases of dna
     * @param dna is a matrix with nitrogenous bases information
     */
    public void setDna(String[] dna) {
        this.dna = dna;
    }
}
