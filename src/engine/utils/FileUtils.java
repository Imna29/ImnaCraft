package engine.utils;

import main.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileUtils {
    public static String loadAsString(String path){
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(path)))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("COULDN'T FIND THE FILE AT " + path);
        } catch (NullPointerException e){
            System.out.println("WHAT THE FUCK");
        }
        return result.toString();
    }
}
