package cz.net21.ttulka.rmimeetsjms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.jms.JMSException;

import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;

/**
 * Proxy for the services, packing a request for the service to a message and waiting for a reply 
 * 
 * @author ttulka
 *
 */
class ServiceProxy implements InvocationHandler {
	
	private final RemoteServiceConsumer consumer;
	
	public ServiceProxy(final RemoteServiceConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
		CallRequest request = new CallRequest();
		
		Class<?> returnType = method.getReturnType();
		request.setReturnType(returnType.equals(Void.TYPE) ? Void.class : returnType);
		
		request.setMethodName(method.getName());
		
		if (parameters != null) {
			for (Object o : parameters) {
				Class clazz = o.getClass();
				request.addParameter(new CallRequest.CallParameter(clazz, o));
			}
		}
		
		try {
			byte[] encodedRequest = ObjectSerializer.serialize(request);
			
			if (request.getReturnType().equals(Void.class)) {
				consumer.request(encodedRequest);
				return Void.TYPE;
			} 
			else {
				byte[] encodedReply = consumer.requestReply(encodedRequest);
				
				CallReply<Integer> reply = (CallReply<Integer>)ObjectSerializer.deserialize(encodedReply);
				
				return reply.getResponse();
			}
		}
		catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
