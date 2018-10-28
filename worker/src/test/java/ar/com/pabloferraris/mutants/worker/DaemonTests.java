package ar.com.pabloferraris.mutants.worker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow; 

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.junit.Test;
import org.mockito.Mockito;

import ar.com.pabloferraris.mutants.persistence.DetectionResultConsumer;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class DaemonTests 
{
	@Test
	public void createInstance() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		Daemon daemon = DaemonFactory.create();
		assertFalse(daemon.isActive());
		assertTrue(daemon.hasWorkers());
	}
	
    @Test
    public void isNotActiveWithEmpty() {
        Daemon daemon = new Daemon();
        assertFalse(daemon.isActive());
    }

    @Test
    public void isNotActiveWithOneWorker() {
        Daemon daemon = new Daemon();
        daemon.addWorker(new MockedDetectionResultConsumer(), Mockito.mock(PersistenceStrategy.class));
        assertFalse(daemon.isActive());
    }
    
    @Test
    public void isActive() throws InterruptedException {
        Daemon daemon = new Daemon();
        daemon.addWorker(new MockedDetectionResultConsumer(), Mockito.mock(PersistenceStrategy.class));
        daemon.run();
        assertTrue(daemon.isActive());
    }

    @Test
    public void processMessage() throws InterruptedException {
        Daemon daemon = new Daemon();
        MockedDetectionResultConsumer consumer = new MockedDetectionResultConsumer();
        PersistenceStrategy persistence = Mockito.mock(PersistenceStrategy.class);
        daemon.addWorker(consumer, persistence);
        DetectionResult result = new DetectionResult();
        daemon.run();
        consumer.injectResult(result);
        assertTrue(daemon.isActive());
    }

    @Test
    public void persistenceException() throws InterruptedException, IOException, TimeoutException {
        Daemon daemon = new Daemon();
        MockedDetectionResultConsumer consumer = new MockedDetectionResultConsumer();
        PersistenceStrategy persistence = Mockito.mock(PersistenceStrategy.class);
        daemon.addWorker(consumer, persistence);
        DetectionResult result = new DetectionResult();
        doThrow(IOException.class).when(persistence).add(result);
        daemon.run();
        consumer.injectResult(result);
        assertTrue(daemon.isActive());
    }
    
    private class MockedDetectionResultConsumer implements DetectionResultConsumer {
    	private boolean active;
    	private Predicate<DetectionResult> predicate;
    	
    	public void injectResult(DetectionResult result) {
    		if (active) {
    			predicate.test(result);
    		}
    	}
    	
		@Override
		public boolean start(Predicate<DetectionResult> predicate) {
			active = true;
			this.predicate = predicate;
			return true;
		}

		@Override
		public void stop() {
			active = false;
		}

		@Override
		public boolean isActive() {
			return active;
		}
    }
}
