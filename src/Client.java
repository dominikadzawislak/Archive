import java.awt.EventQueue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Client {
	public static Scanner scanner;
	public static Socket socket;
	public static String fileName;
	public static String fileLocation;
	public static String ipAddress;
	public static String portNoS;
	public static int portNo;
	public static long size;
	public static int sizeR;
	public static File file;
	public static String response;
	public static String listString = "";
	public static List<File> listOfFilesD;
	public static ArrayList<String> list;
	public static OutputStream outputStream;
	public static ArchiveFrame window;

	public static void openSocket(String ipAddress, int portNo) throws IOException {
		try {
			socket = new Socket(ipAddress, portNo);
			System.out.println("connected.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeSocket(String ipAddress, int portNo) throws IOException {
		try {
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	public static boolean validIP(String ipAddress) {
		try {
			if (ipAddress == null || ipAddress.isEmpty()) {
				return false;
			}

			String[] parts = ipAddress.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ipAddress.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		ipAddress = JOptionPane.showInputDialog("Enter ipAddress of machine :");

		validIP(ipAddress);
		int exit = JOptionPane.showConfirmDialog(null, "Are you sure?", null, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
		if (exit == JOptionPane.YES_OPTION) {
			while (validIP(ipAddress) == false) {
				ipAddress = JOptionPane.showInputDialog("Wrong ipAddress, enter new ipAddress of machine :");

				if (exit == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		}

		portNoS = JOptionPane.showInputDialog("Enter port number of machine(e.g. '5555') :");
		portNo = Integer.valueOf(portNoS);
		scanner = new Scanner(System.in);
		Client fc = new Client(ipAddress, portNo);
	}

	public Client(String ipAddress, int portNo) throws IOException {
		try {

			socket = new Socket(ipAddress, portNo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		runFrame();
	//	closeSocket(ipAddress,portNo);

	}

	public static void runFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new ArchiveFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void showArchiveEvent() throws IOException {
		sendShowArchiveRequest();
		getResponse();

	}

	public static void saveFileEvent(String filePath) throws IOException {

		fileLocation = filePath;
		sendSaveRequest();
		getResponse();
		if (response.equals("notexist")) {
			send(ipAddress, portNo, fileLocation);
			getResponse();
		} else if (response.equals("exists")) {
			window.textArea.append("File already exists on a server\n");

		}
	}

	public static void deleteFileEvent(String fileNameD) throws IOException {
		sendDeleteRequest(fileNameD);
		getResponse();

	}

	public static void sendZipFileEvent(String fileNameD) throws IOException {
		sendZipFileRequest(fileNameD);
		getResponse();
	}

	public static void sendReturnFileEvent(String fileNameD) throws IOException {

		sendReturnFileRequest(fileNameD);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String request = br.readLine();
		String[] parts = request.split("-");
		fileName = parts[0];
		String sizeS = parts[1];
		sizeR = Integer.valueOf(sizeS);
		receiveReturnedFile(fileLocation, sizeR);
		getResponse();
		window.textArea.append(response);
		
	}

	private static void getResponse() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		while ((response = br.readLine()) != null) {
			System.out.println(response);
			window.textArea.append(response+"\n");
			break;
		}
	}

	private static void sendSaveRequest() throws IOException {
		File file = new File(fileLocation);
		fileName = file.getName();
		size = file.length();
		String sizeS = String.valueOf(size);
		long date = file.lastModified();
		String dateS = String.valueOf(date);

		window.textArea.append("Sending save request.\n");
		window.textArea.append("name:" + fileName + "\n");
		window.textArea.append("size:" + sizeS + "\n");

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println("savefile-" + fileName + "-" + sizeS + "-" + dateS);
		pw.flush();
	}

	private static void sendDeleteRequest(String fileName) throws IOException {

		String fileNameD = fileName;

		int sizeS = 0;
		String dateS = "1";

		String text = ("Sending delete request for:" + fileNameD);
		JOptionPane.showMessageDialog(null, text);

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println("deletefile-" + fileNameD + "-" + sizeS + "-" + dateS);
		pw.flush();
	}

	private static void sendZipFileRequest(String fileName) throws IOException {
		String fileNameD = fileName;
		;

		int sizeS = 0;
		String dateS = "1";

		String text = ("Sending zip request for:" + fileNameD);
		JOptionPane.showMessageDialog(null, text);

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println("zipfile-" + fileNameD + "-" + sizeS + "-" + dateS);
		pw.flush();
	}

	private static void sendShowArchiveRequest() throws IOException {
		String fileNameD = null;
		int sizeS = 0;
		String dateS = "1";

		String text = ("Sending showarchive request.");
		JOptionPane.showMessageDialog(null, text);

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println("showarchive-" + fileNameD + "-" + sizeS + "-" + dateS);
		pw.flush();
	}

	private static void sendReturnFileRequest(String fileName) throws IOException {

		String fileNameD = fileName;
		int sizeS = 0;
		String dateS = "1";

		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os, true);
		pw.println("returnfile-" + fileNameD + "-" + sizeS + "-" + dateS);
		pw.flush();
	}

	public static void send(String ipAddress, int portNo, String fileLocation) throws IOException {
		int partCounter = 1;
		int sizeOfFiles = 1024 * 1024;// 1MB
		byte[] buffer = new byte[sizeOfFiles];
		File file = new File(fileLocation);

		String fileName = file.getName();
		list = new ArrayList<String>();
		listOfFilesD = new ArrayList<File>();

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

	public static void delete() throws IOException {
		for (String s : list) {
			listString += s + "\t";
		}

		window.textArea.append(listString + "\n");
		String fname;
		Path p = Paths.get(fileLocation);
		Path folder = p.getParent();

		for (int i = 0; i < list.size(); i++) {
			fname = list.get(i);
			window.textArea.append(fname);

			String textPath = (folder + "\\" + fname);
			Path path = Paths.get(textPath);
			try {
				Files.delete(path);
			} catch (NoSuchFileException x) {
				System.err.format("%s: no such" + " file or directory%n", path);
			} catch (DirectoryNotEmptyException x) {
				System.err.format("%s not empty%n", path);
			} catch (IOException x) {
				System.err.println(x);
			}
		}
	}

	public static void receiveReturnedFile(String fileLocation, int sizeR) throws IOException {
		window.textArea.append("Saving file...\n");
		fileLocation = ("C:\\Users\\Dominika\\Desktop\\JAVAR\\" + fileName);
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(fileLocation);
		byte[] buffer = new byte[sizeR];

		window.textArea.append("rozmiar" + sizeR);
		window.textArea.append("Nazwa" + fileLocation);
		int read = 0;
		int totalRead = 0;
		int remaining = sizeR;
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

	}

}
