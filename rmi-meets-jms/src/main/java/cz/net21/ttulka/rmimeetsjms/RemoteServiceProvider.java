package cz.net21.ttulka.rmimeetsjms;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;

/**
 * Class responsible for invoking and replying a remote method via JMS
 * 
 * @author ttulka
 *
 */
public class RemoteServiceProvider implements AutoCloseable {
	
	private final ServiceAdapter serviceAdapter;
	
	private final Connection connection;
	private final Session session;
	
	private final MessageConsumer consumer;
	
	/**
	 * Constructor initializes the JMS connection
	 * 
	 * @param connectionFactory
	 * @param queue
	 * @throws JMSException
	 */
	public RemoteServiceProvider(final ConnectionFactory connectionFactory, final Destination queue,
			final Object serviceImpl) throws JMSException {
		serviceAdapter = new ServiceAdapter(serviceImpl);
		
		connection = connectionFactory.createConnection();
		
		boolean transacted = false;
		session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
				
		consumer = session.createConsumer(queue);
		MessageListener listener = new Replier();
		consumer.setMessageListener(listener);
				
		connection.start();
	}

	/**
	 * Closes all the JMS resources
	 */
	public void close() throws Exception {
		if (consumer != null) {
			consumer.close();
		}
		if (session != null) {
			session.close();
		}
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Implementation of the message listener
	 */
	private class Replier implements MessageListener {
		
		public void onMessage(Message message) {
			try {
				if (message instanceof BytesMessage) {
					final BytesMessage bytesMessage = (BytesMessage)message;
					
					byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
				    bytesMessage.readBytes(bytes);
					
					final CallRequest request = (CallRequest)ObjectSerializer.deserialize(bytes);
					
					if (message.getJMSReplyTo() != null) {
						
						final CallReply<?> reply = serviceAdapter.callService(request);
						final byte[] replyEncoded = ObjectSerializer.serialize(reply);
						
						Destination replyDestination = message.getJMSReplyTo();
						MessageProducer replyProducer = session.createProducer(replyDestination);

						BytesMessage replyMessage = session.createBytesMessage();
						replyMessage.writeBytes(replyEncoded);
						replyMessage.setJMSCorrelationID(message.getJMSMessageID());
						replyProducer.send(replyMessage);						
					} 
					else {
						serviceAdapter.callService(request);
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
