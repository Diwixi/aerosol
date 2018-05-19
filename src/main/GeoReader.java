package main;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Diwixis on 17.05.2018.
 */
public class GeoReader {

    private static void readFileAsList(String folder, String fileName) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(folder, fileName), Charset.defaultCharset());
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
