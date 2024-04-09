package model;

import java.util.HashMap;

public class HttpQueryParams extends HashMap<String, String> {
    public static HttpQueryParams of(String path) {
        String[] paths;
        paths = path.split("\\?");
        if (paths.length == 1) {
            return null;
        }
        HttpQueryParams queryParams = new HttpQueryParams();
        for (String line : paths[1].split("&")) {
            String[] tokens = line.split("=");
            queryParams.put(tokens[0], tokens[1]);
        }
        return queryParams;
    }
}
