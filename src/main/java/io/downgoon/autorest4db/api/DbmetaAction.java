package io.downgoon.autorest4db.api;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.downgoon.autorest4db.dao.TableSchemaUpdator;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class DbmetaAction {

	private static final Logger LOG = LoggerFactory.getLogger(DbmetaAction.class);

	private TableSchemaUpdator tableSchemaUpdator = new TableSchemaUpdator();

	public void upload(RoutingContext routingContext) {
		String dbName = routingContext.request().getParam("dbName");

		JsonObject json = new JsonObject(); // response
		int failCount = 0;
		Set<FileUpload> uploads = routingContext.fileUploads();
		for (FileUpload fupload : uploads) {

			LOG.info("db: {}, fileName: {}, uploadedFileName: {}, size: {}", dbName, fupload.fileName(),
					fupload.uploadedFileName(), fupload.size());

			// read uploaded file and execute SQL script
			FileSystem fileSystem = routingContext.vertx().fileSystem();
			String sqlScript = fileSystem.readFileBlocking(fupload.uploadedFileName()).toString();

			try {
				tableSchemaUpdator.alterTableSchemas(dbName, sqlScript);
				json.put(fupload.name(), "SUCC");
			} catch (Exception e) {
				json.put(fupload.name(), e.getMessage());
				failCount++;
				LOG.info("table schema update exception: {}", sqlScript, e);
			}

		}

		routingContext.response().putHeader("Content-Type", "application/json;charset=UTF-8");
		if (failCount > 0) {
			routingContext.response().setStatusCode(202);
		}
		routingContext.response().end(json.toString());

	}
}
