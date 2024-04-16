package controller;

import http.*;

public class RootController extends Controller {

    protected void doGet(HttpRequest request, HttpResponse response) {
        byte[] body = "Hello World".getBytes();

        response.setContentType(ContentType.of("html"));
        response.setBody(body);
    }
}
