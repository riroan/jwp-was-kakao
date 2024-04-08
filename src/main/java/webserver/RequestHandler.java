package webserver;

import model.HttpHeaders;
import model.HttpRequest;
import model.StartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;
import utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = parseRequest(bufferedReader);

            // 1. template (html)
            if (handleTemplates(httpRequest, dos)) {
                return;
            }

            // 2. static (css...)

            // 3. etc

            handleRoot(dos);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpRequest parseRequest(BufferedReader br) throws IOException {
        StartLine startLine = StartLine.of(br.readLine());
        HttpHeaders headers = HttpHeaders.of(parseHeaderString(br));
        String body = parseBody(br, headers);

        return new HttpRequest(startLine, headers, body);
    }

    private String parseHeaderString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private String parseBody(BufferedReader br, HttpHeaders headers) throws IOException {
        String key = "Content-Length";
        if (headers.containsKey(key)) {
            return IOUtils.readData(br, Integer.parseInt(headers.get(key)));
        }
        return null;
    }

    private void handleRoot(DataOutputStream dos) {
        byte[] body = "Hello World".getBytes();
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private boolean handleTemplates(HttpRequest request, DataOutputStream dos) throws IOException {
        try {
            String filePath = "templates" + request.getStartLine().getPath();
            System.out.println(filePath);
            byte[] body = FileIoUtils.loadFileFromClasspath(filePath);
            response200Header(dos, body.length);
            responseBody(dos, body);
            return true;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            logger.info("file does not exist in 'template'");
            return false;
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
