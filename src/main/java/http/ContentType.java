package http;
public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "text/javascript;charset=utf-8"),
    ICO("ico", "image/icon"),
    PNG("png", "image/jpeg"),
    TTF("ttf", "application/font-ttf"),
    WOFF("woff", "application/font-woff"),
    OCTET("octet", "application/octet-stream");

    private String extension;
    private String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType of(String extension) {
        return valueOf(extension.toUpperCase());
    }

    public String getContentType() {
        return contentType;
    }
}
