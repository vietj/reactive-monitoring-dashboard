package push.dashboard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server());
  }

  @Override
  public void start() {

    Router router = Router.router(vertx);

    // The web server handler
    router.route().handler(StaticHandler.create().setCachingEnabled(false));

    //
    JsonObject dashboard = new JsonObject();

    router.get("/dashboard").handler(ctx -> {
      ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(dashboard.encode());
    });

    // The proxy handler
    vertx.eventBus().<JsonObject>consumer("metrics").handler(msg -> {
      JsonObject metrics = msg.body();
      dashboard.mergeIn(metrics);
    });


    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(8080);
  }
}
