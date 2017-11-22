
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Server extends Thread {
	private ServerSocket ss;
	public static Scanner scanner;
	public static String portNoS;
	public static int portNo;

	public static void main(String[] args) {
		portNoS = JOptionPane.showInputDialog("Enter port number of machine(e.g. '5555') :");
		portNo = Integer.valueOf(portNoS);
		scanner = new Scanner(System.in);
		new Server().runServer();
	}

	public void runServer() {

		try {
			ServerSocket serverSocket = new ServerSocket(portNo);
			System.out.println("Ready for connections");
			while (true) {
				Socket socket = serverSocket.accept();
				new ServerThread(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}