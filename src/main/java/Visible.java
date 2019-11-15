import java.util.concurrent.atomic.AtomicInteger;

public class Visible {

    static Object verrou = new Object();

    public static void main(String[] args) throws Exception {
        A a = new A() ;        // Création d'un objet a de la classe A
        a.start() ;            // Lancement du thread a
        Thread.sleep(1000) ;
        synchronized (verrou) {
            a.valeur.getAndSet(1); // ca marche mais pas trop
            ;         // Modification de l'attribut valeur
            a.fin = true;         // Modification de l'attribut fin
        }
        System.out.println("Le main a terminé.") ;
    }
}

class A extends Thread {
    public volatile  AtomicInteger valeur = new AtomicInteger(0) ;
    public boolean fin = false ;
    public void run() {
        while(! fin) {System.out.println(valeur) ;} ;    // Attente active

    }
}

/*
  $ make
  javac *.java
  $ java Visible
  Le main a terminé.
  ^C$
*/