package ar.com.pabloferraris.mutants.persistence;

import java.util.function.Predicate;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public interface DetectionResultConsumer {
	boolean start(Predicate<DetectionResult> predicate);
	void stop();
	
	boolean isActive();
}
