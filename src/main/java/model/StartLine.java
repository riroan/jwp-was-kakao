package model;

import org.springframework.http.HttpMethod;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class StartLine {
    private final HttpMethod method;
    private final String path;
    private final String httpVersion;

    public StartLine(HttpMethod method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static StartLine of(String text) {
        String[] tokens = text.split(" ");
        String method = tokens[0];
        String path = URLDecoder.decode(tokens[1], StandardCharsets.UTF_8);
        String httpVersion = tokens[2];
        return new StartLine(HttpMethod.resolve(method), path, httpVersion);
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
