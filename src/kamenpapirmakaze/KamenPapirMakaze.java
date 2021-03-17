package kamenpapirmakaze;

import java.io.IOException;

public class KamenPapirMakaze {

    /**
     * @param args argumenti prosledjeni prilikom startovanja programa, ako vam treba
     */
    public static void main(String[] args) throws IOException {
        //Kreiraj novi objekat klase Igra i zapocni igru
        Igra igra = new Igra();
        igra.igraj();
    }
    
}

//ctrl + hover misa prikazuje informacije o metodi (JavaDoc)