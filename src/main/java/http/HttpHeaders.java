package http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    public HttpHeaders (Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(String text) {
        Map<String, String> headers = Arrays.stream(text.split(LINE_DELIMITER))
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(tokens -> tokens[KEY_INDEX], tokens -> tokens[VALUE_INDEX]));
        return new HttpHeaders(headers);
    }
}
