package http;

import java.util.stream.Collectors;

public class HttpCookie extends HttpQueryParams {
    protected static final String QUERY_STRING_DELIMITER = "; ";

    public String getCookieString() {
        return String.join(QUERY_STRING_DELIMITER, queryParam.entrySet()
                .stream()
                .map(entry -> String.format("%s%s%s", entry.getKey(), KEY_VALUE_DELIMITER, entry.getValue()))
                .collect(Collectors.toList())
                .toArray(String[]::new));
    }

    public boolean isEmpty() {
        return queryParam.isEmpty();
    }

    public void put(String key, String value) {
        queryParam.put(key, value);
    }
}
