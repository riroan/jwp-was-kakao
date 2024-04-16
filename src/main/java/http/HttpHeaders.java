package http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {
    private static final String LINE_DELIMITER = "\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
        String cookieString = headers.getOrDefault("cookie", "");
        if (cookieString.isEmpty()) {
            this.cookie = new HttpCookie();
        } else {
            this.cookie = (HttpCookie) HttpCookie.parseParams(cookieString);
        }
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

    public void addCookie(String key, String value) {
        cookie.put(key, value);
    }

    public HttpCookie cookie() {
        return cookie;
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        String cookieString = headers.getOrDefault("Cookie", "");
        if (cookieString.isEmpty()) {
            return new HttpCookie();
        }
        return (HttpCookie) HttpCookie.parseParams(cookieString);
    }

    public List<String> getHeaderLine() {
        List<String> headerLine = headers.entrySet()
                .stream()
                .map(entry -> String.format("%s: %s\r\n", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        if (!cookie.isEmpty()) {
            headerLine.add(String.format("%s: %s\r\n", "Cookie", cookie.getCookieString()));
        }
        return headerLine;
    }
}
