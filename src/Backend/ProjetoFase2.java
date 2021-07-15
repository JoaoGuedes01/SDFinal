/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import rmiService.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author joaog
 */
public class ProjetoFase2 extends Application {

    public static Sys sys = new Sys();

    @Override
    public void start(Stage stage) throws IOException {
        //System.setProperty("java.rmi.server.hostname","161.35.56.90");
        //Service service = (Service) Naming.lookup("rmi://161.35.56.90:1099/hello");

        Parent root = FXMLLoader.load(getClass().getResource("/Frontend/Frontend.fxml"));

        Scene scene = new Scene(root, 1091, 627);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        try {
            System.getProperties().put( "java.security.policy", "../rmiService/server.policy");
            //Conexão ao servidor local do próprio computador em localhost
            //System.setProperty("java.rmi.server.hostname", "localhost");
            //Service service = (Service) Naming.lookup("rmi://localhost:1099/hello");
            //Conexão ao servidor da Virtual Machine
           /* System.setProperty("java.rmi.server.hostname","161.35.56.90");
            Service service = (Service) Naming.lookup("rmi://161.35.56.90:1099/hello");*/
            //Conexão ao servidor hospedado no computador num computador local da equipa
            System.setProperty("java.rmi.server.hostname","192.168.1.4");
            Service service = (Service) Naming.lookup("rmi://192.168.1.4:1099/hello");
            sys.setService(service);
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
