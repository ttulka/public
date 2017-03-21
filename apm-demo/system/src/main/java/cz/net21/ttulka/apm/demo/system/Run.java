package cz.net21.ttulka.apm.demo.system;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 *
 * Check the property file and print the content of all the files for every application.
 */
public class Run {

    public static void main(String[] argv) {
        // check the parameters
        if (argv == null || argv.length < 2) {
            System.err.println("Program needs two parameters:");
            System.err.println("- path to the application configuration property file,");
            System.err.println("- path to the data directory.");
            System.exit(-1);
        }
        new Run().run(argv[0], argv[1]);
    }

    void run(String applicationsFilename, String dataFolder) {
        // read the config file
        try (BufferedReader br = new BufferedReader(new FileReader(new File(applicationsFilename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> files = getFiles(line);

                for (String file : files) {
                    printFileContent(dataFolder + File.separator + file);
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error by reading a config file '" + applicationsFilename +"'.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    List<String> getFiles(String property) {
        List<String> toReturn = new ArrayList<>();

        String filesString = property.substring(property.indexOf("=") + 1);

        if (filesString != null && !filesString.isEmpty()) {
            for (String file : filesString.split(",")) {
                toReturn.add(file.trim());
            }
        }
        return toReturn;
    }

    void printFileContent(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            System.err.println("Error by reading a file '" + filename + "'.");
            e.printStackTrace();
        }
    }
}
