package br.com.coder.arqprime.model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUtils {

	@SuppressWarnings("resource")
	public static String[] ler(File file) {
		
		List<String> list = new ArrayList<>();
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				list.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
			return list.toArray(new String[list.size()]);
		}
	}
}