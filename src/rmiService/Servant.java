package rmiService;

import Backend.Mensagem;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import Backend.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class Servant extends UnicastRemoteObject implements Service {

    private int numberCon = 0;
    ArrayList<User> listaUsers = new ArrayList<User>();

    String name = null;

    public Servant() throws RemoteException {
        super();
        User user1 = new User("João Guedes", "Jonas", "joaoguedes.cjp@gmail.com", "MIEGSI", "123");
        User user2 = new User("Tomás Lopes", "TommyTries", "domtomas420@gmail.com", "MIEGSI", "123");
        user1.setStatus("offline");
        user2.setStatus("offline");
        listaUsers.add(user1);
        listaUsers.add(user2);
    }

    public void addMessageToPrivateBoardArray(Mensagem msg, String email) throws RemoteException {
        for (int i = 0; i < listaUsers.size(); i++) {
            if (listaUsers.get(i).getEmail().equals(email)) {
                listaUsers.get(i).getListaMensagensBoard().add(msg);
            }
        }
    }

    public void addMessageToPrivateArray(Mensagem msg, String email) throws RemoteException {
        for (int i = 0; i < listaUsers.size(); i++) {
            if (listaUsers.get(i).getEmail().equals(email)) {
                for (int i2 = 0; i2 < listaUsers.get(i).getListaAmigos().size(); i2++) {
                    if (listaUsers.get(i).getListaAmigos().get(i2).getUser().getEmail().equals(msg.getUser().getEmail())) {
                        listaUsers.get(i).getListaAmigos().get(i2).getListaMensagens().add(msg);
                    }
                }
            }
        }
    }

    public User returnUser(String email) throws RemoteException {
        for (int i = 0; i < listaUsers.size(); i++) {
            if (listaUsers.get(i).getEmail().equals(email)) {
                return listaUsers.get(i);
            }
        }
        return null;
    }

    public void newUser() throws RemoteException {
        System.out.println("New Server connection");
        numberCon += 1;
    }

    public void updateUserInfo(User user) throws RemoteException {
        System.out.println("guardando user");
        for (int i = 0; i < listaUsers.size(); i++) {
            if (listaUsers.get(i).getEmail().equals(user.getEmail())) {
                listaUsers.set(i, user);
                System.out.println(listaUsers.get(i).getListaAmigos().size());
            }
        }
    }

    public String verifyUser(User user) throws RemoteException {
        System.out.println("-----------------------conexao recebida-----------------------");
        if (listaUsers.size() > 0) {
            for (int i = 0; i < listaUsers.size(); i++) {
                if (listaUsers.get(i).getEmail().equals(user.getEmail())) {
                    System.out.println("existe");
                    return "alreadyExists";
                } else {
                    System.out.println("nao existe");
                }
            }
        }
        System.out.println("nao existe");
        return "ok";

    }

    public netRequest loginUser(String email, String pass) throws RemoteException {
        String state = null;
        Integer index = null;
        if (listaUsers.size() > 0) {
            for (int i = 0; i < listaUsers.size(); i++) {
                if (listaUsers.get(i).getEmail().toString().equals(email)) {
                    if (listaUsers.get(i).getPassword().equals(pass)) {
                        System.out.println("credenciais corretas");
                        state = "correct";
                        index = i;
                        break;
                    } else {
                        System.out.println("wrongCredentials");
                        state = "not correct";
                        break;
                    }
                } else {
                    System.out.println("noUserFound");
                    state = "not correct";
                }
            }

            switch (state) {
                case "correct":
                    netRequest res = new netRequest(listaUsers.get(index), "200", "ok");
                    return res;
                case "not correct":
                    netRequest res1 = new netRequest();
                    res1.setStatus("501");
                    res1.setDescription("wrongCredentials");
                    return res1;

                case "not exists":
                    System.out.println("not exists");

                    netRequest res2 = new netRequest();
                    res2.setStatus("500");
                    res2.setDescription("noUserFound");
                    return res2;
            }
        }
        System.out.println("noUserFoundDef");
        netRequest res = new netRequest();
        res.setStatus("500");
        res.setDescription("noUserFound");
        return res;

    }

    public void registerUser(User user) {
        listaUsers.add(user);
        System.out.println("User " + user.getEmail() + " registado ");
    }

    public ArrayList<User> returnAllContacts() {
        return listaUsers;
    }

    public Integer numberCon() throws RemoteException {
        return numberCon;
    }

}
