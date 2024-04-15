package http;

import org.springframework.http.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponse {

    private String httpVersion = "HTTP/1.1";
    private HttpStatus status = HttpStatus.OK;
    private HttpHeaders headers = new HttpHeaders();
    private byte[] body = null;

    public HttpResponse() {}

    public HttpResponse(HttpStatus status, HttpHeaders headers) {
        this(status, headers, null);
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void respond(DataOutputStream dos) throws IOException {
        dos.writeBytes(String.format("%s %d %s \r\n", httpVersion, status.value(), status.getReasonPhrase()));
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
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
    }
}
