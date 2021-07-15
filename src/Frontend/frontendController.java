package Frontend;

import Backend.Amigo;
import Backend.Mensagem;
import Backend.ProjetoFase2;
import Backend.Sys;
import Backend.User;
import Backend.reqHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import rmiService.netRequest;

public class frontendController implements Initializable {

    //Importar o Sistema de Dados
    Sys sys = ProjetoFase2.sys;

    //Variaveis necessarias para arrastar a janela UNDECORATED
    double x, y;

    @FXML
    private Pane LoginPane;

    @FXML
    private Button login;

    @FXML
    private Button registar;

    @FXML
    private Pane ListaPane;

    @FXML
    private Button PedidosBtn;

    @FXML
    private Button ListaAmigosBtn;

    @FXML
    private Button pendentesBtn;

    @FXML
    private Pane ListaAmigosPane;

    @FXML
    private Pane PedidosPane;

    @FXML
    private Pane DisponiveisPane;

    @FXML
    private Pane TitlePane;

    @FXML
    private AnchorPane afterloginPane;

    @FXML
    private Pane ChatPane;

    @FXML
    private Button fecharChatPane;

    @FXML
    private Pane DefinicoesPane;

    @FXML
    private Button guardarDefinicoes;

    @FXML
    private Button cancelarDefinicoes;

    @FXML
    private Pane MuralPane;

    @FXML
    private Button definicoesBtn;

    @FXML
    private Button adicionarPaneBtn;

    @FXML
    private GridPane BoardGrid;

    @FXML
    private Pane AddPane;

    @FXML
    private Button adicionarAddPaneBtn;

    @FXML
    private Button cancelarAddPaneBtn, msgsend_btn;

    //Guedes Tomas
    @FXML
    private TextField loginNick, registerName, registerNick, registerEmail, registerCurso, defName, defNick, defEmail, defCurso, defIP, defPort, msg_txtfld, boardMsg_TF;

    @FXML
    private PasswordField loginPass, registerPass;

    @FXML
    private Label regError, nickLabel, chatNick, chatEmail, chatState;

    @FXML
    private GridPane dispSP, pedidosGrid, amigosGrid, messengerGrid;

    @FXML
    private ImageView notReq, notFriends, notDisp, userStateImg;

