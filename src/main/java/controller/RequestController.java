package controller;

import db.DataBase;
import http.*;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestController {
    private static final String EXT_DELIMITER = "\\.";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String PATH_DELIMITER = "/";
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public static void handleRequest(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        String path = httpRequest.getPath();
        HttpResponse httpResponse = null;
        if (isFile(path)) {
            httpResponse = handleFile(httpRequest);
        }

        if (path.startsWith("/user/create")) {
            if (httpRequest.isGet()) {
                httpResponse = handleUserCreate(httpRequest.getQueryParams());
            }
            if (httpRequest.isPost()) {
                String body = (String) httpRequest.getBody();

                HttpQueryParams queryParams = HttpQueryParams.parseParams(body);

                httpResponse = handleUserCreate(queryParams);
            }
        }

        if (path.equals("/")) {
            httpResponse = handleRoot();
        }

        if (httpResponse == null) {
            return;
        }
        httpResponse.respond(dos);
    }

    private static HttpResponse handleUserCreate(HttpQueryParams queryParams) {
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");
        String name = queryParams.get("name");
        String email = queryParams.get("email");
        User user = new User(userId, password, name, email);

        DataBase.addUser(user);

        return HttpResponse.redirect("/index.html");
    }

    public static ContentType extractExt(String path) {
        String[] tokens = path.split(PATH_DELIMITER);
        if (tokens.length < 1) {
            return null;
        }
        String file = tokens[tokens.length - 1];
        String[] fileNames = file.split(EXT_DELIMITER);
        if (fileNames.length <= 1) {
            return null;
        }
        return ContentType.of(fileNames[fileNames.length - 1]);
    }

    private static boolean isFile(String path) {
        String[] paths = path.split(QUERY_DELIMITER);
        ContentType mime = extractExt(paths[0]);
        return mime != null;
    }

    private static boolean existFile(String path) {
        // ?: 상대경로로 안돼서 절대경로로 수정했습니다.
        Path absolutePath = Paths.get(path).toAbsolutePath();
        File file = new File(absolutePath.toString());
        return file.exists();
    }

    private static HttpResponse handleFile(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        // 1. template (html)
        if (existFile("src/main/resources/templates" + path)) {
            return handleFileResponse("templates", httpRequest);
        }

        // 2. static (css...)
        return handleFileResponse("static", httpRequest);
    }

    private static HttpResponse handleRoot() {
        byte[] body = "Hello World".getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", "text/html;charset=utf-8");

        return new HttpResponse(HttpStatus.OK, headers, body);
    }

    private static HttpResponse handleFileResponse(String parentFolder, HttpRequest request) throws IOException {
        try {
            String path = request.getPath();
            String filePath = parentFolder + path;

            byte[] body = FileIoUtils.loadFileFromClasspath(filePath);
            HttpHeaders headers = new HttpHeaders();
            String mime = parseMIME(filePath);
            headers.put("Content-Type", mime + ";charset=utf-8");
            return new HttpResponse(HttpStatus.OK, headers, body);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            logger.info(String.format("file does not exist in '%s'", parentFolder));
        }
        return null;
    }

    private static String parseMIME(String path) {
        String[] paths = path.split(QUERY_DELIMITER);
        ContentType mimeType = extractExt(paths[0]);
        if (mimeType == null) {
            return "application/octet-stream";
        }
        return mimeType.getContentType();
    }
}
