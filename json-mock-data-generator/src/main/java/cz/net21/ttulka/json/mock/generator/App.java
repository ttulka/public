package cz.net21.ttulka.json.mock.generator;

import java.io.Console;
import java.nio.file.Paths;

/**
 * Entry point of the application.
 * 
 * @author ttulka
 */
public class App {
	
	static final String OUTPUT_DEFAULT = "output.json";
	
	public static void main(String[] argv) throws Exception {
		final Console console = System.console();
		
		String conf = null;
		while (conf != null) {
			conf = console.readLine("Configuration file: ");
		}
		
		String output = console.readLine("Output JSON file (%s): ", OUTPUT_DEFAULT);
		if (output == null) {
			output = OUTPUT_DEFAULT;
		}
		
		final Generator generator = new Generator(Paths.get(conf), Paths.get(output));
		final Thread thread = new Thread(() -> {
			try {
				generator.run();
			} 
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		thread.start();
		thread.join();
	}
}
