package org.folio.circulation.support.http.server;

import io.vertx.core.http.HttpServerResponse;
import org.apache.http.entity.ContentType;

public class ServerErrorResponse {
  private ServerErrorResponse() { }

  public static void internalError(HttpServerResponse response, String reason) {
    response.setStatusCode(500);

    response.putHeader("content-type", ContentType.TEXT_PLAIN.toString());

    if(reason != null) {
      response.end(reason);
    }
    else {
      response.end();
    }
  }
}
