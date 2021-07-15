
package rmiService;
import rmiService.Servant;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class server {
    public static void main(String[] args) throws RemoteException{
        System.setProperty("java.rmi.server.hostname","192.168.1.8");
        //System.setProperty("java.rmi.server.hostname","localhost");
        Registry reg = LocateRegistry.createRegistry(1099);
        reg.rebind("hello", new Servant());
        System.out.println("Server started");
    }
}
