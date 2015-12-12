package standalone.weka;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import samza.model.ProductionLog;
import samza.model.Transaction;
import samza.util.DBManager;

public class MongoToArff {
	public static void main(String[] args) {
		HashMap<String, String> rez = new HashMap<>();
		DBManager.INSTANCE.connect("mpi-transactions", "transactions");
		DBManager.INSTANCE.wrap(Transaction.class);
		DBCollection collection = DBManager.INSTANCE.getCollection();
		DBCursor c = collection.find().skip(0).limit(1000000);
		try {
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter("/home/aleksandar/Desktop/test.txt", true)));
			while (c.hasNext()) {
				DBObject o = c.next();
				ArrayList<String> links = (ArrayList<String>) o.get("links");
				if (links.size() == 4) {
					String ip = (String) o.get("ip");
					String s = "";
					boolean found_comma = false;
					for (int i = 0; i < 4; i++) {
						if (java.net.URLDecoder.decode(links.get(i), "UTF-8").contains(","))
							found_comma = true;
						s += java.net.URLDecoder.decode(links.get(i), "UTF-8");
						s += ",";
					}
					s = s.substring(0, s.length() - 1);
					System.out.println(ip + "," + s);

					
					if (!s.contains("%") && (!s.contains(" ")) && (!found_comma)) {
						out.println(ip + "," + s);
						out.flush();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DONE!");
	}
}
