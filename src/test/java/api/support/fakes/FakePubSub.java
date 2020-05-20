package api.support.fakes;

import static org.folio.HttpStatus.HTTP_CREATED;
import static org.folio.HttpStatus.HTTP_INTERNAL_SERVER_ERROR;
import static org.folio.HttpStatus.HTTP_NO_CONTENT;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class FakePubSub {
  private static final Logger logger = LoggerFactory.getLogger(FakePubSub.class);
  private static final List<JsonObject> publishedEvents = new ArrayList<>();

  private static boolean failPubSubRegistration;
  private static boolean failPubSubUnregistering;

  public static void register(Router router) {
    router.route().handler(BodyHandler.create());

    router.post("/pubsub/publish")
      .handler(routingContext -> {
        publishedEvents.add(routingContext.getBodyAsJson());
        routingContext.response()
          .setStatusCode(HTTP_NO_CONTENT.toInt())
          .end();
      });

    router.post("/pubsub/event-types")
      .handler(FakePubSub::postTenant);

    router.post("/pubsub/event-types/declare/publisher")
      .handler(FakePubSub::postTenant);

    router.post("/pubsub/event-types/declare/subscriber")
      .handler(FakePubSub::postTenant);

    router.delete("/pubsub/event-types/:eventTypeName/publishers")
      .handler(FakePubSub::deleteTenant);
  }

  private static void postTenant(RoutingContext routingContext) {
    if (failPubSubRegistration) {
      routingContext.response()
        .setStatusCode(HTTP_INTERNAL_SERVER_ERROR.toInt())
        .end();
    }
    else {
      String json = routingContext.getBodyAsJson().encodePrettily();
      Buffer buffer = Buffer.buffer(json, "UTF-8");
      routingContext.response()
        .setStatusCode(HTTP_CREATED.toInt())
        .putHeader("content-type", "application/json; charset=utf-8")
        .putHeader("content-length", Integer.toString(buffer.length()))
        .write(buffer)
        .end();
    }
  }

  private static void deleteTenant(RoutingContext routingContext) {
    if (failPubSubUnregistering) {
      routingContext.response()
        .setStatusCode(HTTP_INTERNAL_SERVER_ERROR.toInt())
        .end();
    }
    else {
      routingContext.response()
        .setStatusCode(HTTP_NO_CONTENT.toInt())
        .end();
    }
  }

  public static List<JsonObject> getPublishedEvents() {
    return publishedEvents;
  }

  public static void cleanUp() {
    publishedEvents.clear();
  }

  public static void setFailPubSubRegistration(boolean failPubSubRegistration) {
    FakePubSub.failPubSubRegistration = failPubSubRegistration;
  }

  public static void setFailPubSubUnregistering(boolean failPubSubUnregistering) {
    FakePubSub.failPubSubUnregistering = failPubSubUnregistering;
  }
}
