/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiService;

import java.io.Serializable;
import Backend.User;
/**
 *
 * @author jonas
 */
public class netRequest implements Serializable{
    
    private User user;
    private String status;
    private String description;

    public netRequest(User user, String status, String description) {
        this.user = user;
        this.status = status;
        this.description = description;
    }
    
    public netRequest(){
    
    }
    
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
}
