package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.Client;
import common.Telefon;


public class Server implements AutoCloseable {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Map<Socket, Client> clients = Collections.synchronizedMap(new HashMap<Socket, Client>());
    private Map<Integer, Integer> keylist = Collections.synchronizedMap(new HashMap<Integer, Integer>());

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        executorService = Executors.newFixedThreadPool(50 * Runtime.getRuntime().availableProcessors());
        executorService.execute((Runnable) () -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream());

                    executorService.submit((Runnable) () -> {

                        clients.put(socket, new Client(socket.getPort()));
                        while (!socket.isClosed()) {

                            printeazaChei(in, out);

                            try {
                                String comanda = in.readLine();

                                if (comanda.split(",")[0].equals("create")) {//create, id, nume
                                    int id = Integer.parseInt(comanda.split(",")[1]);
                                    String nume = comanda.split(",")[2];

                                    createTelefon(id,nume,out,socket);

                                } else if (comanda.split(",")[0].equals("search")) {//search,id
                                    int id = Integer.parseInt(comanda.split(",")[1]);

                                    searchTelefon(id,out);

                                } else if (comanda.split(",")[0].equals("delete")) { //delete,id
                                    int id = Integer.parseInt(comanda.split(",")[1]);
                                    int idClient = socket.getPort();

                                    deleteTelefon(id,idClient,out);

                                } else if (comanda.split(",")[0].equals("transfer")) { //transfer,id
                                    int id = Integer.parseInt(comanda.split(",")[1]);
                                    int idRequester = socket.getPort();

                                    transferTelefon(id,idRequester,out);
                                }else{//comenzi gresite
                                    out.println("Comanda introdusa nu exista!");
                                    out.flush();
                                }
                            } catch (Exception e) {
                                out.println(e.getMessage());
                                out.flush();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void transferTelefon(int id, int idRequester, PrintWriter out) {

        if (keylist.containsKey(id)) {
            int idDetinator = keylist.get(id);
            for (Map.Entry<Socket, Client> client : clients.entrySet()) {
                if (client.getValue().getPort() == idDetinator) {
                    //stergem din lista detinatorului
                    //adaugam in lista clientului
                    //actualizam keylist-ul

                    Telefon telefonCerut = client.getValue().searchTelefon(id);
                    if (telefonCerut != null) {
                        for (Map.Entry<Socket, Client> client2 : clients.entrySet()) {
                            if (client2.getValue().getPort() == idRequester) {
                                client2.getValue().addTelefon(telefonCerut);
                                client.getValue().removeTelefon(telefonCerut);
                                keylist.put(id, idRequester);
                                out.println("Telefonul a fost transferat cu succes!");
                                out.flush();
                                break;
                            }
                        }

                    } else {
                        out.println("Telefonul nu a fost gasit");
                        out.flush();
                    }
                    break;
                }
            }
        } else {
            out.println("Cheia nu exista!");
            out.flush();
        }
    }

    private void deleteTelefon(int id, int idClient, PrintWriter out) {
        if (keylist.containsKey(id)) {

            boolean hasPhone = false;
            for (Map.Entry<Socket, Client> client : clients.entrySet()) {
                if (client.getValue().getPort() == idClient) {
                    for (Telefon telefon : client.getValue().getTelefonList()) {
                        if (telefon.getId() == id) {
                            client.getValue().removeTelefon(telefon);
                            keylist.remove(id);
                            hasPhone = true;
                            out.println("Telefonul cu id-ul " + id + " a fost sters cu succes!");
                            out.flush();
                            break;
                        }
                    }
                    break;
                }
            }
            if (hasPhone == false) {
                out.println("Nu poti sterge telefonul altui client!");
                out.flush();
            }
        } else {
            out.println("Cheia nu exista!");
            out.flush();
        }
    }

    private void searchTelefon(int id, PrintWriter out) {
        if (keylist.containsKey(id)) {
            int idClient = keylist.get(id);
            for (Map.Entry<Socket, Client> client : clients.entrySet()) {
                if (client.getValue().getPort() == idClient) {
                    for (Telefon telefon : client.getValue().getTelefonList()) {
                        if (telefon.getId() == id) {
                            out.println(telefon);
                            out.flush();
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            out.println("Cheia nu exista!");
            out.flush();
        }
    }

    private void createTelefon(int id, String nume, PrintWriter out, Socket socket) {
        if (!keylist.containsKey(id)) {
            keylist.put(id, socket.getPort());
            clients.get(socket).addTelefon(new Telefon(id, nume));
            out.println("Telefonul cu id-ul " + id + " a fost adaugat cu succes!");
            out.flush();
        } else {
            out.println("Cheia este deja folosita.");
            out.flush();
        }
    }

    private void printeazaChei(BufferedReader in, PrintWriter out) {
        out.println("Lista de chei disponibile: " + keylist.toString()); //aici e o comanda
        out.flush();
    }


    @Override
    public void close() throws Exception {
        serverSocket.close();
    }

}