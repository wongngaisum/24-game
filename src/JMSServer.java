import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;


public class JMSServer {
    private Server server;
    private JMSHelper jmsHelper;
    private MessageConsumer queueReader;
    private MessageProducer topicSender;

    public JMSServer(Server server) throws JMSException, NamingException {
        this.server = server;
        jmsHelper = new JMSHelper();
        initialize();
    }

    public void initialize() throws JMSException {
        queueReader = jmsHelper.createQueueReader();
        topicSender = jmsHelper.createTopicSender();
        jmsHelper.createSession();
    }

    public void receiveMessage() throws JMSException {
        Message msg = queueReader.receive();
        Object objMsg = ((ObjectMessage) msg).getObject();
        if (objMsg instanceof JMS_JoinGame) {
            server.addUserToList((JMS_JoinGame) objMsg);
        }
    }

    public void broadcastMessage(MessageProducer topicSender, Message message) throws JMSException {
    	Object objMsg = ((ObjectMessage) message).getObject();
    	if (objMsg instanceof JMS_StartGame) {
			JMS_StartGame msg = (JMS_StartGame) objMsg;
			for (int i = 0; i < msg.getUsers().size(); i++) {
				message.setStringProperty("receiver" + i, msg.getUsers().get(i).getUsername());
			}
			topicSender.send(message);
			System.out.println("Broadcasted message to players");
		} else if (objMsg instanceof JMS_EndGame) {
            JMS_EndGame msg = (JMS_EndGame) objMsg;
            for (int i = 0; i < msg.getRoom().getPlayers().size(); i++) {
                message.setStringProperty("receiver" + i, msg.getRoom().getPlayers().get(i).getUsername());
            }
            topicSender.send(message);
            System.out.println("Broadcasted message to players");
        }
    }

    public JMSHelper getJmsHelper() {
        return jmsHelper;
    }
    
    public MessageConsumer getQueueReader() {
        return queueReader;
    }

    public MessageProducer getTopicSender() {
        return topicSender;
    }

}
