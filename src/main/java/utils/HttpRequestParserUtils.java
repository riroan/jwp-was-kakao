package utils;

import http.HttpHeaders;
import http.HttpQueryParams;
import http.HttpRequest;
import http.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequestParserUtils {
    private static final String CONTENT_LENGTH = "Content-Length";

    public static HttpRequest parse(BufferedReader br) throws IOException {
        RequestLine RequestLine = http.RequestLine.of(br.readLine());
        HttpHeaders headers = HttpHeaders.of(parseHeader(br));
        HttpQueryParams queryParams = HttpQueryParams.of(RequestLine.getPath());
        String body = parseBody(br, headers);

        return new HttpRequest(RequestLine, headers, queryParams, body);
    }

    private static String parseHeader(BufferedReader br) throws IOException {
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

    private static String parseBody(BufferedReader br, HttpHeaders headers)
            throws IOException {
        if (headers.containsKey(CONTENT_LENGTH)) {
            String body = IOUtils.readData(br, Integer.parseInt(headers.get(CONTENT_LENGTH)));
            return URLDecoder.decode(body, StandardCharsets.UTF_8);
        }
        return null;
    }
}
