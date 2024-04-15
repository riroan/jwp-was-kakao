package http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final int METHOD = 0;
    private static final int PATH = 1;
    private static final int HTTP_VERSION = 2;

    private final HttpMethod method;
    private final String path;
    private final String httpVersion;

    public RequestLine(HttpMethod method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(String text) {
        String[] tokens = text.split(DELIMITER);
        String method = tokens[METHOD];
        String path = tokens[PATH];
        String httpVersion = tokens[HTTP_VERSION];
        return new RequestLine(HttpMethod.valueOf(method), path, httpVersion);
    }

    public boolean isGet() {
        return method.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return method.equals(HttpMethod.POST);
    }

    public String getPath() {
        return path;
    }
}
