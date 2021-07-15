/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiService;

import Backend.Amigo;
import Backend.Mensagem;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author joaog
 */
public class User implements Serializable{
    
    private String name;

    private String nick;

    private String email;

    private String course;
    
    private String password;

    private String  IP;

    private String Port;
    
    private ArrayList<Amigo> listaAmigos;
    
     private ArrayList<Mensagem> listaMensagensBoard;
    
    private String status;

    public User(String name, String nick, String email, String course, String password) {
        this.name = name;
        this.nick = nick;
        this.email = email;
        this.course = course;
        this.password = password;
        this.listaAmigos = new ArrayList<Amigo>();
        this.listaMensagensBoard = new ArrayList<Mensagem>();
    }
    
     User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String Port) {
        this.Port = Port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Amigo> getListaAmigos() {
        return listaAmigos;
    }

    public void setListaAmigos(ArrayList<Amigo> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Mensagem> getListaMensagensBoard() {
        return listaMensagensBoard;
    }

    public void setListaMensagensBoard(ArrayList<Mensagem> listaMensagensBoard) {
        this.listaMensagensBoard = listaMensagensBoard;
    }
    
    
    
    
}
