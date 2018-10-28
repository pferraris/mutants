package ar.com.pabloferraris.mutants.persistence.rabbit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;
import ar.com.pabloferraris.mutants.persistence.rabbit.RabbitDetectionResultConsumer.InternalConsumer;

public class RabbitDetectionResultConsumerTests {

	private static final Charset utf8 = Charset.forName("UTF-8");
	private static final Gson gson = new Gson();

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

	private static RabbitDetectionResultConsumer createConsumer() {
		return new RabbitDetectionResultConsumer(factory, "ConsumerQueue");
	}

	@Test(expected = URISyntaxException.class)
	public void instanceClassWithInvalidUrl() throws Exception {
		new RabbitDetectionResultConsumer("It's not an URL", "ConsumerQueue");
	}

	@Test
	public void startAndStop() throws Exception {
		RabbitDetectionResultConsumer consumer = createConsumer();

		consumer.start(result -> true);
		assertTrue(consumer.isActive());
		consumer.stop();
		assertFalse(consumer.isActive());
	}

	@Test
	public void startTwice() throws Exception {
		RabbitDetectionResultConsumer consumer = createConsumer();

		consumer.start(result -> true);
		boolean startedAgain = consumer.start(result -> true);
		assertFalse(startedAgain);
		consumer.stop();
	}

	@Test
	public void internalConsumer() throws Exception {
		RabbitDetectionResultConsumer consumer = createConsumer();
		Mockito.doNothing().when(channel).basicAck(0, false);

		DetectionResult expected = new DetectionResult();
		expected.setDna(new String[] { "ACT", "TGC", "GTA" });
		expected.setMutant(false);

		byte[] body = gson.toJson(expected).getBytes(utf8);

		Predicate<DetectionResult> predicate = result -> {
			assertEquals(expected.isMutant(), result.isMutant());
			assertArrayEquals(expected.getDna(), result.getDna());
			return true;
		};
		consumer.start(predicate);

		InternalConsumer internal = consumer.getInternalConsumer();
		internal.handleDelivery("consumerTag", new Envelope(0, false, "", ""), null, body);
	}

	@Test
	public void discardMalformedMessage() throws Exception {
		RabbitDetectionResultConsumer consumer = createConsumer();
		Mockito.doNothing().when(channel).basicReject(0, false);

		byte[] body = "It's not a JSON representation".getBytes(utf8);

		consumer.start(result -> true);

		InternalConsumer internal = consumer.getInternalConsumer();
		internal.handleDelivery("consumerTag", new Envelope(0, false, "", ""), null, body);
	}
}
