package common;

public class Telefon {
    private int id;
    private String nume;

    public Telefon(int id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public Telefon(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    @Override
    public String toString() {
        return "Telefon{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                '}';
    }
}