    private static frontendController instance;

    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ola");
        showLoginPane();
    }

    public void clearReqNot() {
        notReq.setOpacity(0);
    }

    public void clearFriendsNot() {
        notFriends.setOpacity(0);
    }

    public void clearDispNot() {
        notDisp.setOpacity(0);
    }

    public void promptReqNot() {
        if (!(sys.getUserLogado().getStatus().equals("Do not disturb"))) {
            notReq.setOpacity(1);
        }
    }

    public void promptFriendsNot() {
        if (!(sys.getUserLogado().getStatus().equals("Do not disturb"))) {
            notFriends.setOpacity(1);
        }
    }

    public void promptDispNot() {
        if (!(sys.getUserLogado().getStatus().equals("Do not disturb"))) {
            notDisp.setOpacity(1);
        }
    }

    //Funçoes de instancia
    public frontendController() {
        instance = this;
    }

    public static frontendController getInstance() {
        return instance;
    }

    @FXML
    private void loginButton() {
        try {
            String userNick = loginNick.getText();
            String userPass = loginPass.getText();
            System.out.println("Login: nick - " + userNick + " pass - " + userPass);
            /*
            Ligar ao servidor rmi, verificar que a conta existe e se as credenciais estao bem, se sim login, senao poopoo.
             */
            netRequest serverRes = sys.getService().loginUser(userNick, userPass);
            if (serverRes.getStatus().equals("200")) {
                User user = serverRes.getUser();
                sys.setUserLogado(user);
                startSocketServerLogin();
                nickLabel.setText(sys.getUserLogado().getNick());
                atualizarContactos();
                getAllContactos();
                clearReqNot();
                clearFriendsNot();
                clearDispNot();
                setStateOnline();
                contactUpdater();
                updateBoard();
                showMural();
            } else {
                System.out.println("algo de errado nao esta certo crl");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerButton() {
        try {

            String regName = registerName.getText();
            String regNick = registerNick.getText();
            String regEmail = registerEmail.getText();
            String regCurso = registerCurso.getText();
            String regPass = registerPass.getText();

            //Nova conecão no rmi server
            sys.getService().newUser();

            User user = new User(regName, regNick, regEmail, regCurso, regPass);
            System.out.println(sys.getService().verifyUser(user));
            if (sys.getService().verifyUser(user).equals("ok")) {
                sys.setUserLogado(user);
                startSocketServerReg();
                getAllContactos();
                nickLabel.setText(sys.getUserLogado().getNick());
                clearReqNot();
                clearFriendsNot();
                clearDispNot();
                setStateOnline();
                contactUpdater();
                showMural();
            } else {
                regError.setText("O email já existe");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void getAllContactos() {
        try {
            dispSP.getChildren().clear();
            ArrayList<User> listaContactos = sys.getService().returnAllContacts();
            for (int i = 0; i < listaContactos.size(); i++) {
                Integer count = i;
                if (!(listaContactos.get(i).getEmail().equals(sys.getUserLogado().getEmail()))) {
                    if (!(sys.getUserLogado().getListaAmigos().stream().anyMatch(o -> o.getUser().getEmail().equals(listaContactos.get(count).getEmail())))) {
                        User user = listaContactos.get(i);
                        if (user.getStatus().equals("online")) {
                            //Utilizadores da app que estão online
                            Button btn = new Button(user.getNick());
                            Button btnA = new Button("+");
                            btn.setId("ContactBtn");
                            btnA.setId("btnA");
                            btn.getStylesheets().add("styles/style.css");
                            btnA.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            dispSP.add(btnA, 1, rows);
                            btn.setStyle("-fx-text-fill: green");
                            btnA.setOnMouseClicked(event -> {
                                try {
                                    System.out.println("Enviar pedido socket para: ");
                                    System.out.println("IP: " + user.getIP());
                                    System.out.println("Port: " + user.getPort());

                                    InetAddress destIP = InetAddress.getByName(user.getIP());
                                    Integer destPort = Integer.parseInt(user.getPort());
                                    Socket client = new Socket(destIP, destPort);

                                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());

                                    Mensagem pedido = new Mensagem("addFriend", "", sys.getUserLogado());
                                    out.writeObject(pedido);
                                    out.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (user.getStatus().equals("Do not disturb")) {
                            //Utilizadores da app que estão em Do not disturb
                            Button btn = new Button(user.getNick());
                            Button btnA = new Button("+");
                            btn.setId("ContactBtn");
                            btnA.setId("btnA");
                            btn.getStylesheets().add("styles/style.css");
                            btnA.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            dispSP.add(btnA, 1, rows);
                            btn.setStyle("-fx-text-fill: #696969");
                            btnA.setOnMouseClicked(event -> {
                                try {
                                    System.out.println("Enviar pedido socket para: ");
                                    System.out.println("IP: " + user.getIP());
                                    System.out.println("Port: " + user.getPort());

                                    InetAddress destIP = InetAddress.getByName(user.getIP());
                                    Integer destPort = Integer.parseInt(user.getPort());
                                    Socket client = new Socket(destIP, destPort);

                                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());

                                    Mensagem pedido = new Mensagem("addFriend", "", sys.getUserLogado());
                                    out.writeObject(pedido);
                                    out.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (user.getStatus().equals("offline")) {
                            //Utilizadores da app que estão offline
                            Button btn = new Button(user.getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: red");
                        }
                    } else {
                        User user = listaContactos.get(i);
                        if (user.getStatus().equals("offline")) {
                            for (int i2 = 0; i2 < sys.getUserLogado().getListaAmigos().size(); i2++) {
                                if (sys.getUserLogado().getListaAmigos().get(i2).getUser().getEmail().equals(user.getEmail())) {
                                    sys.getUserLogado().getListaAmigos().get(i2).setUser(user);
                                }
                            }
                            Button btn = new Button(user.getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: red");
                        } else if (user.getStatus().equals("online")) {
                            for (int i2 = 0; i2 < sys.getUserLogado().getListaAmigos().size(); i2++) {
                                if (sys.getUserLogado().getListaAmigos().get(i2).getUser().getEmail().equals(user.getEmail())) {
                                    sys.getUserLogado().getListaAmigos().get(i2).setUser(user);
                                }
                            }
                            Button btn = new Button(user.getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: green");
                        } else if (user.getStatus().equals("Do not disturb")) {
                            for (int i2 = 0; i2 < sys.getUserLogado().getListaAmigos().size(); i2++) {
                                if (sys.getUserLogado().getListaAmigos().get(i2).getUser().getEmail().equals(user.getEmail())) {
                                    sys.getUserLogado().getListaAmigos().get(i2).setUser(user);
                                }
                            }
                            Button btn = new Button(user.getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = dispSP.getChildren().size();
                            dispSP.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: #696969");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void atualizarContactos() {
        try {
            pedidosGrid.getChildren().clear();
            amigosGrid.getChildren().clear();
            ArrayList<Amigo> listaContactos = sys.getUserLogado().getListaAmigos();
            if (listaContactos.size() > 0) {
                for (int i = 0; i < listaContactos.size(); i++) {
                    //Update da lista de Pedidos recebidos
                    if (listaContactos.get(i).getAccepted() == false) {
                        //Lista de Pedidos de Amizade
                        Amigo amigo = listaContactos.get(i);
                        Button btn = new Button(amigo.getUser().getNick());
                        Button btnA = new Button("✔");
                        Button btnR = new Button("✘");
                        btnA.setId("btnA");
                        btnR.setId("btnR");
                        btn.setId("ContactBtn");
                        btnA.getStylesheets().add("styles/style.css");
                        btnR.getStylesheets().add("styles/style.css");
                        btn.getStylesheets().add("styles/style.css");
                        int rows = pedidosGrid.getChildren().size();
                        pedidosGrid.add(btnA, 1, rows);
                        pedidosGrid.add(btnR, 2, rows);
                        pedidosGrid.add(btn, 0, rows);
                        btnA.setOnMouseClicked(event -> {
                            try {
                                System.out.println("Aceitando pedido de " + amigo.getUser().getEmail());
                                InetAddress destIP = InetAddress.getByName(amigo.getUser().getIP());
                                Integer destPort = Integer.parseInt(amigo.getUser().getPort());
                                Socket client = new Socket(destIP, destPort);
                                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                                Mensagem pedido = new Mensagem("friendRequest", "", sys.getUserLogado());
                                out.writeObject(pedido);
                                out.flush();
                                amigo.setAccepted(true);
                                //Notificar o rmi server de que houve alterações
                                sys.getService().newUser();
                                atualizarContactos();
                                sys.getService().updateUserInfo(sys.getUserLogado());
                                promptFriendsNot();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });

                        btnR.setOnMouseClicked(event -> {
                            try {
                                for (int i2 = 0; i2 < sys.getUserLogado().getListaAmigos().size(); i2++) {
                                    if (sys.getUserLogado().getListaAmigos().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                        sys.getUserLogado().getListaAmigos().remove(i2);
                                        sys.getService().updateUserInfo(sys.getUserLogado());
                                        atualizarContactos();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                    } else if (listaContactos.get(i).getAccepted() == true) {
                        //Lista de amigos aceites
                        Amigo amigo = listaContactos.get(i);
                        if (amigo.getUser().getStatus().equals("online")) {
                            //Amigos Online
                            Button btn = new Button(amigo.getUser().getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = amigosGrid.getChildren().size();
                            amigosGrid.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: green");
                            btn.setOnMouseClicked(event -> {
                                try {
                                    messengerGrid.getChildren().clear();
                                    chatNick.setText(amigo.getUser().getNick());
                                    chatEmail.setText(amigo.getUser().getEmail());
                                    chatState.setText("online");
                                    chatState.setStyle("-fx-text-fill: green");

                                    if (amigo.getListaMensagens().size() > 0) {
                                        for (int i2 = 0; i2 < amigo.getListaMensagens().size(); i2++) {
                                            if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                                addMsgToScreen(amigo.getListaMensagens().get(i2));
                                            } else if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(sys.getUserLogado().getEmail())) {
                                                addMsgToScreenYou(amigo.getListaMensagens().get(i2));
                                            }
                                        }
                                    }
                                    showChat();
                                    msgsend_btn.setOnMouseClicked(eventSendMsg -> {
                                        try {
                                            InetAddress destIP = InetAddress.getByName(amigo.getUser().getIP());
                                            Integer destPort = Integer.parseInt(amigo.getUser().getPort());
                                            Socket client = new Socket(destIP, destPort);
                                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                                            Mensagem message = new Mensagem("msgTeste", msg_txtfld.getText(), sys.getUserLogado());
                                            out.writeObject(message);
                                            amigo.getListaMensagens().add(message);
                                            addMsgToScreenYou(message);
                                            sys.getService().updateUserInfo(sys.getUserLogado());
                                            msg_txtfld.clear();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                                    fecharChatPane.setOnMouseClicked(eventSendMsg -> {
                                        showMural();
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } else if (amigo.getUser().getStatus().equals("Do not disturb")) {
                            //Amigos em Do not Disturb
                            messengerGrid.getChildren().clear();
                            Button btn = new Button(amigo.getUser().getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = amigosGrid.getChildren().size();
                            amigosGrid.add(btn, 0, rows);
                            btn.setStyle("-fx-text-fill: #696969");
                            btn.setOnMouseClicked(event -> {
                                chatNick.setText(amigo.getUser().getNick());
                                chatEmail.setText(amigo.getUser().getEmail());
                                chatState.setText("DnD");
                                chatState.setStyle("-fx-text-fill: grey");
                                showChat();

                            });
                        } else if (amigo.getUser().getStatus().equals("offline")) {
                            //Amigos offline
                            messengerGrid.getChildren().clear();
                            Button btn = new Button(amigo.getUser().getNick());
                            btn.setId("ContactBtn");
                            btn.getStylesheets().add("styles/style.css");
                            int rows = amigosGrid.getChildren().size();
                            btn.setStyle("-fx-text-fill: red");
                            if (amigo.getListaMensagens().size() > 0) {
                                for (int i2 = 0; i2 < amigo.getListaMensagens().size(); i2++) {
                                    if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                        addMsgToScreen(amigo.getListaMensagens().get(i2));
                                    } else if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(sys.getUserLogado().getEmail())) {
                                        addMsgToScreenYou(amigo.getListaMensagens().get(i2));
                                    }
                                }
                            }
                            amigosGrid.add(btn, 0, rows);
                            btn.setOnMouseClicked(event -> {
                                chatNick.setText(amigo.getUser().getNick());
                                chatEmail.setText(amigo.getUser().getEmail());
                                chatState.setText("offline");
                                chatState.setStyle("-fx-text-fill: red");
                                msgsend_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent eventSendMsg) {
                                        try {
                                            Mensagem message = new Mensagem("msgTeste", msg_txtfld.getText(), sys.getUserLogado());
                                            //User offlineFriend = sys.getService().returnUser(amigo.getUser().getEmail());
                                            sys.getService().addMessageToPrivateArray(message, amigo.getUser().getEmail());
                                            System.out.println("enviando msg para user offline");
                                            for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                                                if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                                    sys.getUserLogado().getListaAmigos().get(i).getListaMensagens().add(message);
                                                    sys.getService().updateUserInfo(sys.getUserLogado());
                                                }
                                            }
                                            addMsgToScreenYou(message);
                                            msg_txtfld.clear();
                                        } catch (RemoteException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                                showChat();
                            });
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
            if (!(chatEmail.getText().equals("email@email.com"))) {
                if (sys.getUserLogado().getListaAmigos().get(i).getAccepted() == true) {
                    if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(chatEmail.getText())) {
                        Amigo amigo = sys.getUserLogado().getListaAmigos().get(i);
                        messengerGrid.getChildren().clear();
                        chatNick.setText(amigo.getUser().getNick());
                        chatEmail.setText(amigo.getUser().getEmail());
                        if (amigo.getUser().getStatus().equals("online")) {
                            chatState.setText("online");
                            chatState.setStyle("-fx-text-fill: green");
                            if (amigo.getListaMensagens().size() > 0) {
                                for (int i2 = 0; i2 < amigo.getListaMensagens().size(); i2++) {
                                    if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                        addMsgToScreen(amigo.getListaMensagens().get(i2));
                                    } else if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(sys.getUserLogado().getEmail())) {
                                        addMsgToScreenYou(amigo.getListaMensagens().get(i2));
                                    }
                                }
                            }
                            showChat();
                            msgsend_btn.setOnMouseClicked(eventSendMsg -> {
                                try {
                                    InetAddress destIP = InetAddress.getByName(amigo.getUser().getIP());
                                    Integer destPort = Integer.parseInt(amigo.getUser().getPort());
                                    Socket client = new Socket(destIP, destPort);
                                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                                    Mensagem message = new Mensagem("msgTeste", msg_txtfld.getText(), sys.getUserLogado());
                                    out.writeObject(message);
                                    amigo.getListaMensagens().add(message);
                                    addMsgToScreenYou(message);
                                    sys.getService().updateUserInfo(sys.getUserLogado());
                                    msg_txtfld.clear();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            fecharChatPane.setOnMouseClicked(eventSendMsg -> {
                                showMural();
                            });
                        } else if (amigo.getUser().getStatus().equals("offline")) {
                            chatState.setText("offline");
                            chatState.setStyle("-fx-text-fill: red");
                            if (amigo.getListaMensagens().size() > 0) {
                                for (int i2 = 0; i2 < amigo.getListaMensagens().size(); i2++) {
                                    if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                        addMsgToScreen(amigo.getListaMensagens().get(i2));
                                    } else if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(sys.getUserLogado().getEmail())) {
                                        addMsgToScreenYou(amigo.getListaMensagens().get(i2));
                                    }
                                }
                            }
                            msgsend_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent eventSendMsg) {
                                    try {
                                        Mensagem message = new Mensagem("msgTeste", msg_txtfld.getText(), sys.getUserLogado());
                                        //User offlineFriend = sys.getService().returnUser(amigo.getUser().getEmail());
                                        sys.getService().addMessageToPrivateArray(message, amigo.getUser().getEmail());
                                        System.out.println("enviando msg para user offline");
                                        for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                                            if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                                sys.getUserLogado().getListaAmigos().get(i).getListaMensagens().add(message);
                                                sys.getService().updateUserInfo(sys.getUserLogado());
                                            }
                                        }
                                        addMsgToScreenYou(message);
                                        msg_txtfld.clear();
                                    } catch (RemoteException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                        } else if (amigo.getUser().getStatus().equals("Do not disturb")) {
                            chatState.setText("DnD");
                            chatState.setStyle("-fx-text-fill: #696969");
                            if (amigo.getListaMensagens().size() > 0) {
                                for (int i2 = 0; i2 < amigo.getListaMensagens().size(); i2++) {
                                    if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(amigo.getUser().getEmail())) {
                                        addMsgToScreen(amigo.getListaMensagens().get(i2));
                                    } else if (amigo.getListaMensagens().get(i2).getUser().getEmail().equals(sys.getUserLogado().getEmail())) {
                                        addMsgToScreenYou(amigo.getListaMensagens().get(i2));
                                    }
                                }
                            }
                            showChat();
                            msgsend_btn.setOnMouseClicked(eventSendMsg -> {
                                try {
                                    InetAddress destIP = InetAddress.getByName(amigo.getUser().getIP());
                                    Integer destPort = Integer.parseInt(amigo.getUser().getPort());
                                    Socket client = new Socket(destIP, destPort);
                                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                                    Mensagem message = new Mensagem("msgTeste", msg_txtfld.getText(), sys.getUserLogado());
                                    out.writeObject(message);
                                    amigo.getListaMensagens().add(message);
                                    addMsgToScreenYou(message);
                                    sys.getService().updateUserInfo(sys.getUserLogado());
                                    msg_txtfld.clear();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            fecharChatPane.setOnMouseClicked(eventSendMsg -> {
                                showMural();
                            });
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void saveUserSettings() throws RemoteException {
        String defname = defName.getText();
        String defnick = defNick.getText();
        String defcurso = defCurso.getText();
        sys.getUserLogado().setName(defname);
        sys.getUserLogado().setNick(defnick);
        sys.getUserLogado().setCourse(defcurso);
        sys.getService().updateUserInfo(sys.getUserLogado());
        sys.getService().newUser();
        nickLabel.setText(sys.getUserLogado().getNick());
    }

    @FXML
    private void addMsgToScreenYou(Mensagem msg) {
        TextField tf = new TextField(msg.getMensagem());
        Label lb = new Label("Tu:");
        tf.setId("msgTxtFld");
        tf.getStylesheets().add("styles/style.css");
        tf.setDisable(true);
        lb.setId("contactLabelMsg");
        lb.getStylesheets().add("styles/style.css");
        int rowsMsg = messengerGrid.getChildren().size();
        messengerGrid.add(lb, 1, rowsMsg);
        int rowsMsg2 = messengerGrid.getChildren().size();
        messengerGrid.add(tf, 1, rowsMsg2);
    }

    @FXML
    public void addMsgToScreen(Mensagem msg) {
        TextField tf = new TextField(msg.getMensagem());
        Label lb = new Label(msg.getUser().getNick() + ":");
        lb.setId("contactLabelMsg");
        lb.getStylesheets().add("styles/style.css");
        tf.setId("msgTxtFld");
        tf.getStylesheets().add("styles/style.css");
        tf.setDisable(true);
        int rowsMsg = messengerGrid.getChildren().size();
        messengerGrid.add(lb, 0, rowsMsg);
        int rowsMsg2 = messengerGrid.getChildren().size();
        messengerGrid.add(tf, 0, rowsMsg2);
    }

    @FXML
    public void addMsgToBoard(Mensagem msg) {
        try {
            System.out.println(sys.getUserLogado().getListaMensagensBoard().size());
            String state = null;
            TextField tf = new TextField(msg.getMensagem());
            Label lb = new Label(msg.getUser().getNick() + ":");
            lb.setId("contactLabelMsg");
            lb.getStylesheets().add("styles/style.css");
            tf.setId("BoardLabelMsg");
            tf.getStylesheets().add("styles/style.css");
            int rowsMsg = BoardGrid.getChildren().size();
            BoardGrid.add(lb, 0, rowsMsg);
            int rowsMsg2 = BoardGrid.getChildren().size();
            BoardGrid.add(tf, 0, rowsMsg2);
            if (!(msg.getUser().getEmail().equals(sys.getUserLogado().getEmail()))) {
                for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                    if (sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail().equals(msg.getUser().getEmail())) {
                        state = sys.getUserLogado().getListaAmigos().get(i).getUser().getStatus();
                    }
                }
            } else {
                state = "online";
            }
            if (state.equals("online")) {
                lb.setStyle("-fx-text-fill: green");
            } else if (state.equals("offline")) {
                lb.setStyle("-fx-text-fill: red");
            } else if (state.equals("Do not disturb")) {
                lb.setStyle("-fx-text-fill: #696969");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateBoard() {
        BoardGrid.getChildren().clear();
        System.out.println(sys.getUserLogado().getListaMensagensBoard().size());
        for (int i = 0; i < sys.getUserLogado().getListaMensagensBoard().size(); i++) {
            System.out.println(sys.getUserLogado().getListaMensagensBoard().size());
            addMsgToBoard(sys.getUserLogado().getListaMensagensBoard().get(i));
        }
    }

    @FXML
    private void sendMessageToBoard() {
        try {
            Mensagem message = new Mensagem("msgBoard", boardMsg_TF.getText(), sys.getUserLogado());
            for (int i = 0; i < sys.getUserLogado().getListaAmigos().size(); i++) {
                if (sys.getUserLogado().getListaAmigos().get(i).getAccepted() == true) {
                    if (sys.getUserLogado().getListaAmigos().get(i).getUser().getStatus().equals("online")) {
                        Amigo amigo = sys.getUserLogado().getListaAmigos().get(i);
                        InetAddress destIP = InetAddress.getByName(amigo.getUser().getIP());
                        Integer destPort = Integer.parseInt(amigo.getUser().getPort());
                        Socket client = new Socket(destIP, destPort);
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        out.writeObject(message);
                        out.flush();
                        System.out.println("Mensagem de Board enviada");
                    } else if (sys.getUserLogado().getListaAmigos().get(i).getUser().getStatus().equals("offline")) {
                        sys.getService().addMessageToPrivateBoardArray(message, sys.getUserLogado().getListaAmigos().get(i).getUser().getEmail());
                    }
                }
            }
            addMsgToBoard(message);
            sys.getUserLogado().getListaMensagensBoard().add(message);
            sys.getService().updateUserInfo(sys.getUserLogado());
            boardMsg_TF.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void contactUpdater() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sys.getService().newUser();
                    Integer currCon = sys.getService().numberCon();
                    Integer oldCon = currCon;
                    while (true) {
                        currCon = sys.getService().numberCon();
                        if (!(oldCon.equals(currCon))) {
                            System.out.println("Hora de atualizar os contactos todos");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    getAllContactos();
                                    atualizarContactos();
                                    updateBoard();
                                }
                            });
                            oldCon = sys.getService().numberCon();
                        } else {
                            oldCon = sys.getService().numberCon();
                        }
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startSocketServerReg() {
        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(0);
                    String inetAddress = InetAddress.getLocalHost().getHostAddress();
                    sys.getUserLogado().setIP(inetAddress.toString());
                    sys.getUserLogado().setPort(Integer.toString(server.getLocalPort()));
                    sys.getService().registerUser(sys.getUserLogado());
                    System.out.println("Servidor a escutar em " + sys.getUserLogado().getIP() + " : " + sys.getUserLogado().getPort());

                    while (true) {
                        Socket ligacao = server.accept();
                        reqHandler thread = new reqHandler(ligacao);
                        thread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    
  

    private void startSocketServerLogin() {
        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(0);
                    String inetAddress = InetAddress.getLocalHost().getHostAddress();
                    sys.getUserLogado().setIP(inetAddress.toString());
                    sys.getUserLogado().setPort(Integer.toString(server.getLocalPort()));
                    sys.getService().updateUserInfo(sys.getUserLogado());
                    System.out.println("Servidor a escutar em " + sys.getUserLogado().getIP() + " : " + sys.getUserLogado().getPort());

                    while (true) {
                        Socket ligacao = server.accept();
                        reqHandler thread = new reqHandler(ligacao);
                        thread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @FXML
    private void setStateOnline() {
        try {
            sys.getUserLogado().setStatus("online");
            Image img = new Image("Images/Online.png");
            userStateImg.setImage(img);
            sys.getService().updateUserInfo(sys.getUserLogado());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setStateOffline() {
        try {
            sys.getUserLogado().setStatus("offline");
            sys.getService().updateUserInfo(sys.getUserLogado());
            //Notificar o rmi server de que houve alterações
            sys.getService().newUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setStateDnD() {
        if (sys.getUserLogado().getStatus().equals("Do not disturb")) {
            try {
                sys.getUserLogado().setStatus("online");
                Image img = new Image("Images/Online.png");
                userStateImg.setImage(img);
                sys.getService().updateUserInfo(sys.getUserLogado());
                //Notificar o rmi server de que houve alterações
                sys.getService().newUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                sys.getUserLogado().setStatus("Do not disturb");
                Image img = new Image("Images/DnD.png");
                userStateImg.setImage(img);
                sys.getService().updateUserInfo(sys.getUserLogado());
                //Notificar o rmi server de que houve alterações
                sys.getService().newUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void showAdd(ActionEvent event) {
        AddPane.toFront();
    }

    @FXML
    void showDefinicoes(ActionEvent event) {
        defName.setText(sys.getUserLogado().getName());
        defNick.setText(sys.getUserLogado().getNick());
        defEmail.setText(sys.getUserLogado().getEmail());
        defCurso.setText(sys.getUserLogado().getCourse());
        defIP.setText(sys.getUserLogado().getIP());
        defPort.setText(sys.getUserLogado().getPort());
        DefinicoesPane.toFront();
    }

    @FXML
    void showChat() {
        ChatPane.toFront();
    }

    @FXML
    void showListaAmigos(ActionEvent event) {
        clearFriendsNot();
        atualizarContactos();
        ListaAmigosPane.toFront();
    }

    @FXML
    void showLoginPane() {
        setStateOffline();
        LoginPane.toFront();
    }

    @FXML
    void showMural() {
        chatEmail.setText("email@email.com");
        afterloginPane.toFront();
        MuralPane.toFront();
        ListaPane.toFront();
        TitlePane.toFront();
    }

    @FXML
    void showPedidos(ActionEvent event) {
        clearReqNot();
        atualizarContactos();
        PedidosPane.toFront();

    }

    @FXML
    void showDisponiveis(ActionEvent event) {
        getAllContactos();
        DisponiveisPane.toFront();
    }

    //Mover a janela juntamente com a função pressWindow
    @FXML
    private void dragWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    //Mover a janela juntamente com a função dragWindow
    @FXML
    private void pressWindow(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    //Fechar a App
    @FXML
    private void closeApp() {
        System.out.println("A fechar a app...");
        setStateOffline();
        System.exit(0);
    }

    public String getChatEmail() {
        return chatEmail.getText();
    }

}
