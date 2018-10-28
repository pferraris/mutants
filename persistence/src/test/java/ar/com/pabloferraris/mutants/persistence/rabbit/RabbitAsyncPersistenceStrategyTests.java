package ar.com.pabloferraris.mutants.persistence.rabbit;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class RabbitAsyncPersistenceStrategyTests {

	private static ConnectionFactory factory;
	private static Connection conn;
	private static Channel channel;

	@BeforeClass
	public static void initialize() throws Exception {
		factory = Mockito.mock(ConnectionFactory.class);
		conn = Mockito.mock(Connection.class);
		channel = Mockito.mock(Channel.class);
		when(factory.newConnection()).thenReturn(conn);
		when(conn.createChannel()).thenReturn(channel);
	}

	private static RabbitAsyncPersistenceStrategy createPersistence() {
		return new RabbitAsyncPersistenceStrategy(factory);
	}

	@Test(expected = URISyntaxException.class)
	public void instanceClassWithInvalidUrl() throws Exception {
		RabbitAsyncPersistenceStrategy persistence = new RabbitAsyncPersistenceStrategy("It's not an URL");
		persistence.close();
	}
	
	@Test
	public void addResult() throws Exception {
		RabbitAsyncPersistenceStrategy persistence = createPersistence();
		
		DetectionResult expected = new DetectionResult();
		expected.setMutant(true);
	
		doNothing().when(channel).basicPublish("DetectionResults", String.valueOf(expected.isMutant()), null, new byte[0]);
		
		persistence.add(expected);
		persistence.close();
	}
}
