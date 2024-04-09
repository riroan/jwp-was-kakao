package webserver;

import model.HttpHeaders;
import model.HttpRequest;
import model.HttpResponse;
import model.StartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
import utils.FileIoUtils;
import utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            String path = httpRequest.getStartLine().getPath();

            if (isFile(path)) {
                handleFile(httpRequest, dos);
                return;
            }

            // 3. etc

            handleRoot(dos);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean isFile(String path) {
        String mime = java.net.URLConnection.guessContentTypeFromName(path);
        return mime != null;
    }

    private void handleFile(HttpRequest httpRequest, DataOutputStream dos) throws IOException{
        // 1. template (html)
        if (handleFileResponse("templates", httpRequest, dos)) {
            return;
        }

        // 2. static (css...)
        handleFileResponse("static", httpRequest, dos);
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

    private void handleRoot(DataOutputStream dos) throws IOException {
        byte[] body = "Hello World".getBytes();

        HttpResponse response = new HttpResponse();
        response.getHeaders().put("Content-Type", "text/html;charset=utf-8");
        response.setBody(body);

        response.respond(dos);
    }

    private boolean handleFileResponse(String parentFolder, HttpRequest request, DataOutputStream dos) throws IOException {
        try {
            String path = request.getStartLine().getPath();
            String filePath = parentFolder + path;

            HttpResponse response = new HttpResponse();

            byte[] body = FileIoUtils.loadFileFromClasspath(filePath);
            response.setBody(body);
            response.setStatus(HttpStatus.OK);
            response.setHttpVersion(request.getStartLine().getHttpVersion());

            String mime = parseMIME(filePath);
            response.getHeaders().put("Content-Type", mime + ";charset=utf-8");

            response.respond(dos);

            return true;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            logger.info(String.format("file does not exist in '%s'", parentFolder));
            return false;
        }
    }

    private String parseMIME(String path) {
        String mimeType = java.net.URLConnection.guessContentTypeFromName(path);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }
}
