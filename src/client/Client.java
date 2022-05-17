package client;

import common.Telefon;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private int port;
    private List<Telefon> telefonList;

    public Client(int port, List<Telefon> telefonList) {
        this.port = port;
        this.telefonList = telefonList;
    }

    public Client(int port) {
        this.port = port;
        telefonList=new ArrayList<>();
    }

    public Client(){

    }

    public void addTelefon(Telefon telefon){
        telefonList.add(telefon);
    }

    public void removeTelefon(Telefon telefon){
        telefonList.remove(telefon);
    }

    public Telefon searchTelefon(int id){
        for(Telefon telefon:this.telefonList){
            if(telefon.getId()==id){
                return telefon;
            }
        }
        return null;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Telefon> getTelefonList() {
        return telefonList;
    }

    public void setTelefonList(List<Telefon> telefonList) {
        this.telefonList = telefonList;
    }

    @Override
    public String toString() {
        return "Client{" +
                "port=" + port +
                ", telefonList=" + telefonList +
                '}';
    }
}
