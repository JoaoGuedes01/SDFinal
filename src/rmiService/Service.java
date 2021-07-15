package rmiService;

import Backend.Mensagem;
import java.rmi.RemoteException;
import java.rmi.Remote;
import Backend.User;
import java.util.ArrayList;
public interface Service extends Remote{
    public void newUser() throws RemoteException;
    public Integer numberCon() throws RemoteException;
    public void registerUser(User user) throws RemoteException;
    public String verifyUser(User user) throws RemoteException;
    public netRequest loginUser(String email, String pass) throws RemoteException;
    public ArrayList<User> returnAllContacts()throws RemoteException;
    public void updateUserInfo(User user) throws RemoteException;
    public User returnUser(String email)throws RemoteException;
    public void addMessageToPrivateArray(Mensagem msg, String email)throws RemoteException;
    public void addMessageToPrivateBoardArray(Mensagem msg, String email)throws RemoteException;
}
