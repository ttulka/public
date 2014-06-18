package cz.net21.ttulka.rmimeetsjms;

import java.lang.reflect.Proxy;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;

/**
 * Class responsible for calling a remote method via JMS
 * 
 * @author ttulka
 *
 */
public class RemoteServiceConsumer implements AutoCloseable {
		
	private final QueueConnection connection;
	private final QueueSession session;
	
	private final MessageProducer producer;	
	private final QueueRequestor requestor;
	
	private final Object serviceProxy;
	
	/**
	 * Constructor initializes the JMS connection
	 * 
	 * @param connectionFactory JMS connection factory
	 * @param queue	Destination queue
	 */
	public RemoteServiceConsumer(final QueueConnectionFactory connectionFactory, final Queue queue, 
			final Class<?> serviceClazz) throws JMSException {	
		
		connection = connectionFactory.createQueueConnection();
		
		boolean transacted = false;
		session = connection.createQueueSession(transacted, Session.AUTO_ACKNOWLEDGE);
				
		producer = session.createProducer(queue);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		requestor = new QueueRequestor(session, queue);
				
		connection.start();
		
		serviceProxy = Proxy.newProxyInstance(
				serviceClazz.getClassLoader(), 
				new Class[] { serviceClazz },
				new ServiceProxy(this)
		);
	}

	/**
	 * Closes all the JMS resources
	 */
	public void close() throws Exception {
		if (producer != null) {
			producer.close();
		}
		if (requestor != null) {
			requestor.close();
		}
		if (session != null) {
			session.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	/**
	 * Getter for the remove service proxy
	 * 
	 * @return remove service proxy
	 */
	public Object getService() {
		return serviceProxy;
	}
	
	/**
	 * Send an asynchronous request via JMS
	 *  
	 * @param encodedMsg encoded object to send
	 * @throws JMSException
	 */
	void request(byte[] encodedMsg) throws JMSException {
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes(encodedMsg);
		producer.send(msg);
	}
	
	/**
	 * Send an synchronous request and wait for a reply via JMS
	 * 
	 * @param encodedMsg encoded object to send
	 * @return response object
	 * @throws JMSException
	 */
	byte[] requestReply(byte[] encodedMsg) throws JMSException {		
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes(encodedMsg);
		
		Message reply = requestor.request(msg);
		
		if (reply instanceof BytesMessage) {
			final BytesMessage bytesMessage = (BytesMessage)reply;
			
			byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
		    bytesMessage.readBytes(bytes);
		    
			return bytes;
		}
		else {
			throw new IllegalArgumentException("Wrong type of the response message was received.");
		}		
	}
}
