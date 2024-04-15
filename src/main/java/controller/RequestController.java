package controller;

import db.DataBase;
import http.HttpHeaders;
import http.HttpQueryParams;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class RequestController {
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public static void handleRequest(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        String path = httpRequest.getPath();
        if (isFile(path)) {
            handleFile(httpRequest, dos);
            return;
        }

        if (path.startsWith("/user/create")) {
            if (httpRequest.isGet()) {
                handleUserCreate(httpRequest.getQueryParams(), dos);
                return;
            }
            if (httpRequest.isPost()) {
                String body = (String) httpRequest.getBody();

                HttpQueryParams queryParams = HttpQueryParams.parseParams(body);

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

        HttpResponse response = HttpResponse.redirect("/index.html");
        response.respond(dos);
    }

    public static String extractExt(String path) {
        String[] tokens = path.split("/");
        if (tokens.length < 1) {
            return null;
        }
        String file = tokens[tokens.length - 1];
        String[] fileNames = file.split("\\.");
        if (fileNames.length <= 1) {
            return null;
        }
        return fileNames[fileNames.length - 1];
    }

    private static boolean isFile(String path) {
        String[] paths = path.split("\\?");
        String mime = extractExt(paths[0]);
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
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", "text/html;charset=utf-8");

        HttpResponse response = new HttpResponse(HttpStatus.OK, headers, body);

        response.respond(dos);
    }

    private static boolean handleFileResponse(String parentFolder, HttpRequest request, DataOutputStream dos) throws IOException {
        try {
            String path = request.getPath();
            String filePath = parentFolder + path;

            byte[] body = FileIoUtils.loadFileFromClasspath(filePath);
            HttpHeaders headers = new HttpHeaders();
            String mime = parseMIME(filePath);
            headers.put("Content-Type", mime + ";charset=utf-8");
            HttpResponse response = new HttpResponse(HttpStatus.OK, headers, body);

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
        String[] paths = path.split("\\?");
        String mimeType = extractExt(paths[0]);
        if (mimeType == null) {
            return "application/octet-stream";
        }
        return "text/" + mimeType;
    }
}
