package controller;

import http.*;
import utils.FileIoUtils;

import java.io.IOException;
import java.net.URISyntaxException;


public class FileController extends Controller {
    private static final String EXT_DELIMITER = "\\.";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String PATH_DELIMITER = "/";
    private static final int MINIMUM_LENGTH = 1;

    protected void doGet(HttpRequest request, HttpResponse response) {
        // 1. template (html)
        try {
            handleFileResponse("templates", request, response);
            return;
        } catch (IOException | URISyntaxException | NullPointerException e) {
             logger.info("Not a template file");
        }

        // 2. static (css...)
        try {
            handleFileResponse("static", request, response);
            return;
        } catch (IOException | URISyntaxException | NullPointerException e) {
            logger.info("Not a static file");
        }
        response.setStatus(HttpStatus.NOT_FOUND);
    }

    private void handleFileResponse(String parentFolder, HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String path = request.getPath();
        String filePath = parentFolder + path;

        byte[] body = FileIoUtils.loadFileFromClasspath(filePath);
        ContentType contentType = parseMIME(filePath);
        response.setContentType(contentType);

        response.setBody(body);
    }

    private static ContentType parseMIME(String path) {
        String[] paths = path.split(QUERY_DELIMITER);
        ContentType mimeType = extractExt(paths[0]);
        if (mimeType == null) {
            return ContentType.of("OCTET");
        }
        return mimeType;
    }

    public static ContentType extractExt(String path) {
        String[] tokens = path.split(PATH_DELIMITER);
        if (tokens.length < MINIMUM_LENGTH) {
            return null;
        }
        String file = tokens[tokens.length - 1];
        String[] fileNames = file.split(EXT_DELIMITER);
        if (fileNames.length <= MINIMUM_LENGTH) {
            return null;
        }
        return ContentType.of(fileNames[fileNames.length - 1]);
    }
}
