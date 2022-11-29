package chatClient.logic;

import chatClient.data.Data;
import chatClient.data.XmlPersister;
import chatClient.presentation.Controller;
import chatProtocol.User;
import chatProtocol.Protocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;
import chatProtocol.IService;
import chatProtocol.Message;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServiceProxy implements IService{
    private static IService theInstance;
    private Data data;
    private User logged;
    public static IService instance(){
        if (theInstance==null){ 
            theInstance=new ServiceProxy();
        }
        return theInstance;
    }

    ObjectInputStream in;
    ObjectOutputStream out;
    Controller controller;

    public ServiceProxy() {
        data =  new Data();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    Socket skt;
    private void connect() throws Exception{
        skt = new Socket(Protocol.SERVER,Protocol.PORT);
        out = new ObjectOutputStream(skt.getOutputStream());
        out.flush();
        in = new ObjectInputStream(skt.getInputStream());    
    }

    private void disconnect() throws Exception{
        skt.shutdownOutput();
        skt.close();
    }
    
    public User login(User u) throws Exception{
        connect();
        try {
            out.writeInt(Protocol.LOGIN);
            out.writeObject(u);
            out.flush();
            int response = in.readInt();
            if (response==Protocol.ERROR_NO_ERROR){
                logged=(User) in.readObject();
                logged.setOnline(true);
                this.start();
                try{
                    data= XmlPersister.instance(u.getId()).load();
                }
                catch(Exception ignored){}
                return logged;
            }
            else {
                disconnect();
                throw new Exception("No remote user");
            }            
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }
    
    public void logout(User u) throws Exception{
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(u);
        out.flush();
        u.setOnline(false);
        this.stop();
        this.disconnect();
        store();
        logged=null;
    }
    
    public void post(Message message){
        try {
            out.writeInt(Protocol.POST);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            
        }   
    }

    @Override
    public void register(User u) throws Exception {
            connect();
            out.writeInt(Protocol.REGISTER);
            out.writeObject(u);
            out.flush();
            this.start();
    }
    public User checkContact(User user) throws Exception {
        try {
            //connect();
            out.writeInt(Protocol.CONTACT);
            out.writeObject(user);
            out.flush();
            // this.start();
        }catch (IOException ex){}
        return null;
    }
    // LISTENING FUNCTIONS
   boolean continuar = true;    
   public void start(){
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            }
        });
        continuar = true;
        t.start();
    }
    public void stop(){
        continuar=false;
    }
    
   public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operacion: "+method);
                switch(method){
                case Protocol.DELIVER:
                    try {
                        Message message=(Message)in.readObject();
                        deliver(message);
                    } catch (ClassNotFoundException ex) {}
                    break;
                    case Protocol.CONTACT_RESPONSE:
                        try{
                            contact((User)in.readObject());
                        }catch (ClassNotFoundException ignored){}
                        break;
                        case Protocol.ONLINE:
                            try {
                                online((User) in.readObject());
                            }catch (ClassNotFoundException exception){} break;
                            case Protocol.OFFLINE:
                                try {
                                    offline((User) in.readObject());
                                }catch (ClassNotFoundException exception){} break;
                }
                out.flush();
            } catch (IOException  ex) {
                continuar = false;
            }
        }
    }
    
   private void deliver( final Message message ){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               controller.deliver(message);
            }
         }
      );
   }
    private void contact(final User u){
       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               try {
                   controller.noErrorContact(u);
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
           }
       });
    }
    private void online(final User u){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    controller.online(u);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void offline(final User u){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    controller.offline(u);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void store(){
        try {
            XmlPersister.instance(logged.getId()).store(data);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public List<User> contactsSearch(String filter)throws Exception{
        List<User> result=new ArrayList<>();
        User u=null;
        boolean flag;
        boolean allContacts;
        for(Message message: data.getMessages()){
            if(Objects.equals(filter, "")){
                allContacts=true;
            }else{
                allContacts=Objects.equals(message.getReceiver().getNombre(), filter);
            }
            if(Objects.equals(message.getSender().getId(),logged.getId()) && allContacts){
                flag=false;
                u=message.getReceiver();
                if(result.isEmpty()){
                    result.add(u);
                }else {
                    for (User user : result) {
                        if (Objects.equals(user.getId(), u.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }
    public List<Message> messagesSearch(String filtroContact)throws Exception{
       List<Message> result=new ArrayList<>();
       for(Message message: data.getMessages()){
               if (Objects.equals(message.getSender().getId(), filtroContact) || Objects.equals(message.getReceiver().getId(),filtroContact)) {
                   result.add(message);
               }
       }
       return result;
    }
    public void saveMessage(Message message){
        data.getMessages().add(message);
    }
    public void saveContact(User contact){
       contact.setOnline(false);
       data.getMessages().add(new Message(logged,"Messages are end-to-end encrypted",contact));
    }
}
