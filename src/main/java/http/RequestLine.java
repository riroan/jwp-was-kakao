package http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String httpVersion;

    public RequestLine(HttpMethod method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(String text) {
        String[] tokens = text.split(" ");
        String method = tokens[0];
        String path = URLDecoder.decode(tokens[1], StandardCharsets.UTF_8);
        String httpVersion = tokens[2];
        return new RequestLine(HttpMethod.valueOf(method), path, httpVersion);
    }

    public boolean isGet() {
        return method.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return method.equals(HttpMethod.POST);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
