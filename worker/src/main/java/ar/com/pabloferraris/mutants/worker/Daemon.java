package ar.com.pabloferraris.mutants.worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import ar.com.pabloferraris.mutants.persistence.DetectionResultConsumer;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;

public class Daemon {

	private Map<DetectionResultConsumer, PersistenceStrategy> workers;

	public Daemon() {
		workers = new HashMap<DetectionResultConsumer, PersistenceStrategy>();
	}

	public void addWorker(DetectionResultConsumer consumer, PersistenceStrategy persistence) {
		workers.put(consumer, persistence);
	}

	public void run() throws InterruptedException {
		for (DetectionResultConsumer consumer : workers.keySet()) {
			consumer.start(result -> {
				try {
					workers.get(consumer).add(result);
					return true;
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
					return false;
				}
			});
		}

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

	public boolean isActive() {
		if (workers.isEmpty())
			return false;
		return workers.keySet().stream().allMatch(x -> x.isActive());
	}

	public boolean hasWorkers() {
		return workers.size() > 0;
	}
	
	public static void main(String[] args) throws Exception {
		Daemon daemon = DaemonFactory.create();
		daemon.run();
		while (daemon.isActive()) {
			Thread.sleep(100);
		}
	}
}
