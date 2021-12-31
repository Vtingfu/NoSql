package com.bjtu.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB Utils class.
 * MongoDB工具类.
 *
 * @author HelloTeam
 * @version 2021/12/26
 */
public class MongoDbUtil {

    // 数据库系统链接
    public static final MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    public static  MongoDatabase mongoDatabase;
    // 数据库名
    public static final String DbName = "profileMgDb";
    // 集合名
    public static final String UserCollection = "userCollection";
    public static final String MovieCollection = "movieCollection";

    /**
     * Set up connection with the MongoDB Database.
     *
     * @param dbName Database name
     */public static void ConnectToMongoDb(String dbName){
        // 连接到数据库
        mongoDatabase = mongoClient.getDatabase(dbName);
    }

    /**
     * Initialization.
     */
    public static void Init(){
        ConnectToMongoDb(DbName);

    }

    /**
     * Add a document into the database.
     *
     * @param collectionNAME Collection to be added
     * @param object         Name of the document
     */
    public static void PutDoc(String collectionNAME , Object object){
        // Target collection
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionNAME);
        // Insert the expected document into the collection
        collection.insertOne(Document.parse(JSONObject.toJSONString(object)));
    }

    public static void main(String[] args) {
        // 初始化mongodb
        MongoDbUtil.Init();
        // 插入数据
        Map<String, ArrayList<String>> rates = new HashMap<String, ArrayList<String>>();
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("test1");
        strings.add("test2");
        rates.put("key1", strings);
        rates.put("key2", new ArrayList<String>(strings));
        MongoDbUtil.PutDoc("testCollection", rates);
    }

}
