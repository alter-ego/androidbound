package com.alterego.androidbound.zzzztoremove;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Streams {
	public static List<String> readAllLines(InputStream stream) throws IOException {
		return readAllLines(stream, "UTF-8");
	}
	
	public static List<String> readAllLines(InputStream stream, String encoding) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

		String line = null;
		try {
			while ((line = reader.readLine()) != null) 
				lines.add(line);
		} finally {
			stream.close();
		}
		return lines;
	}
	
	public static String readAllTextSafe(InputStream stream) {
		try {
			return readAllText(stream);			
		} catch(IOException e) {
			return "";
		}
	}
	
	public static String readAllText(InputStream stream) throws IOException {
		return readAllText(stream, "UTF-8");
	}
	
	public static String readAllText(InputStream stream, String encoding) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
		StringBuilder builder = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null)
				builder.append(line + "\n");
		} finally {
			stream.close();
		}
		return builder.toString();
	}
}
