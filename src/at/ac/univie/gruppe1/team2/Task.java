package at.ac.univie.gruppe1.team2;

/**
 * Created by max on 06.05.15.
 */
public class Task {

    private float Task_id;
    private String Ersteller;
    private String Name;
    private String Beschreibung;
    private String Anleitung;
    private String art;
    private String ort;
    private int zeit;
    private int level;
    private int Punkte;
    private int likes;

    public float getTask_id() {
        return Task_id;
    }

    public void setTask_id(float task_id) {
        Task_id = task_id;
    }

    public String getErsteller() {
        return Ersteller;
    }

    public void setErsteller(String ersteller) {
        Ersteller = ersteller;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        Beschreibung = beschreibung;
    }

    public String getAnleitung() {
        return Anleitung;
    }

    public void setAnleitung(String anleitung) {
        Anleitung = anleitung;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public int getZeit() {
        return zeit;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public void setZeit(int zeit) {
        this.zeit = zeit;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPunkte() {
        return Punkte;
    }

    public void setPunkte(int punkte) {
        Punkte = punkte;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean taskCheck() {
        /*
        private float Task_id;
    private String Ersteller;
    private String Name;
    private String Beschreibung;
    private String Anleitung;
    private String art;
    private String ort;
    private int zeit;
    private int level;
    private int Punkte;
    private int likes;
         */
        if (getErsteller() == null) {
            return false;
        }
        if (getName() == null) {
            return false;
        }
        if (getBeschreibung() == null) {
            return false;
        }
        if (getAnleitung() == null) {
            return false;
        }
        if (getArt() == null) {
            return false;
        }
        if (getOrt() == null) {
            return false;
        }
        if (getZeit() < 1) {
            return false;
        }
        /**
         * HIER NOCH AKTUALISIEREN!
         *
         *
         if(getLevel() <1){
         return false;
         }
         */
        if (getPunkte() < 1) {
            return false;
        }

        return true;
    }
}
