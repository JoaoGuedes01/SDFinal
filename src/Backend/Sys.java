/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import java.util.ArrayList;
import rmiService.Service;

/**
 *
 * @author joaog
 */
public class Sys {
    
    private User userLogado;
    private Service service;

    public Sys() {
        this.userLogado = new User();
    }

    public User getUserLogado() {
        return userLogado;
    }

    public void setUserLogado(User userLogado) {
        this.userLogado = userLogado;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    
    
  
}
