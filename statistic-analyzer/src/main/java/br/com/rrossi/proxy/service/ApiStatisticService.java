package br.com.rrossi.proxy.service;

import br.com.rrossi.proxy.model.ApiStatisticModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@ApplicationScoped
public class ApiStatisticService {

    public static final String PROXY_DATABASE = "proxy";
    public static final String PROXY_COLLECTION = "proxy";

    MongoClient mongoClient;

    @Inject
    public ApiStatisticService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public List<ApiStatisticModel> findAllApiCalls(String basePath) {
        List<ApiStatisticModel> apiStatisticModelList = new ArrayList<>();

        Document document = new Document();
        document.append("basePath", basePath);
        MongoCursor<Document> cursor = getCollection().find(document).cursor();

        while (cursor.hasNext())
            apiStatisticModelList.add(new ApiStatisticModel(cursor.next()));


        return apiStatisticModelList;
    }

    public List<ApiStatisticModel> findApiCalls() {
        List<ApiStatisticModel> apiStatisticModelList = new ArrayList<>();

        BsonDocument bsonArray = BsonDocument.parse("{\"$group\": {\"_id\":\"$basePath\", \"count\": {\"$sum\": 1}}}");

        MongoCursor<Document> cursor = getCollection().aggregate(Collections.singletonList(bsonArray)).cursor();

        while (cursor.hasNext()) {
            ApiStatisticModel model = new ApiStatisticModel(cursor.next());
            model.setBasePath(model.getId());
            model.setId(null);
            apiStatisticModelList.add(model);
        }

        return apiStatisticModelList;
    }

    public List<ApiStatisticModel> findApiAverageResponseTime(String basePath) {
        List<ApiStatisticModel> apiStatisticModelList = new ArrayList<>();

        BsonDocument bsonMatch = BsonDocument.parse(String.format("{\"$match\": {\"basePath\": {\"$eq\": \"%s\"}}}", basePath));
        BsonDocument bsonGroup = BsonDocument.parse("{\"$group\": {\"_id\":\"$basePath\", \"responseTime\": {\"$avg\": \"$responseTime\"}}}");

        MongoCursor<Document> cursor = getCollection().aggregate(Arrays.asList(bsonMatch, bsonGroup)).cursor();

        while (cursor.hasNext()) {
            ApiStatisticModel model = new ApiStatisticModel(cursor.next());
            model.setBasePath(model.getId());
            model.setId(null);
            apiStatisticModelList.add(model);
        }

        return apiStatisticModelList;
    }

    public void add(String model) {
        getCollection().insertOne(Document.parse(model));
    }

    public List<ApiStatisticModel> findApiStatusCode(String basePath) {
        List<ApiStatisticModel> apiStatisticModelList = new ArrayList<>();

        BsonDocument bsonMatch = BsonDocument.parse(String.format("{\"$match\": {\"basePath\": {\"$eq\": \"%s\"}}}", basePath));
        BsonDocument bsonGroup = BsonDocument.parse("{\"$group\": {\"_id\":{\"basePath\": \"$basePath\", \"responseCode\": \"$responseCode\"}, \"count\": {\"$sum\": 1}}}");

        MongoCursor<Document> cursor = getCollection().aggregate(Arrays.asList(bsonMatch, bsonGroup)).cursor();

        while (cursor.hasNext()) {
            Document next = cursor.next();
            ApiStatisticModel model = new ApiStatisticModel(next);
            Document idDocument = (Document) next.get("_" + ApiStatisticModel.ID);
            model.setBasePath(idDocument.get(ApiStatisticModel.BASE_PATH).toString());
            model.setResponseCode(Integer.valueOf(idDocument.get(ApiStatisticModel.RESPONSE_CODE).toString()));
            model.setId(null);
            apiStatisticModelList.add(model);
        }

        return apiStatisticModelList;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(PROXY_DATABASE).getCollection(PROXY_COLLECTION);
    }
}
