package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;
import ar.com.pabloferraris.mutants.detection.DetectionStrategyBuilder;

public class NitrogenousBasesDetectionStrategyBuilder implements DetectionStrategyBuilder {

	private Class<? extends NitrogenousBasesDetectionStrategy> strategyType;
	private int sequenceSize;
	private int sequenceCount;

	public NitrogenousBasesDetectionStrategyBuilder() {
		strategyType = MatrixScanNitrogenousBasesDetectionStrategy.class;
		sequenceCount = 2;
		sequenceSize = 4;
	}

	public NitrogenousBasesDetectionStrategyBuilder withStrategy(
			Class<? extends NitrogenousBasesDetectionStrategy> value) {
		strategyType = value;
		return this;
	}

	public NitrogenousBasesDetectionStrategyBuilder withSize(int value) {
		sequenceSize = value;
		return this;
	}

	public NitrogenousBasesDetectionStrategyBuilder withCount(int value) {
		sequenceCount = value;
		return this;
	}

	@Override
	public DetectionStrategy build() {
		try {
			NitrogenousBasesDetectionStrategy strategy = strategyType.getConstructor().newInstance();
			strategy.setSequenceCount(sequenceCount);
			strategy.setSequenceSize(sequenceSize);
			return strategy;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
