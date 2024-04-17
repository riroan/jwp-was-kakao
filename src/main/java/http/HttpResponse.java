package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private List<String> cookies;
    private String httpVersion = "HTTP/1.1";
    private HttpStatus status = HttpStatus.OK;
    private HttpHeaders headers = new HttpHeaders();
    private byte[] body = null;

    public HttpResponse() {
        this.cookies = new ArrayList<>();
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers) {
        this(status, headers, null);
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public void redirect(String path) {
        status = HttpStatus.FOUND;
        headers.put("Location", path);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void respond(DataOutputStream dos) throws IOException {
        dos.writeBytes(String.format("%s %d %s \r\n", httpVersion, status.getStatusCode(), status.getStatus()));
        writeHeaders(dos);
        dos.writeBytes("\r\n");

        if (body != null) {
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }

    private void writeHeaders(DataOutputStream dos) throws IOException {
        if (body != null && body.length > 0) {
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
        }

        for (String cookie : cookies) {
            dos.writeBytes(cookie);
        }

        for (String headerLine : headers.getHeaderLine()) {
            dos.writeBytes(headerLine);
        }
    }

    public void addCookie(String key, String value) {
        this.cookies.add(String.format("Set-Cookie: %s=%s; Path=/\r\n", key, value));
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public void setContentType(ContentType contentType) {
        headers.put("Content-Type", contentType.getContentType());
    }
}
