package ar.com.pabloferraris.mutants.persistence.rabbit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class RabbitDetectionResultsConsumer {

	private static final String exchangeName = "DetectionResults";
	private static final Charset utf8 = Charset.forName("UTF-8");

	private Connection conn;
	private Channel channel;
	private String queueName;
	private String consumerTag;
	private Gson gson;

	public RabbitDetectionResultsConsumer(String connectionString, String queueName) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(connectionString);
		factory.setAutomaticRecoveryEnabled(true);
		this.queueName = queueName;
		conn = factory.newConnection();
		channel = conn.createChannel();
		initialize();
	}

	private void initialize() {
		try {
			channel.queueDeclare(queueName, true, false, false, null);
			channel.queueBind(queueName, exchangeName, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Start(Predicate<DetectionResult> predicate) {
		if (!isActive()) {
			try {
				consumerTag = UUID.randomUUID().toString();
				gson = new Gson();
				channel.basicConsume(queueName, false, consumerTag, new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
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
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Stop() {
		if (isActive()) {
			try {
				channel.basicCancel(consumerTag);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				consumerTag = null;
			}
		}
	}

	public boolean isActive() {
		return consumerTag != null;
	}
}
