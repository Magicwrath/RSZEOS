package kamenpapirmakaze;

import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class Igra {
    Scanner sc;
    Random rnd;
    
    /**
     * konstuktor klase
     * kreiraju se objekti klase Scanner i Random koji se koriste kasnije u kodu
     */
    public Igra() {
        sc = new Scanner(System.in);
        rnd = new Random();
        /*
        Random klasa se, za generisanje celobrojnog podatka, koristi:
        int broj = rnd..nextInt(granica)
        nakon cega ce broj imati celobrojnu vrednost od 0 (ukljucujuci 0) do 
        granica - 1
        objekat rnd koristicete prilikom igranja runde kako bi se za svakog 
        igraca dobio kamen, papir ili makaze
        */
    }
    
    /**
     * Proverava da li postoje prethodno sacuvane igre izmedju dva igraca
     * @param ime1 prvi igrac
     * @param ime2 drugi igrac
     * @return true ako postoje rezultati, false u suprotnom
     */
    boolean postojiSacuvanaIgra(String ime1, String ime2){
        /*
        Proveri da li postoje sacuvani podaci za igrace ime 1 i ime2 
        ako ima vrati true u suprotnom vrati false (npr. nazvati fajl sa 
        sacuvanim podacima ime1-ime2.rez ili eventualno citanjem datoteke u 
        kojoj je upisan skor
        */
        //otvori direktorijum u kome se cuvaju sacuvane igre
        String sep = System.getProperty("file.separator");
        File saveDir = new File("." + sep + "saves");
        //ukoliko direktorijum za sacuvane igre ne postoji, kreiraj ga
        if(!saveDir.exists())
            saveDir.mkdir();
        //proveri da li postoji fajl sa imenom igrac1_igrac2.rez ili igrac2_igrac1.rez
        String saveName = ime1.toLowerCase() + "_" + ime2.toLowerCase() + ".rez";
        String reverseSaveName = ime2.toLowerCase() + "_" + ime1.toLowerCase() + ".rez";
        for(File files : saveDir.listFiles()) {
            if(saveName.equals(files.getName()) || reverseSaveName.equals(files.getName())) {
                System.out.println("\nPostoji sacuvana igra!");
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Trazi broj osvojenih rundi za igraca u prethodno odigranim rundama
     * @param filename ime fajla u kojem su upisani podaci o prethodno odigranim rundama
     * @param ime igraca za kojeg se traze rezultati
     * @return broj osvojenih rundi u prethodno osvojenim partijama
     */
    int pronadjiSkorZaIgraca(String filename, String ime) throws IOException {
        /*
        Procitaj fajl filename i pronadji koliki je "skor" igraca sa imenom ime
        Skor je broj dosada osvojenih rundi za tog igraca
        */
        
        /*
        U fajlu ce rezultati biti sacuvani u sledecem formatu:
        ime1 broj_poena
        ime2 broj_poena
        */
        
        //otvori fajl sa imenom filename
        File save = new File(filename);
        //kreiraj baferovani reader koji ce da cita FileInputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(
                new FileInputStream(save)));
        String lineStr;
        
        //citaj fajl dok ne pronadjes liniju sa odgovarajucim imenom
        while((lineStr = rd.readLine()) != null) {
            //tokenizuj string sa separatorom " "
            String[] infoIgraca = lineStr.split(" ");
            //proveri prvi token koji sadrzi ime igraca
            if(ime.equalsIgnoreCase(infoIgraca[0])) {
                rd.close();
                return Integer.parseInt(infoIgraca[1]);
            }
        }
        rd.close();
        return 0;
    }
    
    /**
     * Odigravanje jedne runde
     * @param igrac1 ime prvog igraca
     * @param igrac2 ime drugog igraca
     * @return Ime igraca koji je osvojio rundu, ako je rezultat runde nereseno,
     * odigrava se ponovo sve dok jedan od igraca ne osvoji rundu
     */
    String odigrajRundu(String igrac1, String igrac2){
        /*Koristi rnd objekat kako bi generisao broj koji odgovara kamen, papir
        ili makaze, za svakog igraca. Ponavljaj dok je rezultat neresen, 
        u suprotnom pobednik runde je:
        1) kamen - papir  -> pobednik igrac koji je dobio papir
        2) kamen - makaze  -> pobednik igrac koji je dobio kamen
        3) makaze - papir  -> pobednik igrac koji je dobio makaze
        */
        //na slucajan nacin generisi 0,1 ili 2 sto simbolizuje da je igrac izabrao
        //0 - kamen
        //1 - papir
        //2 - makaze
        int ruka1 = rnd.nextInt(3);
        int ruka2 = rnd.nextInt(3);
        while(ruka1 == ruka2) {
            ruka1 = rnd.nextInt(3);
            ruka2 = rnd.nextInt(3); 
        }
        String simboli[] = new String[] {"Kamen", "Papir", "Makaze"};
        System.out.println("\nRunda zapoceta");
        System.out.println(igrac1 + ": " + simboli[ruka1]);
        System.out.println(igrac2 + ": " + simboli[ruka2]);
        
        //odredi pobednika
        int pobednik = 0;
        switch(ruka1) {
            case 0:
                //prvi igrac je igrao kamen, pobedjuje ako je protivnik izabrao makaze
                if(ruka2 == 2)
                    pobednik = 1;
                else
                    pobednik = 2;
                break;
                
            case 1:
                //prvi igrac - papir, pobedjuje samo ako je drugi igrac izabrao kamen
                if(ruka2 == 0)
                    pobednik = 1;
                else
                    pobednik = 2;
                break;
                
            case 2:
                //prvi igrac - makaze, pobedjuje samo ako je drugi igrac izabrao papir
                if(ruka2 == 1)
                    pobednik = 1;
                else
                    pobednik = 2;
                break;
        }
        
        if(pobednik == 1) {
            System.out.println("Pobednik runde je: " + igrac1);
            return igrac1;
        }
        else {
            System.out.println("Pobednik runde je: " + igrac2);
            return igrac2;
        }
    }

    /**
     * Metoda koja sadrzi tok igranja igre
     */
    void igraj() throws IOException {
        /*
        1. Trazi od korisnika da unesu imena igraca koriscenjem objekta sc. Ponavljaj dok se ne unesu dva
        razlicita imena.
        2. Proveri da li postoje rezultati za prethodno unete igrace, ako da, pitaj da li da se nastavi, 
        ili da se igra nova igra. Koristite metodu postojiSacuvanaIgra
        3. Ako se nastavlja prethodna igra, pronadji skor svakog od igraca
        4. Igraj rundu za rundom (odigrajRundu) dok se ne dodje do 5 osvojenih rundi, kada je kraj igre. 
        Kada se dodje do kraja igre, ponudi novu igru ili izlaz iz programa. Ako se izlazi iz programa, 
        obrisi prethodne rezultate, ako postoje
        5. Na kraju svake runde pitaj da li se nastavlja igra, ili se prekida. U slucaju prekida, sacuvaj trenutni skor u fajlu
        kako bi se kasnije mogla nastaviti prekinuta igra
        */
        //imena igraca
        String igrac1;
        String igrac2;
        
        //skorovi za igrace
        int skor1;
        int skor2;
        
        //string u kome je sacuvana putanja fajla sacuvane igre
        String saveString = null;
        
        //unos imena prvog igraca
        System.out.println("Unesite ime prvog igraca:");
        igrac1 = sc.nextLine();
        System.out.println("Uspesno ste uneli ime igraca1: " + igrac1);
        
        //unos imena drugog igraca i provera da li su imena ista
        do {
            System.out.println("Unesite ime drugog igraca (mora se razlikovati od imena prvog igraca):");
            igrac2 = sc.nextLine();
        } while(igrac1.equalsIgnoreCase(igrac2));
        System.out.println("Uspesno ste uneli ime igraca2: " + igrac2);
        
        //provera da li postoji sacuvana igra za date igrace
        if(postojiSacuvanaIgra(igrac1, igrac2)) {
            System.out.println("\nDa li zelite da ucitate sacuvanu igru (1 - Da, 0 - Ne)?");
            int opcija = sc.nextInt();
            
            while(opcija != 0 && opcija != 1) {
                System.out.println("Nepostojeca opcija, izaberite 1 - Da, 0 - Ne");
                opcija = sc.nextInt();
            }
            
            //pronadji putanju postojeceg fajla sa sacuvanom igrom
            String sep = System.getProperty("file.separator");
            File save = new File("." + sep + "saves" + sep + igrac1.toLowerCase() +
                                 "_" + igrac2.toLowerCase() + ".rez");

            //proveri da li postoji takav save fajl ili su imena u drukcijem rasporedu u nazivu
            //u slucaju da ne postoji fajl ime1_ime2.rez, mora postojati fajl ime2_ime1.rez
            if(!save.exists())
                save = new File("." + sep + "saves" + sep + igrac2.toLowerCase() +
                                "_" + igrac1.toLowerCase() + ".rez");
            //System.out.println(save.getPath());

            //sacuvaj ime fajla u savegame promenljivu
            saveString = save.getPath();
            
            if(opcija == 1) {
                //izabrana je opcija da se ocita rezultat iz sacuvane igre
                //ocitaj skorove iz fajlova uz pomocu metode pronadjiSkorZaIgraca
                skor1 = pronadjiSkorZaIgraca(save.getPath(), igrac1);
                skor2 = pronadjiSkorZaIgraca(save.getPath(), igrac2);
                System.out.println("\nUcitani rezultat:");
                System.out.println(igrac1 + ": " + skor1);
                System.out.println(igrac2 + ": " + skor2);
            } else {
                //pocinje nova igra
                skor1 = 0;
                skor2 = 0;
            }
        } else {
            //ako nema sacuvane igre, pocni odmah novu igru
            skor1 = 0;
            skor2 = 0;
            String sep = System.getProperty("file.separator");
            //napravi ime fajla gde ce se sacuvati igra u slucaju da se izadje
            //iz igre
            saveString = "." + sep + "saves" + sep + igrac1.toLowerCase() + 
                         "_" + igrac2.toLowerCase() + ".rez";
        }
        
        //prva tri koraka su zavrsena
        //sledeci korak je igranje igre
        //igra se do 5 osvojenih rundi ili dok igraci ne izadju
        boolean endgame = false;
        while(skor1 != 5 && skor2 != 5 && !endgame) {
            int opcija = -1;
            while(opcija != 1 && opcija != 2) {
                System.out.println("\nIzaberite opciju:");
                System.out.println("1) Odigraj rundu");
                System.out.println("2) Izadji iz igre");
                opcija = sc.nextInt();
                
                if(opcija != 1 && opcija != 2)
                    System.out.println("Izabrali ste nepostojecu opciju!");
            }
            
            if(opcija == 1) {
                //odigraj rundu
                String pobednik = odigrajRundu(igrac1, igrac2);
                
                //odredi pobednika i povecaj mu skor
                if(pobednik.equals(igrac1))
                    skor1++;
                else
                    skor2++;
                
                //ispisi rezultat nakon gotove runde
                System.out.println("Trenutni rezultat -> " + skor1 + ":" + skor2);
                
                //proveri da li postoji pobednik
                if(skor1 == 5 || skor2 == 5) {
                    System.out.print("\nIgra zavrsena. Pobednik je: ");
                    System.out.println(skor1 == 5 ? igrac1 : igrac2);
                    //ponuditi novu igru, ili izlazak iz programa
                    //ako se odluci za izlazak, obrisati sacuvanu igru
                    int opcija2;
                    System.out.println("\nPoceti novu igru? (1 - Da, 0 - Ne)");
                    opcija2 = sc.nextInt();
                    while(opcija2 != 1 && opcija2 != 0) {
                        System.out.println("Uneli ste nepostojecu opciju, izaberite 1 - Da, 0 - Ne)");
                        opcija2 = sc.nextInt();
                    }
                    
                    if(opcija2 == 1) {
                        //pocinje nova igra, resetuj skor
                        skor1 = 0;
                        skor2 = 0;
                    } else {
                        //kraj igre, obrisi fajl sa sacuvanom igrom ako postoji
                        endgame = true;
                        File saveGame = new File(saveString);
                        if(saveGame.exists())
                            saveGame.delete();
                    }
                }
            } else {
                //izlazak iz igre
                endgame = true;
                //sacuvaj rezultat
                File saveGame = new File(saveString);
                //ukoliko fajl postoji, prebrisi ga
                if(saveGame.exists())
                    saveGame.delete();
                
                //kreiraj writer za upis rezultata u fajl
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(saveGame)));
                out.println(igrac1 + " " + skor1);
                out.println(igrac2 + " " + skor2);
                out.flush();
                out.close();
            }
        }
    }
    //alt+f7 ili desni klik+find usages prikazuje gde se sve koristi metoda
}
