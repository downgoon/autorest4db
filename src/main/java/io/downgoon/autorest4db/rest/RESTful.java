package io.downgoon.autorest4db.rest;

import io.vertx.ext.web.RoutingContext;

public interface RESTful {

	void getAll(RoutingContext routingContext);

	void getId(RoutingContext routingContext);

	void post(RoutingContext routingContext);

	void put(RoutingContext routingContext);

	void delete(RoutingContext routingContext);

}