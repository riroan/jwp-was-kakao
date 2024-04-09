package model;

public class HttpRequest {
    private final StartLine startLine;
    private final HttpHeaders headers;
    private final Object body;

    public HttpRequest(StartLine startLine, HttpHeaders headers, Object body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }
}
