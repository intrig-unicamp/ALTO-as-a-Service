package intrig.AaaS.Util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class CsvFileWriter {

	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";	

	public void writeCsvFile(String fileName, List<String> lstAS,
			boolean bolIsCOMMA_DELIMITER) {
		BufferedWriter fileWriter = null;

		try {

			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(
					fileName));
			fileWriter = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
			
			String strIsDelimiter = bolIsCOMMA_DELIMITER ? COMMA_DELIMITER
					: NEW_LINE_SEPARATOR;

			for (int i = 0; i < lstAS.size(); i++) {
				fileWriter.append(lstAS.get(i));
				if (i < lstAS.size() - 1)
					fileWriter.append(strIsDelimiter);
			}

			System.out.println("File was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in file !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out
						.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}

	public void writeCsvFile(String fileName,
			HashMap<String, List<String>> lstAS) {
		BufferedWriter fileWriter = null;

		try {
			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(
					fileName));
			fileWriter = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));

			Iterator<Map.Entry<String, List<String>>> entries = lstAS
					.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, List<String>> entry = entries.next();
				String key = entry.getKey();
				List<String> value = entry.getValue();

				fileWriter.append(key);
				if (value.size() > 0)
					fileWriter.append(COMMA_DELIMITER);

				for (int i = 0; i < value.size(); i++) {
					fileWriter.append(String.valueOf(value.get(i)));
					if (i < value.size() - 1)
						fileWriter.append(COMMA_DELIMITER);
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			System.out.println("CSV file was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out
						.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}

	public void writeCsvFileInt(String fileName, HashMap<Integer, Integer> lstAS) {
		BufferedWriter fileWriter = null;

		try {
			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(
					fileName));
			fileWriter = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));

			Iterator<Map.Entry<Integer, Integer>> entries = lstAS.entrySet()
					.iterator();
			while (entries.hasNext()) {
				Map.Entry<Integer, Integer> entry = entries.next();
				Integer key = entry.getKey();
				Integer value = entry.getValue();

				fileWriter.append(key.toString());
				// if (value.size() > 0)
				fileWriter.append(COMMA_DELIMITER);

				// for (int i = 0; i < value.size(); i++) {
				fileWriter.append(value.toString());
				// if (i < value.size() - 1)
				// fileWriter.append(COMMA_DELIMITER);
				// }
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			System.out.println("CSV file was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out
						.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}
}
