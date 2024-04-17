package http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie extends HttpQueryParams {
    protected static final String QUERY_STRING_DELIMITER = "; ";

    public HttpCookie() {
        this(new HashMap<>());
    }

    public HttpCookie(Map<String, String> queryParam) {
        super(queryParam);
    }

    public static HttpCookie parseParams(String queryString) {
        Map<String, String> queryParams = Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(tokens -> URLDecoder.decode(tokens[KEY_INDEX], StandardCharsets.UTF_8), tokens -> URLDecoder.decode(tokens[VALUE_INDEX], StandardCharsets.UTF_8), (p1, p2) -> p1));
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        return new HttpCookie(queryParams);
    }
}
