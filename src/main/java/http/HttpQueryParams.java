package http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpQueryParams {
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_DELIMITER = "\\?";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int QUERY_PART = 1;

    private final Map<String, String> queryParam;

    public HttpQueryParams() {
        this(new HashMap<>());
    }

    public HttpQueryParams(Map<String, String> queryParam) {
        this.queryParam = queryParam;
    }

    public static HttpQueryParams of(String path) {
        String[] paths = path.split(QUERY_DELIMITER);
        if (paths.length == 1) {
            return null;
        }
        return parseParams(paths[QUERY_PART]);
    }

    public static HttpQueryParams parseParams(String queryString) {
        Map<String, String> queryParams = Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(tokens -> URLDecoder.decode(tokens[KEY_INDEX], StandardCharsets.UTF_8), tokens -> URLDecoder.decode(tokens[VALUE_INDEX], StandardCharsets.UTF_8)));
        return new HttpQueryParams(queryParams);
    }

    public String get(String key) {
        return queryParam.get(key);
    }
}
