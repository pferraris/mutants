package ar.com.pabloferraris.mutants.worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ar.com.pabloferraris.mutants.persistence.DetectionResultConsumer;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class Daemon {

	private Map<DetectionResultConsumer, PersistenceStrategy> workers;

	public Daemon() {
		workers = new HashMap<DetectionResultConsumer, PersistenceStrategy>();
	}

	public void addWorker(DetectionResultConsumer consumer, PersistenceStrategy persistence) {
		workers.put(consumer, persistence);
	}

	public void run() {
		RetryPolicy retryPolicy = new RetryPolicy()
				.retryOn(RuntimeException.class)
				.withDelay(2, TimeUnit.SECONDS)
				.withMaxDuration(5, TimeUnit.MINUTES);
		Failsafe.with(retryPolicy).run(() -> startWorkers());
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			for (DetectionResultConsumer consumer : workers.keySet()) {
				consumer.stop();
				try {
					workers.get(consumer).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
	}

	private void startWorkers() throws RuntimeException {
		for (DetectionResultConsumer consumer : workers.keySet()) {
			boolean started = consumer.start(result -> {
				try {
					workers.get(consumer).add(result);
					return true;
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
					return false;
				}
			});
			if (!started) {
				throw new RuntimeException("Consumer " + consumer.toString() + " cannot be started");
			}
		}
	}
	
	public boolean isActive() {
		if (workers.isEmpty())
			return false;
		return workers.keySet().stream().anyMatch(x -> x.isActive());
	}

	public boolean hasWorkers() {
		return workers.size() > 0;
	}
	
	public static void main(String[] args) throws Exception {
		Daemon daemon = new DaemonFactory().create();
		daemon.run();
		while (daemon.isActive()) {
			Thread.sleep(100);
		}
	}
}
