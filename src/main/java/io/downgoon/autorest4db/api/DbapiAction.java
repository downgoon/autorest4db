package io.downgoon.autorest4db.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.downgoon.autorest4db.biz.TableCrudBiz;
import io.downgoon.autorest4db.rest.RESTful;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class DbapiAction implements RESTful {

	private TableCrudBiz crud = new TableCrudBiz();

	@Override
	public void getAll(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");
		String tableName = routingContext.request().getParam("tableName");

		List<Object> records = null;
		if (hasQueryParam(routingContext.request())) {
			Map<String, Object> map = toMapFormat(routingContext.request().params());
			records = crud.getList(dbName, tableName, map);
		} else {
			records = crud.getList(dbName, tableName);
		}

		if (records == null) {
			routingContext.fail(404);
			return;
		}

		String json = String.format("{\"resources\":%s}", new JsonArray(records).toString());
		routingContext.response().end(json);
	}

	private boolean hasQueryParam(HttpServerRequest request) {
		return request.query() != null && request.query().length() > 0 && request.params().size() > 0;
	}

	private Map<String, Object> toMapFormat(MultiMap form) {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> formFields = form.names();
		for (String fieldName : formFields) {
			map.put(fieldName, form.get(fieldName));
		}
		return map;
	}

	@Override
	public void getId(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");
		String tableName = routingContext.request().getParam("tableName");
		String id = routingContext.request().getParam("id");

		Object record = crud.getDetail(dbName, tableName, id);

		if (record == null) {
			routingContext.fail(404);
			return;
		}

		routingContext.response().end(JsonObject.mapFrom(record).toString());
	}

	@Override
	public void post(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");
		String tableName = routingContext.request().getParam("tableName");

		JsonObject bodyJson = routingContext.getBodyAsJson();
		@SuppressWarnings("unchecked")
		Map<String, Object> bodyMap = bodyJson.mapTo(Map.class);

		Object record = crud.create(dbName, tableName, bodyMap);

		if (record == null) {
			routingContext.fail(404);
			return;
		}

		routingContext.response().end(JsonObject.mapFrom(record).toString());
	}

	@Override
	public void put(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");
		String tableName = routingContext.request().getParam("tableName");
		String id = routingContext.request().getParam("id");

		JsonObject bodyJson = routingContext.getBodyAsJson();
		@SuppressWarnings("unchecked")
		Map<String, Object> bodyMap = bodyJson.mapTo(Map.class);

		int rows = crud.update(dbName, tableName, id, bodyMap);
		if (rows <= 0) {
			routingContext.fail(404);
			return;
		}

		routingContext.response().end(bodyJson.toString());
	}

	@Override
	public void delete(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");
		String tableName = routingContext.request().getParam("tableName");
		String id = routingContext.request().getParam("id");

		int rows = crud.remove(dbName, tableName, id);
		if (rows <= 0) {
			routingContext.fail(404);
			return;
		}

		routingContext.response().end();
	}

}
