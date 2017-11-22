import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ServerThread extends Thread {
	public static Socket socket;
	public static Scanner scanner;
	public static int size;
	public static long sizeR;
	public static long date;
	public static String request;
	public static String requestM;
	public static String fileName;
	public static String fileLocation;
	public static Path path = null;
	public static List<File> listOfFiles = new ArrayList<File>();
	public static ArrayList<String> list = new ArrayList<String>();
	public static String listString;

	ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			while (true) {
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String request = br.readLine();
				if (request != null) {
					System.out.println("Client request: " + request);
					processRequest(request, pw, is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processRequest(String request, PrintWriter pw, InputStream is) throws IOException {
		String[] parts = request.split("-");
		requestM = parts[0];
		fileName = parts[1];
		String sizeS = parts[2];
		String dateS = parts[3];
		size = Integer.valueOf(sizeS);
		date = Long.valueOf(dateS);
		System.out.println("File" + fileName);

		if (requestM.equals("savefile")) {
			//File f = new File("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
			 File f = new File("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
			long date1 = f.lastModified();
			System.out.println("dateC:" + date);
			long size1 = f.length();
			if (date1 == date && size1==size) {
				System.out.println("file exists");
				pw.println("exists");
				pw.flush();
			} else {
				pw.println("notexist");
				pw.flush();
				receiveFile(fileLocation, size, date);
				System.out.println("File saved.");
				pw.println("File saved.");
				pw.flush();
			}

		} else if (requestM.equals("deletefile")) {
	//		File f = new File("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
			File f = new File("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
			if (f.exists()) {
				deleteFiles(fileLocation);
				System.out.println("File deleted.");
				pw.println("File deleted.");
				pw.flush();
			} else {
				System.out.println("File doesn't exist.");
				pw.println("File doesn't exist.");
				pw.flush();
			}
		} else if (requestM.equals("zipfile")) {
		//	File f = new File("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
			File f = new File("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
			if (f.exists()) {
				zipFile(fileLocation);
				System.out.println("File ziped.");
				pw.println("File ziped.");
				pw.flush();
			} else {
				System.out.println("File doesn't exist.");
				pw.println("File doesn't exist.");
				pw.flush();
			}
		} else if (requestM.equals("showarchive")) {
		//	listOfArchive(new File("C:\\Users\\Pacia\\Desktop\\JAVA"));
			listOfArchive(new File("C:\\Users\\Dominika\\Desktop\\JAVA2"));
			System.out.println("List shown");
			pw.println("" + list);
			pw.flush();
			list.clear();
			listOfFiles.clear();
		} else if (requestM.equals("returnfile")) {
		//	File f = new File("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
			File f = new File("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
			if (f.exists()) {
				pw.println("exists");
				pw.flush();
				sendSaveRequest(fileName);
				returnFile(fileLocation);
				System.out.println("File returned");
				pw.println("File returned.");
				pw.flush();
			} else {
				pw.println("notexist");
				pw.flush();
				System.out.println("File doesn't exist.");
				pw.println("File doesn't exist.");
				pw.flush();
			}

		} else {
			System.out.println("Option doesn't exist");
		}
	}

	private void sendSaveRequest(String fileName) throws IOException {
	//	fileLocation = ("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
		File file = new File(fileLocation);
		sizeR = file.length();
		String sizeS = String.valueOf(sizeR);

		System.out.println("name:" + fileName);
		System.out.println("size:" + sizeS);

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println(fileName + "-" + sizeS);
		pw.flush();
	}

	public void receiveFile(String fileLocation, int size, long date ) throws IOException {
		System.out.println("Saving file...");
	//	fileLocation = ("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(fileLocation);
		byte[] buffer = new byte[size];

		System.out.println("rozmiar" + size);
		System.out.println("Nazwa" + fileLocation);
		int read = 0;
		int totalRead = 0;
		int remaining = size;
		try {
			while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				fos.write(buffer, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		fos.close();
		Date creationDate = new Date(date);
		FileTime time = FileTime.fromMillis(creationDate.getTime());
		Path path = Paths.get(fileLocation);
		Files.setLastModifiedTime(path,time);

	}

	public static boolean deleteFiles(String fileLocation) {
		System.out.println("Called deleteFiles");
	//	fileLocation = ("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
		File file = new File(fileLocation);

		if (file.delete()) {
			System.out.println("File deleted successfully");
		} else {
			System.out.println("Failed to delete the file");
		}
		return true;
	}

	public static List<File> listOfArchive(File catalog) {
		System.out.println(catalog.getAbsoluteFile());
		listOfFiles = new ArrayList<File>();
		list = new ArrayList<String>();
		System.out.println("List of files in archive");

		if (catalog.isDirectory()) {
			String[] subNote = catalog.list();
			for (String filename : subNote) {
				listOfFiles.add(new File(catalog.getAbsoluteFile() + "//" + filename));
				System.out.println("" + filename);
				list.add("" + filename);
			}
			for (String s : list) {
				listString += s + "\t";
			}
		}

		return listOfFiles;
	}

	public static void returnFile(String fileLocation) throws IOException {

		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		OutputStream outputStream = null;

		int partCounter = 1;
		int sizeOfFiles = 1024 * 1024;// 1MB
		byte[] buffer = new byte[sizeOfFiles];
		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
	//	fileLocation = ("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
		File file = new File(fileLocation);

		try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			outputStream = socket.getOutputStream();

			while ((bytesAmount = bis.read(buffer)) > 0) {
				System.out.println("Sending " + fileLocation + "( size: " + bytesAmount + " bytes)");
				outputStream.write(buffer, 0, bytesAmount);

			}
			outputStream.flush();
			System.out.println("Done.");
		}
	}

	public boolean zipFile(String fileLocation) throws IOException {

		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVA2\\" + fileName);
	//	fileLocation = ("C:\\Users\\Pacia\\Desktop\\JAVA\\" + fileName);
		File file = new File(fileLocation);
		FileInputStream in = new FileInputStream(file);

		String fileNameZip = "C:\\Users\\Dominika\\Desktop\\JAVA2\\" + file.getName() + ".zip";
    //    String fileNameZip = "C:\\Users\\Pacia\\Desktop\\JAVA\\" + file.getName() + ".zip";
		System.out.println("Plik zip: " + fileNameZip);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileNameZip));

		out.putNextEntry(new ZipEntry(file.getName()));

		byte[] b = new byte[1024];
		int count;

		while ((count = in.read(b)) > 0) {
			out.write(b, 0, count);
		}
		out.close();
		in.close();
		return true;
	}
}