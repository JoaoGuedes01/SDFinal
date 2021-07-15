/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import Frontend.frontendController;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;

/**
 *
 * @author joaog
 */
public class reqHandler extends Thread {

    //Importar o Sistema que guarda os dados da classe App
    private Sys sys = ProjetoFase2.sys;
    Socket ligacao;
    ObjectInputStream in;
    ObjectOutputStream out;

    public reqHandler(Socket ligacao) throws IOException {
        this.ligacao = ligacao;

        try {

            InputStream inputStream = ligacao.getInputStream();

            this.out = new ObjectOutputStream(ligacao.getOutputStream());

            this.in = new ObjectInputStream(ligacao.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            Mensagem pedido = (Mensagem) in.readObject();
            User usr = (User) pedido.getUser();
            System.out.println("Pedido tipo: " + pedido.getTipo());

            switch (pedido.getTipo()) {
                case "addFriend":
                    System.out.println("Pedido de amizade recebido de " + pedido.getUser().getEmail());
                    if (sys.getUserLogado().getListaAmigos().stream().anyMatch(o -> o.getUser().getEmail().equals(usr.getEmail()))) {
                        System.out.println("ja na lista de contactos");
                    } else {
                        Amigo amigo = new Amigo();
                        amigo.setUser(usr);
                        amigo.setAccepted(false);
                        sys.getUserLogado().getListaAmigos().add(amigo);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                frontendController.getInstance().promptReqNot();
                            }
                        });
                        sys.getService().updateUserInfo(sys.getUserLogado());
                    }
                    break;
                case "friendRequest":
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("Pedido de amizade aceite de " + pedido.getUser().getEmail());
                                Amigo amigo = new Amigo();
                                amigo.setUser(usr);
                                amigo.setAccepted(true);
                                sys.getUserLogado().getListaAmigos().add(amigo);
                                sys.getService().updateUserInfo(sys.getUserLogado());
                                frontendController.getInstance().promptFriendsNot();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
               

                case "msgTeste":
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Se o user estiver na página do amigo que lhe mandou msg
                                if (frontendController.getInstance().getChatEmail().equals(usr.getEmail())) {
                                    frontendController.getInstance().addMsgToScreen(pedido);
                                    for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                                        if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(usr.getEmail())) {
                                            sys.getUserLogado().getListaAmigos().get(i).getListaMensagens().add(pedido);
                                            sys.getService().updateUserInfo(sys.getUserLogado());
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                                        if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(usr.getEmail())) {
                                            frontendController.getInstance().promptFriendsNot();
                                            sys.getUserLogado().getListaAmigos().get(i).getListaMensagens().add(pedido);
                                            sys.getService().updateUserInfo(sys.getUserLogado());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;

                case "msgBoard":
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("Mensagem de Board Recebida");
                                sys.getUserLogado().getListaMensagensBoard().add(pedido);
                                sys.getService().updateUserInfo(sys.getUserLogado());
                                frontendController.getInstance().addMsgToBoard(pedido);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            // out.println("Conexao sem sucesso");
            e.printStackTrace();
        }

    }
}
