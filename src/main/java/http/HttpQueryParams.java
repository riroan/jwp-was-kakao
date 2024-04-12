package http;

import java.util.HashMap;

public class HttpQueryParams extends HashMap<String, String> {
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static HttpQueryParams of(String path) {
        String[] paths;
        paths = path.split("\\?");
        if (paths.length == 1) {
            return null;
        }
        return parseParams(paths[1]);
    }

    public static HttpQueryParams parseParams(String queryString) {
        HttpQueryParams queryParams = new HttpQueryParams();
        for (String line : queryString.split(QUERY_STRING_DELIMITER)) {
            String[] tokens = line.split(KEY_VALUE_DELIMITER);
            queryParams.put(tokens[KEY_INDEX], tokens[VALUE_INDEX]);
        }
        return queryParams;
    }
}
