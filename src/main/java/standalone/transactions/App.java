package standalone.transactions;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import samza.model.ProductionLog;
import samza.model.Transaction;
import samza.util.DBManager;
import scala.annotation.meta.param;

public class App {
	private static final String INPUT_DATABASE_NAME = "mpi";
	private static final String INPUT_COLLECTION_NAME = "logs";
	
	private static final String OUTPUT_DATABASE_NAME = "mpi-transactions";
	private static final String OUTPUT_COLLECTION_NAME = "transactions";
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static void main(String[] args) {
		DBManager.INSTANCE.connect(INPUT_DATABASE_NAME, INPUT_COLLECTION_NAME);
		DBManager.INSTANCE.wrap(ProductionLog.class);
		DBCollection collection = DBManager.INSTANCE.getCollection();
		ArrayList<Transaction> transactions = new ArrayList<>();
		HashMap<String, ArrayList<String>> transactions_temp = new HashMap<>();
		HashMap<String, Date> last_active = new HashMap<>();
		DBCursor c = collection.find().skip(0).limit(2639336);
		DateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH);
		while (c.hasNext()) {
			DBObject o = c.next();
			HashMap<String, String> parameters = (HashMap<String, String>) o.get("parameters");
			try {
				String ipaddress = parameters.get("id");
				String link = parameters.get("link");
				String date_str = (String) o.get("date");

				if (ipaddress == null || link == null || date_str == null)
					continue;

				Date date = format.parse(date_str);

				// System.out.println(ipaddress +" (" + date + ") " + " -> " +
				// link);
				if (!transactions_temp.containsKey(ipaddress)) {
					ArrayList<String> temp = new ArrayList<>();
					temp.add(link);
					transactions_temp.put(ipaddress, temp);
					last_active.put(ipaddress, date);
				} else {
					//System.out.println(date.getTime() - last_active.get(ipaddress).getTime());
					if (date.getTime() - last_active.get(ipaddress).getTime() > 10000
							&& !transactions_temp.get(ipaddress).isEmpty()) {
						transactions.add(new Transaction(ipaddress, transactions_temp.get(ipaddress)));
						ArrayList<String> temp = new ArrayList<>();
						temp.add(link);
						transactions_temp.put(ipaddress, temp);
					} else {
						transactions_temp.get(ipaddress).add(link);
					}
					last_active.put(ipaddress, date);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		DBManager.INSTANCE.connect(OUTPUT_DATABASE_NAME, OUTPUT_COLLECTION_NAME);
		DBManager.INSTANCE.wrap(Transaction.class);
		
		for (Transaction list : transactions){
			DBManager.INSTANCE.insert(list);
			
			/*
			System.out.println("---------------------");
			for (String s : list.getLinks()){
				System.out.println(s);
			}
			*/
		}
		System.out.println("DONE");
	}
}
