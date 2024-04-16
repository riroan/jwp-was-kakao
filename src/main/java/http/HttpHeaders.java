package http;

import java.util.*;
import java.util.stream.Collectors;

public class HttpHeaders {
    private static final String LINE_DELIMITER = "\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(String text) {
        Map<String, String> headers = Arrays.stream(text.split(LINE_DELIMITER))
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(tokens -> tokens[KEY_INDEX], tokens -> tokens[VALUE_INDEX]));
        return new HttpHeaders(headers);
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void put(ContentType contentType) {
        headers.put("Content-Type", contentType.getContentType() + ";charset=utf-8");
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        String cookieString = headers.getOrDefault("cookie", "");
        if (cookieString.isEmpty()) {
            return new HttpCookie();
        }
        return (HttpCookie) HttpCookie.parseParams(cookieString);
    }

    public List<String> getHeaderLine() {
        return headers.entrySet()
                .stream()
                .map(entry -> String.format("%s: %s\r\n", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
