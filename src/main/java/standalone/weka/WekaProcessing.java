package standalone.weka;

import weka.associations.Apriori;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.StringToNominal;

public class WekaProcessing {
	public static void main(String[] args) {
		try {
			String s = "/home/aleksandar/Desktop/wekadata.arff";
			DataSource source = new DataSource(s);
			Instances unfiltered_data = source.getDataSet();
			StringToNominal stringToNominal;

			Instances filtered_data = unfiltered_data;
			try {
			       String[] options = new String[2];
			       options[0] = "-R";                // range
			       options[1] = "1-5";                // attributes
			       weka.filters.unsupervised.attribute.StringToNominal ff=new weka.filters.unsupervised.attribute.StringToNominal(); // new instance of filter

			       ff.setOptions(options);   
			       ff.setInputFormat(unfiltered_data);
			       filtered_data = weka.filters.Filter.useFilter(unfiltered_data, ff);
			       System.out.println(filtered_data);
			} catch (Exception ex) {
				throw new RuntimeException("String to nominal conversion failed", ex);
			}

			Apriori model = new Apriori();
			model.buildAssociations(filtered_data);
			System.out.println(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
