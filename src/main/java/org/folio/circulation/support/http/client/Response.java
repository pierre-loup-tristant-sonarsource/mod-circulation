package org.folio.circulation.support.http.client;

import static java.lang.String.format;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;

public class Response {
  protected final String body;
  private final int statusCode;
  private final String contentType;
  private final CaseInsensitiveHeaders headers;
  private final String fromUrl;

  public Response(int statusCode, String body, String contentType) {
    this(statusCode, body, contentType, new CaseInsensitiveHeaders(), null);
  }

  public Response(int statusCode, String body, String contentType,
    CaseInsensitiveHeaders headers, String fromUrl) {

    this.statusCode = statusCode;
    this.body = body;
    this.contentType = contentType;
    this.headers = headers;
    this.fromUrl = fromUrl;
  }

  static Response responseFrom(String url, HttpResponse<Buffer> response) {
    final CaseInsensitiveHeaders headers = new CaseInsensitiveHeaders();

    headers.addAll(response.headers());

    return new Response(response.statusCode(), response.bodyAsString(),
      headers.get(HttpHeaders.CONTENT_TYPE), headers, url);
  }

  public boolean hasBody() {
    return StringUtils.isNotBlank(getBody());
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getBody() {
    return body;
  }

  public JsonObject getJson() {
    if(hasBody()) {
      return new JsonObject(getBody());
    }
    else {
      return new JsonObject();
    }
  }

  public String getContentType() {
    return contentType;
  }

  String getHeader(String name) {
    return headers.get(name);
  }

  String getFromUrl() {
    return fromUrl;
  }

  @Override
  public String toString() {
    return format(
      "Response from \"%s\" status code: %s body: \"%s\", content type: \"%s\"",
        getFromUrl(), getStatusCode(), getBody(), getContentType());
  }
}
