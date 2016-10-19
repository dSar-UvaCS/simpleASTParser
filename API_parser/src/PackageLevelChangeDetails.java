import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PackageLevelChangeDetails {

	public static void main(String[] args) throws IOException {

		/**
		 * Begin: Setup
		 */

		/* Create string and file to write output to */
		String toWrite = "";
		File file = new File("output/PackagesChanges.csv");
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

			/**
			 * "CHANGES" Packages
			 */

			String changedPackagesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/packages_index_changes.html";

			/* Loads HTML and grabs table entries from list */
			Document changedPackagesDoc = Jsoup.connect(changedPackagesUrl).get();
			Element changedPackagesList = changedPackagesDoc.getElementById("indexTableEntries");
			String[] changedPackages = changedPackagesList.text().split(" "); // List of changed packages

			/* Iterate through all changed Packages */
			if (!changedPackagesList.text().isEmpty()) {
				for (int i = 0; i < changedPackages.length; i++) {
					String primaryToWrite = versionNum  + ",Package,"+ changedPackages[i] + ",Changes"; // alters the output string

					String changedPackageUrl = "https://developer.android.com/sdk/api_diff/" + versionNum + "/changes/pkg_" + changedPackages[i] + ".html";
					Document changedPackage = Jsoup.connect(changedPackageUrl).get();
					Element body = changedPackage.getElementById("body-content"); // grabs only body, where all tables are

					/*
					 * Iterate through all possible tables (Changed Classes /
					 * Added Classes and Interfaces / etc.)
					 */
					// num tables there are
					int count = body.select("table").size();

					for (int index = 0; index < count; index++) {
						Element table = body.select("table").get(index);
						String elementModificationInfo = table.getElementsByTag("tr").get(0).text().trim();
						String elementModificationType = elementModificationInfo.substring(0, elementModificationInfo.indexOf(" ")).trim();
						String changedElementType = elementModificationInfo.substring(elementModificationInfo.indexOf(" ")).trim();
						
						// how many entries there are in each table
						int numEntries = table.select("tr").size();

						// Writes each entry into table
						for (int x = 1; x < numEntries; x++) {
							String entry = table.getElementsByTag("tr").get(x).text().replace(" ", "");
							toWrite = primaryToWrite + "," + changedElementType + "," + entry.trim() + "," + elementModificationType + "\n";
							bw.write(toWrite);
						}
					}
				}
				
				// routinely clear buffer
				bw.flush();
			}

			/**
			 * "ADDITIONS" Packages
			 */

			String addedPackagesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/packages_index_additions.html";

			/* Loads HTML and grabs table entries from list */
			Document addedPackagesDoc = Jsoup.connect(addedPackagesUrl).get();
			Element addedPackagesList = addedPackagesDoc.getElementById("indexTableEntries");
			// List of added packages
			String[] addedPackages = addedPackagesList.text().split(" ");
			
			if (!addedPackagesList.text().isEmpty()) {
				for (int i = 0; i < addedPackages.length; i++) {
					toWrite = versionNum  + ",Package,"+ addedPackages[i] + ",Additions\n";
					bw.write(toWrite);
					toWrite = "";
				}
			}
			
			// routinely clear writer
			bw.flush();

			/**
			 * "REMOVALS" Packages
			 */

			String removedPackagesUrl = "https://developer.android.com/sdk/api_diff/" + versionNum
					+ "/changes/packages_index_removals.html";

			/* Loads HTML and grabs table entries from list */
			Document removedPackagesDoc = Jsoup.connect(removedPackagesUrl).get();
			Element removedPackagesList = removedPackagesDoc.getElementById("indexTableEntries");
			// List of removed packages
			String[] removedPackages = removedPackagesList.text().split(" ");

			if (!removedPackagesList.text().isEmpty()) {
				for (int i = 0; i < removedPackages.length; i++) {
					toWrite = versionNum  + ",Package,"+ removedPackages[i] + ",Removals\n";
					bw.write(toWrite);
					toWrite = "";
				}
			}
			
			// routinely clear writer
			bw.flush();
			
			/**
			 * Summary Statistic
			 */
			System.out.println("v" + versionNum + " Summary");
			System.out.println("Added packages: " + addedPackages.length);
			System.out.println("Changed packages: " + changedPackages.length);
			System.out.println("Removed packages: " + removedPackages.length);

		}
		
		bw.close();
		System.out.println("TASK COMPLETED!");
	}
}
