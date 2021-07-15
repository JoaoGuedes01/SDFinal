/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author joaog
 */
public class Amigo implements Serializable {

    private User user;
    private Boolean accepted;
    private ArrayList<Mensagem> listaMensagens;

    public Amigo(User user, Boolean accepted, ArrayList<Mensagem> listaMensagens) {
        this.user = user;
        this.accepted = accepted;
        this.listaMensagens = new ArrayList<Mensagem>();
    }

    public Amigo() {
        this.listaMensagens = new ArrayList<Mensagem>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public ArrayList<Mensagem> getListaMensagens() {
        return listaMensagens;
    }

    public void setListaMensagens(ArrayList<Mensagem> listaMensagens) {
        this.listaMensagens = listaMensagens;
    }

}
