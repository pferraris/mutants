package ar.com.pabloferraris.mutants.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import ar.com.pabloferraris.mutants.detection.DetectionStrategy;
import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.nitrogenousBases.MatrixScanNitrogenousBasesDetectionStrategy;
import ar.com.pabloferraris.mutants.detection.nitrogenousBases.NitrogenousBasesDetectionStrategyBuilder;

public class DetectorBinder extends AbstractBinder {

	@Override
	protected void configure() {
		DetectionStrategy strategy = new NitrogenousBasesDetectionStrategyBuilder()
				.withStrategy(MatrixScanNitrogenousBasesDetectionStrategy.class)
				.withCount(2)
				.withSize(4)
				.build();
		Detector detector = new Detector();
		detector.setStrategy(strategy);
		
		bind(detector).to(Detector.class);
	}

}
