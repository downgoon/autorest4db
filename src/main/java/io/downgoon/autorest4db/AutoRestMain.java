package io.downgoon.autorest4db;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.downgoon.autorest4db.api.DbapiAction;
import io.downgoon.autorest4db.api.DbmetaAction;
import io.downgoon.autorest4db.rest.RESTful;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class AutoRestMain {

	private static final Logger LOG = LoggerFactory.getLogger(AutoRestMain.class);

	/**
	 * java -Dport=80 io.downgoon.autorest4db.AutoRestMain
	 */
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer server = vertx.createHttpServer();

		Router mainRouter = Router.router(vertx);

		Router dbapiRouter = Router.router(vertx);
		mainRouter.mountSubRouter("/dbapi", dbapiRouter);

		Router dbmetaRouter = Router.router(vertx);
		mainRouter.mountSubRouter("/dbmeta", dbmetaRouter);

		// main.dbapi
		RESTful dbapiAction = new DbapiAction();

		dbapiRouter.route().handler(routingContext -> {
			LOG.debug("comming request: {}", routingContext.request().absoluteURI());
			routingContext.response().putHeader("Server", "autorest4db");
			routingContext.response().putHeader("Content-Type", "application/json;charset=UTF-8");
			routingContext.next();
		});

		dbapiRouter.route(HttpMethod.GET, "/:dbName/:tableName").handler(dbapiAction::getAll);
		dbapiRouter.route(HttpMethod.GET, "/:dbName/:tableName/:id").handler(dbapiAction::getId);
		dbapiRouter.route(HttpMethod.DELETE, "/:dbName/:tableName/:id").handler(dbapiAction::delete);

		dbapiRouter.route().handler(BodyHandler.create()); // For POST/PUT
		dbapiRouter.route(HttpMethod.POST, "/:dbname/:tableName").handler(dbapiAction::post);
		dbapiRouter.route(HttpMethod.PUT, "/:dbName/:tableName/:id").handler(dbapiAction::put);

		// main.dbmeta
		DbmetaAction dbmetaAction = new DbmetaAction();
		String uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "autorest4db";

		dbmetaRouter.route()
				.handler(BodyHandler.create(uploadDir).setBodyLimit(1024 * 1024).setDeleteUploadedFilesOnEnd(true));
		dbmetaRouter.route(HttpMethod.POST, "/:dbname").handler(dbmetaAction::upload);

		// handler all requests in RequestHandler
		server.requestHandler(mainRouter::accept);

		int port = Integer.parseInt(System.getProperty("port", "8080"));
		server.listen(port);

		LOG.info("autorest4db is listening on: {}", port);

	}

}
