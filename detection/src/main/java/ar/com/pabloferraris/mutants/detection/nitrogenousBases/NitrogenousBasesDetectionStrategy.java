package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;

/**
 * Detection strategy based on the presence of nitrogenous bases sequences in DNA
 * @author Pablo Ferraris
 */
public abstract class NitrogenousBasesDetectionStrategy implements DetectionStrategy {

    private int sequenceSize;
    private int sequenceCount;

    /**
     * Get the size of sequence to find out
     * @return int The size of the sequence to find out
     */
    public int getSequenceSize() {
        return sequenceSize;
    }

    /**
     * Set the size of sequence to find out
     * @param value The size of the sequence to find out
     */
    public void setSequenceSize(int value) {
        sequenceSize = value;
    }

    /**
     * Get the count of sequences that must be found
     * @return int The count of sequences that must be found
     */
    public int getSequenceCount() {
        return sequenceCount;
    }

    /**
     * Set the count of sequences that must be found
     * @param value The count of sequences that must be found
     */
    public void setSequenceCount(int value) {
        sequenceCount = value;
    }
    
    public abstract boolean detect(String[] dna);
}
