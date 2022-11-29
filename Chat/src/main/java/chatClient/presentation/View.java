package chatClient.presentation;

import chatClient.Application;
import chatClient.logic.ServiceProxy;
import chatProtocol.Message;
import chatProtocol.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Observer;

public class View implements Observer {
    private JPanel panel;
    private JPanel loginPanel;
    private JPanel bodyPanel;
    private JTextField id;
    private JPasswordField clave;
    private JButton login;
    private JButton finish;
    private JTextPane messages;
    private JTextField mensaje;
    private JButton post;
    private JButton logout;
    private JButton register;
    private JTable usersFld;
    private JButton addContact;
    private JTextField agregarFld;
    private JTextField searchFld;
    private JButton searchContact;
    private JLabel nameChat;
    private JLabel stateLbl;
    private int row;

    Model model;
    Controller controller;

    public View() {
        loginPanel.setVisible(true);
        Application.window.getRootPane().setDefaultButton(login);
        bodyPanel.setVisible(false);

        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User u = new User(id.getText(), new String(clave.getPassword()), "");
                id.setBackground(Color.white);
                clave.setBackground(Color.white);
                try {
                    row=-1;
                    controller.login(u);
                    id.setText("");
                    clave.setText("");
                    stateLbl.setIcon(null);
                    nameChat.setText("");
                } catch (Exception ex) {
                    id.setBackground(Color.orange);
                    clave.setBackground(Color.orange);
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.logout();
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.post(mensaje.getText(),model.getUsers().get(row));
                mensaje.setText("");
            }
        });
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nombreR=new JTextField("");
                JTextField idR=new JTextField("");
                JPasswordField claveR=new JPasswordField();
                Object[] fields={"Nombre", nombreR, "Usuario", idR, "Clave",claveR};
                int option=JOptionPane.showConfirmDialog(panel,fields,"Registrarse",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
                if(option==JOptionPane.OK_OPTION){
                    try{
                        controller.register(new User(idR.getText(),new String(claveR.getPassword()), nombreR.getText()));
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(panel,ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        addContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.addContact(new User(agregarFld.getText(),"",""));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                agregarFld.setText("");
            }
        });
        usersFld.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    try {
                        row = usersFld.getSelectedRow();
                        nameChat.setText(model.getUsers().get(row).getNombre());
                        controller.selectChat(row);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
            }
        });
        searchContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.searchContact(searchFld.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                searchFld.setText("");
            }
        });
    }

    public void setModel(Model model) {
        this.model = model;
        model.addObserver(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public JPanel getPanel() {
        return panel;
    }

    String backStyle = "margin:0px; background-color:#e6e6e6;";
    String senderStyle = "background-color:#c2f0c2;margin-left:30px; margin-right:5px;margin-top:3px; padding:2px; border-radius: 25px;";
    String receiverStyle = "background-color:white; margin-left:5px; margin-right:30px; margin-top:3px; padding:2px;";

    public void update(java.util.Observable updatedModel, Object properties) {
        int[] cols={TableModel.NOMBRE,TableModel.STATE};
        usersFld.setModel(new chatClient.presentation.TableModel(cols,model.getUsers()));
        usersFld.setRowHeight(30);
        int prop = (int) properties;
        if (model.getCurrentUser() == null) {
            Application.window.setTitle("CHAT");
            loginPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(login);
            bodyPanel.setVisible(false);
        } else {
            Application.window.setTitle(model.getCurrentUser().getNombre().toUpperCase());
            loginPanel.setVisible(false);
            bodyPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(post);
            if ((prop & Model.CHAT) == Model.CHAT) {
                this.messages.setText("");
                String text = "";
                for (Message m : model.getMessages()) {
                    if (m.getSender().equals(model.getCurrentUser())) {
                        text += ("Me:" + m.getMessage() + "\n");
                    } else {
                            text += (m.getSender().getNombre() + ": " + m.getMessage() + "\n");
                     }
                }
                if(row==-1){
                    stateLbl.setIcon(null);
                }else {
                    try {
                        Image i = null;
                        Icon icon=null;
                        if (model.getUsers().get(row).isOnline()) {
                            i = ImageIO.read(Application.class.getResourceAsStream("/luzVerde.png"));
                            icon = new ImageIcon(i);
                            stateLbl.setIcon(icon);
                        } else {
                            i = ImageIO.read(Application.class.getResourceAsStream("/luzRoja.png"));
                            icon = new ImageIcon(i);
                            stateLbl.setIcon(icon);
                        }
                    }catch (IOException e){throw new RuntimeException(e);}
                }
                this.messages.setText(text);
            }
            //this.mensaje.setText("");
        }
        panel.validate();
    }

}
