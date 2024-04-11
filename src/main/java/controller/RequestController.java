package controller;

import db.DataBase;
import http.HttpQueryParams;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class RequestController {
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public static void handleRequest(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
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

        handleRoot(dos);
    }

    private static void handleUserCreate(HttpQueryParams queryParams, DataOutputStream dos) throws IOException {
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

    private static boolean isFile(String path) {
        String[] paths = path.split("\\?");
        String mime = java.net.URLConnection.guessContentTypeFromName(paths[0]);
        return mime != null;
    }

    private static void handleFile(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        // 1. template (html)
        if (handleFileResponse("templates", httpRequest, dos)) {
            return;
        }

        // 2. static (css...)
        handleFileResponse("static", httpRequest, dos);
    }

    private static void handleRoot(DataOutputStream dos) throws IOException {
        byte[] body = "Hello World".getBytes();

        HttpResponse response = new HttpResponse();
        response.getHeaders().put("Content-Type", "text/html;charset=utf-8");
        response.setBody(body);

        response.respond(dos);
    }

    private static boolean handleFileResponse(String parentFolder, HttpRequest request, DataOutputStream dos) throws IOException {
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

    private static String parseMIME(String path) {
        String mimeType = java.net.URLConnection.guessContentTypeFromName(path);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }
}
