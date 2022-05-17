package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Program {
	private static final int PORT=8080;
	private static final String HOSTNAME="127.0.0.1";

	public static void main(String[] args) {

		try (Socket socket = new Socket(HOSTNAME, PORT)) {
			System.out.println("Connected to server..");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			try(Scanner scanner = new Scanner(System.in)) {
				while(true) {
					String responseLista = in.readLine();
					if(responseLista!=null) {
						System.out.println(responseLista);
					}

					String command = scanner.nextLine();
					out.println(command);
					out.flush();
					String response = in.readLine();
					System.out.println(response);	
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}