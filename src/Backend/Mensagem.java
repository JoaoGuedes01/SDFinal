/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import java.io.Serializable;

/**
 *
 * @author joaog
 */
public class Mensagem implements Serializable{
    private String tipo;
    private String mensagem;
    private User user;

    public Mensagem(String tipo, String mensagem, User user) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.user = user;
    }
    
    public Mensagem() {

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
}
