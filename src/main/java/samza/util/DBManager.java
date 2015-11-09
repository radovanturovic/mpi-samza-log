package samza.util;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import org.mongojack.WriteResult;

@SuppressWarnings({"rawtypes", "unchecked"})
public enum DBManager {
	INSTANCE;
	
	private MongoClient client;
	private DB dataBase;
	private DBCollection collection;
	private JacksonDBCollection JackCollection;
	
	private DBManager(){
		try {
			client = new MongoClient("192.168.0.70",27017);
		} catch (Exception e) {
			System.out.println("Error instantiating mongo client!");
			e.printStackTrace();
		}
	}
	
	public MongoClient getClient(){
		return client;
	}
	
	/**
	 * connect to database 
	 * @param dbname data base to connect to
	 * @param colname collection to connect to
	 * @return
	 */
	public boolean connect(String dbname,String colname){
		dataBase = client.getDB(dbname);
		collection = dataBase.getCollection(colname);
		return dataBase!=null && collection!=null;
	}
	
	public boolean changeCollection(String colname){
		collection = dataBase.getCollection(colname);
		return collection != null;
	}

	/**
	 * sets the db connection to parse specific objects
	 * @param objClass
	 */
	public synchronized void wrap(Class objClass){
		this.JackCollection = JacksonDBCollection.wrap(collection, objClass);
	}
	
	/**
	 * sets the db connection to parse specific objects
	 * @param objClass
	 * @param keyClass
	 */
	public synchronized void  wrap(Class objClass,Class keyClass){
		this.JackCollection = JacksonDBCollection.wrap(collection, objClass, keyClass);
	}
	
	/**
	 * Insert object into specified mongo database and collection
	 * @param dataBase data base name
	 * @param collection collection name
	 * @param obj object to serialize
	 * @return id inserted object id
	 */
	public Object insert(Object obj){
		WriteResult result = JackCollection.insert(obj);
		return result.getSavedId();
	}
}
