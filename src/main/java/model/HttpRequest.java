package model;

import java.util.Map;

public class HttpRequest {
    private final StartLine startLine;
    private final Map<String, String> headers;
    private final Object body;

    public HttpRequest(StartLine startLine, Map<String,String> headers, Object body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }
}
