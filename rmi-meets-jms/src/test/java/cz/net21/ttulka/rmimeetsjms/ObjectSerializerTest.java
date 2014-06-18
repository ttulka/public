package cz.net21.ttulka.rmimeetsjms;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cz.net21.ttulka.rmimeetsjms.ObjectSerializer;
import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest.CallParameter;

/**
 * Test for the ObjectSerializer
 * 
 * @author ttulka
 *
 */
public class ObjectSerializerTest {
	
	@Test
	public void serialize() throws IOException {
		final Object o = new Integer(123);
		byte[] bytes = ObjectSerializer.serialize(o);
		Assert.assertNotNull(bytes);
	}
	
	@Test
	public void serializeAndDeserialize() throws ClassNotFoundException, IOException  {
		final Object o = new Integer(123);
		byte[] bytes = ObjectSerializer.serialize(o);
		Integer o2 = (Integer)ObjectSerializer.deserialize(bytes);
		Assert.assertNotNull(o2); 
		Assert.assertEquals(o, o2);
	}
	
	@Test
	public void serializeCallRequest() throws IOException {
		final CallRequest req = getRequest();
		byte[] bytes = ObjectSerializer.serialize(req);
		Assert.assertNotNull(bytes);
	}
	
	@Test
	public void serializeAndDeserializeCallRequest() throws ClassNotFoundException, IOException  {
		final CallRequest req = getRequest();
		byte[] bytes = ObjectSerializer.serialize(req);
		CallRequest req2 = (CallRequest)ObjectSerializer.deserialize(bytes);
		Assert.assertNotNull(req2);
		Assert.assertEquals(req.getReturnType(), req2.getReturnType());
		Assert.assertEquals(req.getMethodName(), req2.getMethodName());
		Assert.assertArrayEquals(req.getParameters().toArray(), req2.getParameters().toArray()); 
	}
	
	@Test
	public void serializeCallReply() throws IOException {
		final CallReply<?> rep = getReply();
		byte[] bytes = ObjectSerializer.serialize(rep);
		Assert.assertNotNull(bytes);
	}
	
	@Test
	public void serializeAndDeserializeCallReply() throws ClassNotFoundException, IOException  {
		final CallReply<?> rep = getReply();
		byte[] bytes = ObjectSerializer.serialize(rep);
		CallReply<?> rep2 = (CallReply<?>)ObjectSerializer.deserialize(bytes);
		Assert.assertNotNull(rep2);
		Assert.assertEquals(rep.getResponse(), rep2.getResponse());
	}
	
	// ---- helpers ----
	
	private CallRequest getRequest() {
		final CallRequest req = new CallRequest();
		req.setReturnType(getReturnType());
		req.setMethodName(getMethodName());
		req.setParameters(getParameters());
		return req;
	}
	
	private Class<?> getReturnType() {
		return Integer.class;
	}
	
	private String getMethodName() {
		return "MyMethodName";
	}

	private List<CallParameter> getParameters() {
		List<CallParameter> params = new LinkedList<>();
		params.add(new CallParameter(String.class, "abc"));
		params.add(new CallParameter(Double.class, 1.2));
		return params;
	}
	
	private CallReply<?> getReply() {
		CallReply<String> rep = new CallReply<>();
		rep.setResponse(getResponse());
		return rep;
	} 
	
	private String getResponse() {
		return "abc";
	}
}
