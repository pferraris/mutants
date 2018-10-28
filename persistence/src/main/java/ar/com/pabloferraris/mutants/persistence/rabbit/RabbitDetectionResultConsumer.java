package ar.com.pabloferraris.mutants.persistence.rabbit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ar.com.pabloferraris.mutants.persistence.DetectionResultConsumer;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class RabbitDetectionResultConsumer implements DetectionResultConsumer {

	private static final Charset utf8 = Charset.forName("UTF-8");
	private static final Gson gson = new Gson();
	private static final String exchangeName = "DetectionResults";

	private ConnectionFactory factory;
	private Connection conn;
	private Channel channel;
	private InternalConsumer internalConsumer;
	private String queueName;
	private String consumerTag;

	public RabbitDetectionResultConsumer(String connectionString, String queueName)
			throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		factory = new ConnectionFactory();
		factory.setUri(connectionString);
		factory.setAutomaticRecoveryEnabled(true);
		this.queueName = queueName;
	}

	RabbitDetectionResultConsumer(ConnectionFactory factory, String queueName) {
		this.factory = factory;
		this.queueName = queueName;
	}

	private void ensureConnection() throws IOException, TimeoutException {
		conn = factory.newConnection();
		channel = conn.createChannel();
		try {
			channel.exchangeDeclare(exchangeName, "fanout", true);
			channel.queueDeclare(queueName, true, false, false, null);
			channel.queueBind(queueName, exchangeName, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean start(Predicate<DetectionResult> predicate) {
		if (!isActive()) {
			try {
				ensureConnection();
				consumerTag = UUID.randomUUID().toString();
				internalConsumer = new InternalConsumer(channel, predicate);
				channel.basicConsume(queueName, false, consumerTag, internalConsumer);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void stop() {
		if (isActive()) {
			try {
				channel.basicCancel(consumerTag);
				channel.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				consumerTag = null;
			}
		}
	}

	public boolean isActive() {
		return consumerTag != null;
	}

	InternalConsumer getInternalConsumer() {
		return internalConsumer;
	}

	class InternalConsumer extends DefaultConsumer {
		private Predicate<DetectionResult> predicate;

		public InternalConsumer(Channel channel, Predicate<DetectionResult> predicate) {
			super(channel);
			this.predicate = predicate;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			long deliveryTag = envelope.getDeliveryTag();
			String content = new String(body, utf8);
			try {
				DetectionResult result = gson.fromJson(content, DetectionResult.class);
				if (predicate.test(result)) {
					channel.basicAck(deliveryTag, false);
				} else {
					channel.basicReject(deliveryTag, true);
				}
			} catch (JsonSyntaxException e) {
				// This message is corrupt and must not be requeue
				channel.basicReject(deliveryTag, false);
				e.printStackTrace();
			}
		}
	}
}
