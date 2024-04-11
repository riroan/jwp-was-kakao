package webserver;

import db.DataBase;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import utils.FileIoUtils;
import utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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

            if (path.startsWith("/user/create")) {
                if (httpRequest.getStartLine().getMethod().equals(HttpMethod.GET)) {
                    handleUserCreate(httpRequest.getQueryParams(), dos);
                    return;
                }
                if (httpRequest.getStartLine().getMethod().equals(HttpMethod.POST)) {
                    String body = (String) httpRequest.getBody();

                    HttpQueryParams queryParams = new HttpQueryParams();
                    for (String line : body.split("&")) {
                        String[] tokens = line.split("=");
                        queryParams.put(tokens[0], tokens[1]);
                    }

                    handleUserCreate(queryParams, dos);
                    return;
                }
            }

            // 3. etc
            handleRoot(dos);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void handleUserCreate(HttpQueryParams queryParams, DataOutputStream dos) throws IOException {
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");
        String name = queryParams.get("name");
        String email = queryParams.get("email");
        User user = new User(userId, password, name, email);

        DataBase.addUser(user);
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.getHeaders().put("Location", "/index.html");
        response.respond(dos);
    }

    private boolean isFile(String path) {
        String[] paths = path.split("\\?");
        String mime = java.net.URLConnection.guessContentTypeFromName(paths[0]);
        return mime != null;
    }

    private void handleFile(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
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
        HttpQueryParams queryParams = HttpQueryParams.of(startLine.getPath());
        String body = parseBody(br, headers);

        return new HttpRequest(startLine, headers, queryParams, body);
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
            String body = IOUtils.readData(br, Integer.parseInt(headers.get(key)));
            return URLDecoder.decode(body, StandardCharsets.UTF_8);
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
