package server;

import java.util.ResourceBundle;
import java.util.Scanner;

public class Program {
	private static final int PORT=8080;
	public static void main(String[] args) {

		try (Server server = new Server()) {
			server.start(PORT);
			System.out.println(String.format("Server running on port %d, type 'exit' to close", PORT));
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if (command == null || "exit".equals(command)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}