package http;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpQueryParams queryParams;
    private final Object body;
    private final HttpCookie cookie;

    public HttpRequest(
            RequestLine requestLine,
            HttpHeaders headers,
            HttpQueryParams queryParams,
            Object body,
            HttpCookie cookie
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
        this.cookie = cookie;
    }

    public HttpRequest(
            RequestLine requestLine,
            HttpHeaders headers,
            HttpQueryParams queryParams,
            Object body
    ) {
        this(requestLine, headers, queryParams, body, headers.getCookie());
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getRawPath() {
        return requestLine.getRawPath();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public HttpQueryParams getQueryParams() {
        return queryParams;
    }

    public Object getBody() {
        return body;
    }
}
