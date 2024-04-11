package http;

public class HttpRequest {
    private final StartLine startLine;
    private final HttpHeaders headers;
    private final HttpQueryParams queryParams;
    private final Object body;

    public HttpRequest(StartLine startLine, HttpHeaders headers, HttpQueryParams queryParams, Object body) {
        this.startLine = startLine;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public HttpQueryParams getQueryParams() {
        return queryParams;
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
