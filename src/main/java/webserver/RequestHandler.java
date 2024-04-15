package webserver;

import controller.RequestController;
import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestParserUtils;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequestParserUtils.parse(br);
            logger.debug(httpRequest.toString());

            RequestController.handleRequest(httpRequest, dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
