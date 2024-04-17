package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestParserUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller defaultController = new FileController();

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    static {
        controllers.put("/", new RootController());
        controllers.put("/user/create", new UserCreateController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new UserListController());
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequestParserUtils.parse(br);
            HttpResponse response = new HttpResponse();

            String path = request.getRawPath();
            Controller controller = getController(path);

            controller.service(request, response);

            response.respond(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Controller getController(String path) {
        return controllers.getOrDefault(path, defaultController);
    }
}
