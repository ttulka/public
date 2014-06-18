package cz.net21.ttulka.rmimeetsjms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Helper class for de/serializing objects
 *  
 * @author ttulka
 *
 */
class ObjectSerializer {

	/**
	 * Serialize the object into an array of bytes 
	 * 
	 * @param obj the object
	 * @return array of bytes
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			return bos.toByteArray();
		} 
		finally {
			try {
				if (out != null) {
					out.close();
				}
			}
			catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			}
			catch (IOException ex) {
				// ignore close exception
			}
		}
	}

	/**
	 * Deserialize an object from the array of bytes
	 *  
	 * @param bytes the array of bytes
	 * @return object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return in.readObject();
		}
		finally {
			try {
				bis.close();
			}
			catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			}
			catch (IOException ex) {
				// ignore close exception
			}
		}
	}
}
