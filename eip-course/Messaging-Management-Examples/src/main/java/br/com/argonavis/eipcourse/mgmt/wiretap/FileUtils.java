package br.com.argonavis.eipcourse.mgmt.wiretap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class FileUtils {
	
    public static byte[] readBytes(File file) {
    	InputStream in = null;
    	OutputStream out = null;
		try {
			in = new FileInputStream(file);
			byte[] bytes = new byte[(int)file.length()];
	    	in.read(bytes);
	    	return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {}
		}
    }
    
    public static String readChars(File file) {
    	Reader reader = null;
    	Writer writer = null;
		try {
			reader = new FileReader(file);
			writer = new StringWriter();
	    	char[] buffer = new char[4096];
	    	int len = reader.read(buffer);
	    	while(len > 0) {
	    		writer.write(buffer, 0, len);
	    		len = reader.read(buffer);
	    	}
	    	writer.flush();
	    	return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {}
		}
    }

	public static void saveFile(byte[] payload, File directory, String filename, String type) throws IOException {
		if(filename == null) {
			filename = generateFileName(directory, "File");
		}
		FileOutputStream out = new FileOutputStream(new File(directory, filename));
		out.write(payload);
		out.close();
	}

	public static void saveFile(String payload, File directory, String filename, String type) throws IOException {
		if(filename == null) {
			filename = generateFileName(directory, "File."+type);
		}
		FileWriter writer = new FileWriter(new File(directory, filename));
		writer.write(payload);
		writer.close();
	}
	
	public static String generateFileName(File directory, String filename) {
		File file = new File(directory, filename);
		if(file.exists()) {
			filename = filename.substring(0,filename.lastIndexOf(".")) + "1" + filename.substring(filename.lastIndexOf("."));
			file = new File(directory, filename);
			if(file.exists()) {
				filename = generateFileName(directory, filename);
			}
		}
		return filename;
	}
}
