import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ClassLevelChangeDetails {

	public static void main(String[] args) throws IOException {
		/**
		 * Begin: Setup
		 */

		/* Create string and file to write output to */
		String toWrite = "";
		File file = new File("output/ClassesChanges.csv");
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

			int totalChangedClasses = 0;
			int totalAddedClasses = 0;
			int totalRemovedClasses = 0;
			
			/**
			 * "CHANGES" Classes
			 */

			String changedClassesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/classes_index_changes.html";

			/* Loads HTML and grabs table entries from list */
			Document changedClassesDoc = Jsoup.connect(changedClassesUrl).get();
			Elements changedClassesList = changedClassesDoc.getElementsByClass("hiddenlink");
						
			for (Element changedClass : changedClassesList) {
				
				totalChangedClasses += 1;
				
				String changedClassName = changedClass.text();
				String changedClassLink = changedClass.attr("href");
				
				String primaryToWrite = versionNum  + ",Class,"+ changedClassName + ",Changes"; // alters the output string

				String changedClassUrl = "https://developer.android.com/sdk/api_diff/" + versionNum + "/changes/" + changedClassLink;
				Document changedClassContent = Jsoup.connect(changedClassUrl).get();
				Element body = changedClassContent.getElementById("body-content"); // grabs only body, where all tables are

				
				int count = body.select("table").size();

				for (int index = 0; index < count; index++) {
					Element table = body.select("table").get(index);
					String changedElementModificationInfo = table.getElementsByTag("tr").get(0).text().trim();
					String changedElementModificationType = changedElementModificationInfo.substring(0, changedElementModificationInfo.indexOf(" ")).trim();
					String changedElementType = changedElementModificationInfo.substring(changedElementModificationInfo.indexOf(" ")).trim();
					
					// how many entries there are in each table
					int numEntries = table.select("tr").size();

					// Writes each entry into table
					for (int x = 1; x < numEntries; x++) {
						String entry = table.getElementsByTag("tr").get(x).text().replace(" ", "");
						entry = entry.replace(",", " ");
						toWrite = primaryToWrite + "," + changedElementType + "," + entry.trim() + "," + changedElementModificationType + "\n";
						bw.write(toWrite);
					}
						
				}
				
				// routinely clear buffer
				bw.flush();
			}

			/**
			 * "ADDITIONS" Classes
			 */
			
			String addedClassesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/classes_index_additions.html";

			/* Loads HTML and grabs table entries from list */
			Document addedClassesDoc = Jsoup.connect(addedClassesUrl).get();

			Elements addedClassesList = addedClassesDoc.getElementsByClass("hiddenlink");
			
			for (Element addedClass : addedClassesList) {
				
				totalAddedClasses += 1;
				String addedClassName = addedClass.text();
				String addedClassLink = addedClass.attr("href");
				String primaryToWrite = versionNum  + ",Class,"+ addedClassName + ",Additions\n"; // alters the output string
				
				bw.write(primaryToWrite);
			}
			
			// routinely clear writer
			bw.flush();
			
			/**
			 * "REMOVALS" Classes
			 */
			
			String removedClassesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/classes_index_removals.html";

			/* Loads HTML and grabs table entries from list */
			Document removedClassesDoc = Jsoup.connect(removedClassesUrl).get();

			Elements removedClassesList = removedClassesDoc.getElementsByClass("hiddenlink");
			
			for (Element removedClass : removedClassesList) {
				
				totalRemovedClasses += 1;
				String removedClassName = removedClass.text();
				String removedClassLink = removedClass.attr("href");
				String primaryToWrite = versionNum  + ",Class,"+ removedClassName + ",Removals\n"; // alters the output string
				
				bw.write(primaryToWrite);
			}
			
			// routinely clear writer
			bw.flush();
			
			/**
			 * Summary Statistic
			 */
			System.out.println("v" + versionNum + ":");
			System.out.println("# Added Classes: " + totalAddedClasses);
			System.out.println("# Changed Classes: " + totalChangedClasses);
			System.out.println("# Removed Classes: " + totalRemovedClasses);
		}

		bw.flush();
		bw.close();
		System.out.println("TASK COMPLETED!");
	}
}
