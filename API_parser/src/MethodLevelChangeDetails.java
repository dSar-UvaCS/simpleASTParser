import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MethodLevelChangeDetails {

	public static void main(String[] args) throws IOException {
		
		/**
		 * Begin: Setup
		 */

		/* Create string and file to write output to */
		String toWrite = "";
		File file = new File("output/MethodsChanges.csv");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		String header = "API_version,element_type,element_name,element_type_change_type,changed_element_type,changed_element_name,changed_element_modification_type\n";
		bw.write(header);

		/* Iterate through all available version data online */
		for (int version = 24; version >= 19; version--) {
			int versionNum = version;

			int totalChangedMethods = 0;
			int totalAddedMethods = 0;
			int totalRemovedMethods = 0;
			
			/**
			 * "CHANGES" Methods
			 */

			String changedMethodsUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/methods_index_changes.html";

			/* Loads HTML and grabs table entries from list */
			Document changedMethodsDoc = Jsoup.connect(changedMethodsUrl).get();
			Elements changedMethodsList = changedMethodsDoc.getElementsByClass("hiddenlink");
						
			for (Element changedMethod : changedMethodsList) {
				
				String changedMethodName = changedMethod.text();
				String changedMethodLink = changedMethod.attr("href");
				String primaryToWrite = versionNum  + ",Method,\""+ changedMethodName + "\",Changes\n"; // alters the output string
				
				bw.write(primaryToWrite);
				
				if (!primaryToWrite.isEmpty())
					totalChangedMethods += 1;
			}
			
			// routinely clear writer
			bw.flush();

			/**
			 * "ADDITIONS" Methods
			 */
			
			String addedMethodsUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/methods_index_additions.html";

			/* Loads HTML and grabs table entries from list */
			Document addedMethodsDoc = Jsoup.connect(addedMethodsUrl).get();

			Elements addedMethodsList = addedMethodsDoc.getElementsByClass("hiddenlink");
			
			for (Element addedMethod : addedMethodsList) {
				
				String addedMethodName = addedMethod.text();
				String addedMethodLink = addedMethod.attr("href");
				String primaryToWrite = versionNum  + ",Method,\""+ addedMethodName + "\",Additions\n"; // alters the output string
				
				bw.write(primaryToWrite);
				
				if (!primaryToWrite.isEmpty())
					totalAddedMethods += 1;
			}
			
			// routinely clear writer
			bw.flush();
			
			/**
			 * "REMOVALS" Methods
			 */
			
			String removedMethodsUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/methods_index_removals.html";

			/* Loads HTML and grabs table entries from list */
			Document removedMethodsDoc = Jsoup.connect(removedMethodsUrl).get();

			Elements removedMethodsList = removedMethodsDoc.getElementsByClass("hiddenlink");
			
			for (Element removedMethod : removedMethodsList) {
				
				String removedMethodName = removedMethod.text();
				String removedMethodLink = removedMethod.attr("href");
				String primaryToWrite = versionNum  + ",Method,\""+ removedMethodName + "\",Removals\n"; // alters the output string
				
				bw.write(primaryToWrite);
				
				if (!primaryToWrite.isEmpty())
					totalRemovedMethods += 1;
			}
			
			// routinely clear writer
			bw.flush();
			
			/**
			 * Summary Statistic
			 */
			System.out.println("v" + versionNum + ":");
			System.out.println("# Added Methods: " + totalAddedMethods);
			System.out.println("# Changed Methods: " + totalChangedMethods);
			System.out.println("# Removed Methods: " + totalRemovedMethods);
		}

		bw.flush();
		bw.close();
		System.out.println("TASK COMPLETED!");
	}

}