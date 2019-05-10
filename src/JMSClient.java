import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

public class JMSClient implements MessageListener {
	private JMSHelper jmsHelper;
	private Client client;

	private MessageProducer queueSender;
	private MessageConsumer topicReceiver;

	public JMSClient(String host, Client client) throws JMSException, NamingException {
		jmsHelper = new JMSHelper(host);
		this.client = client;
	}

	public void initialize(User user) throws JMSException {
		queueSender = jmsHelper.createQueueSender();
		topicReceiver = jmsHelper.createTopicReader(user.getUsername());
		topicReceiver.setMessageListener(this);
	}

	public void sendMessage(Message message) {
		if (message != null) {
			try {
				queueSender.send(message);
			} catch (JMSException e) {
				System.err.println("Failed to send message");
			}
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("Received message");
			Object objMsg = ((ObjectMessage) message).getObject();
			if (objMsg instanceof JMS_StartGame) {
				System.out.println("Received start game request");
				JMS_StartGame msg = (JMS_StartGame) objMsg;
				client.startGame(msg);
			} else if (objMsg instanceof JMS_EndGame) {
				System.out.println("Received end game request");
				JMS_EndGame msg = (JMS_EndGame) objMsg;
				client.endGame(msg);
			}
		} catch (Exception e) {
			System.err.println("Failed to receive message");
		}
	}

	public JMSHelper getJmsHelper() {
		return jmsHelper;
	}

	public MessageProducer getQueueSender() {
		return queueSender;
	}

	public MessageConsumer getTopicReceiver() {
		return topicReceiver;
	}
}
