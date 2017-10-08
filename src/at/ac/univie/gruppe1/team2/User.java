package at.ac.univie.gruppe1.team2;

import android.graphics.Bitmap;

/**
 * Created by max on 03.05.15.
 */
public class User {
    private String username;
    private String password;
    private String eimaladresse;
    private int level;
    private int punkte;
    private Bitmap profilPic;


    /*public User(String username, String password, String eimaladresse){
        setUsername(username);
        setPassword(password);
        setEimaladresse(eimaladresse);
    }*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEimaladresse() {
        return eimaladresse;
    }

    public void setEimaladresse(String eimaladresse) {
        this.eimaladresse = eimaladresse;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }

    public Bitmap getProfilPic() {
        return profilPic;
    }

    public void setProfilPic(Bitmap profilPic) {
        this.profilPic = profilPic;
    }
}
