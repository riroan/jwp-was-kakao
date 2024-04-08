package model;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders extends HashMap<String, String> {

    public static HttpHeaders of(String text) {
        HttpHeaders headers = new HttpHeaders();
        for (String line : text.split("\n")) {
            String[] tokens = line.split(": ");
            headers.put(tokens[0], tokens[1]);
        }
        return headers;
    }
}
