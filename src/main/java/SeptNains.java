// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;


public class SeptNains {
    public static void main(String[] args) {
        Object verrou = new Object();

        int nbNains = 7;
        String nom [] = {" Simplet ", " Dormeur ",  " Atchoum ", " Joyeux  ", "Grincheux",
                "  Prof   ", " Timide  "};
        Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
        for(int i = 0; i < nbNains; i++) nain[i].start();

        for(int i = 0; i < 20; i++) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            synchronized (nain[0].bn) {
                System.out.println("notifyyyyyyyyy");
                nain[0].bn.notifyAll();
            }
        }

        /* Attendre 5s. avant d'interrompre chaque nain */
        try { sleep(5_000); } catch (InterruptedException e) {e.printStackTrace();}

        /* Interrompre chaque nain, un à un */
        for(int i = 0; i < nbNains; i++) {
            nain[i].interrupt();
        }

        /* Attendre la terminaison de chaque nain, l'un après l'autre */
        for(int i = 0; i < nbNains; i++){
            try { nain[i].join(); } catch (InterruptedException e) {e.printStackTrace();}
        }

        /* Afficher le message final */
        System.out.println("Tous les nains ont terminé.");
    }
}

class BlancheNeige {
    private final boolean verbeux = false;  // Pour afficher éventuellement le contenu de la liste
    public ArrayList<Thread> liste = new ArrayList<Thread>();
    private SimpleDateFormat sdf = new SimpleDateFormat("hh'h 'mm'mn 'ss','SSS's'");

    public synchronized void requerir() {   // C'est simplement s'inscrire à la fin de la liste
        liste.add(Thread.currentThread());
        Date date = new Date(System.currentTimeMillis());
        System.out.println("[" + sdf.format(date) + " " + Thread.currentThread() + "] "
                + "Je requiers un accès exclusif à la ressource.");
        if (verbeux) System.out.println("\t\t" + liste.toString());
    }

    public synchronized void acceder() throws InterruptedException {
        // Le droit d'accéder à la ressource correspond  au fait d'être le premier dans la liste

        while( liste.get(0) != Thread.currentThread()) {
            Date date = new Date(System.currentTimeMillis());
            System.out.println("[" + sdf.format(date) + " " + Thread.currentThread() + "] " + "Eh alors !");
            wait(1000);                            // Le nain s'endort sur le moniteur Blanche-Neige
        }

        Date date = new Date(System.currentTimeMillis());
        System.out.println("[" + sdf.format(date) + " " + Thread.currentThread() + "] "
                + "\t\t J'ai obtenu le privilège!");
        if (verbeux) System.out.println("\t\t\t\t" + liste.toString());
    }

    public synchronized void relacher(){
        notifyAll();
        liste.remove(0);
        // Le nain s'efface de la liste: il cède ainsi son privilège au suivant.
        Date date = new Date(System.currentTimeMillis());
        System.out.println("[" + sdf.format(date) + " " + Thread.currentThread() + "] "
                + "\t\t\t Je relâche le privilège.");
        if (verbeux) System.out.println("\t\t\t\t" + liste.toString());
    }
}

class Nain extends Thread {
    final static BlancheNeige bn = new BlancheNeige();

    public Nain(String nom) {
        this.setName(nom);
    }

    public void run() {
        while(true) {
            try {
                bn.requerir();
                bn.acceder();
                sleep(2_500);  // Maintien de l'accès exclusif pendant 2,5 s.
                bn.relacher();
            } catch (InterruptedException e) { break ;}
        }
        System.out.println(getName() + " s'en va!");
    }

    public String toString(){
        // Permet un affichage simple de la liste d'attente par liste.toString()
        return getName();
    }
}

/* Fonctionnement du programme fourni.
  $ make
  javac *.java
  $ java SeptNains
  [12h 20mn 20,214s  Simplet ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s  Simplet ] 		 J'ai obtenu le privilège!
  [12h 20mn 20,215s  Timide  ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s   Prof   ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s Grincheux] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s  Joyeux  ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s  Atchoum ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 20,215s  Dormeur ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 22,716s  Simplet ] 			 Je relâche le privilège.
  [12h 20mn 22,717s  Simplet ] Je requiers un accès exclusif à la ressource.
  [12h 20mn 22,717s  Timide  ] 		 J'ai obtenu le privilège!
  [12h 20mn 25,220s  Timide  ] 			 Je relâche le privilège.
  [12h 20mn 25,220s  Timide  ] Je requiers un accès exclusif à la ressource.
   Timide   s'en va!
  [12h 20mn 25,220s   Prof   ] 		 J'ai obtenu le privilège!
    Prof    s'en va!
  Grincheux s'en va!
   Atchoum  s'en va!
   Simplet  s'en va!
   Joyeux   s'en va!
   Dormeur  s'en va!
  Tous les nains ont terminé.
  $
*/


/* Fonctionnement attendu du programme complété
   $ make
   javac *.java
   $ java SeptNains
   [12h 39mn 37,627s  Simplet ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,628s  Simplet ] 		 J'ai obtenu le privilège!
   [12h 39mn 37,628s  Timide  ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,628s   Prof   ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,628s  Joyeux  ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,628s Grincheux] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,628s  Atchoum ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 37,629s  Dormeur ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 38,630s  Timide  ] ET ALORS?
   [12h 39mn 38,630s  Atchoum ] ET ALORS?
   [12h 39mn 38,630s  Dormeur ] ET ALORS?
   [12h 39mn 38,630s   Prof   ] ET ALORS?
   [12h 39mn 38,630s Grincheux] ET ALORS?
   [12h 39mn 38,630s  Joyeux  ] ET ALORS?
   [12h 39mn 39,635s  Timide  ] ET ALORS?
   [12h 39mn 39,635s  Joyeux  ] ET ALORS?
   [12h 39mn 39,636s  Dormeur ] ET ALORS?
   [12h 39mn 39,636s   Prof   ] ET ALORS?
   [12h 39mn 39,636s  Atchoum ] ET ALORS?
   [12h 39mn 39,636s Grincheux] ET ALORS?
   [12h 39mn 40,133s  Simplet ] 			 Je relâche le privilège.
   [12h 39mn 40,134s  Simplet ] Je requiers un accès exclusif à la ressource.
   [12h 39mn 40,134s  Timide  ] 		 J'ai obtenu le privilège!
   [12h 39mn 40,640s Grincheux] ET ALORS?
   [12h 39mn 40,640s  Joyeux  ] ET ALORS?
   [12h 39mn 40,640s  Dormeur ] ET ALORS?
   [12h 39mn 40,640s  Atchoum ] ET ALORS?
   [12h 39mn 40,640s   Prof   ] ET ALORS?
   [12h 39mn 41,139s  Simplet ] ET ALORS?
   [12h 39mn 41,645s  Joyeux  ] ET ALORS?
   [12h 39mn 41,645s  Dormeur ] ET ALORS?
   [12h 39mn 41,646s  Atchoum ] ET ALORS?
   [12h 39mn 41,646s   Prof   ] ET ALORS?
   [12h 39mn 41,646s Grincheux] ET ALORS?
   [12h 39mn 42,144s  Simplet ] ET ALORS?
    Simplet  s'en va!
    Timide   s'en va!
    Joyeux   s'en va!
   Grincheux s'en va!
    Dormeur  s'en va!
    Atchoum  s'en va!
     Prof    s'en va!
   Tous les nains ont terminé.
 */