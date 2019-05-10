import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSHelper {
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 3700;

	private static final String JMS_CONNECTION_FACTORY = "jms/JPoker24GameConnectionFactory";
	private static final String JMS_QUEUE = "jms/JPoker24GameQueue";
	private static final String JMS_TOPIC = "jms/JPoker24GameTopic";

	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Connection connection;

	private Session session;
	private Queue queue;
	private Topic topic;

	public JMSHelper() throws NamingException, JMSException {
		this(DEFAULT_HOST);
	}

	public JMSHelper(String host) throws NamingException, JMSException {
		int port = DEFAULT_PORT;
		Properties props = new Properties();
		props.setProperty("org.omg.CORBA.ORBInitialHost", host);
		props.setProperty("org.omg.CORBA.ORBInitialPort", "" + port);
		try {
			jndiContext = new InitialContext(props);
			connectionFactory = (ConnectionFactory) jndiContext.lookup(JMS_CONNECTION_FACTORY);
			queue = (Queue) jndiContext.lookup(JMS_QUEUE);
			topic = (Topic) jndiContext.lookup(JMS_TOPIC);
		} catch (NamingException e) {
			System.err.println("JNDI failed: " + e);
			throw e;
		}
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException e) {
			System.err.println("Failed to create connection to JMS provider: " + e);
			throw e;
		}
	}

	public Session createSession() throws JMSException {
		if (session != null) {
			return session;
		} else {
			try {
				return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				System.err.println("Failed creating session: " + e);
				throw e;
			}
		}
	}

	public ObjectMessage createMessage(Serializable obj) throws JMSException {
		try {
			return createSession().createObjectMessage(obj);
		} catch (JMSException e) {
			System.err.println("Error preparing message: " + e);
			throw e;
		}
	}

	public MessageProducer createQueueSender() throws JMSException {
		try {
			return createSession().createProducer(queue);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}
	}

	public MessageConsumer createQueueReader() throws JMSException {
		try {
			return createSession().createConsumer(queue);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}
	}

	public MessageProducer createTopicSender() throws JMSException {
		try {
			return createSession().createProducer(topic);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}

	}

	public MessageConsumer createTopicReader(String name) throws JMSException {
		try {
			name = name.replace("'", "''");
			String selector = "receiver0 = '" + name + "' OR " + "receiver1 = '" + name + "' OR " + "receiver2 = '"
					+ name + "' OR " + "receiver3 = '" + name + "'";
			return createSession().createConsumer(topic, selector);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}
	}
}
