
package chatServer;

import chatProtocol.Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import chatProtocol.IService;
import chatProtocol.Message;
import chatProtocol.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

public class Server {
    ServerSocket srv;
    List<Worker> workers; 
    
    public Server() {
        try {
            srv = new ServerSocket(Protocol.PORT);
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Servidor iniciado...");
        } catch (IOException ex) {
        }
    }
    /*
    public void run(){
        IService service = new Service();

        boolean continuar = true;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;
        while (continuar) {
            try {
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");
                User user=this.login(in,out,service);                          
                Worker worker = new Worker(this,in,out,user, service); 
                workers.add(worker);                      
                worker.start();                                                
            }
            catch (IOException | ClassNotFoundException ex) {}
            catch (Exception ex) {
                try {
                    out.writeInt(Protocol.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex1) {}
               System.out.println("Conexion cerrada...");
            }
        }
    }*/
    public void run() {
        IService service = new Service();
        boolean continuar = true;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;
        int method;
        while (continuar) {
            try {
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");
                method=in.readInt();
                switch (method){
                    case Protocol.LOGIN:
                        try {
                            User user = service.login((User) in.readObject());
                            out.writeInt(Protocol.ERROR_NO_ERROR);
                            out.writeObject(user);
                            out.flush();
                            for(Worker wk: workers){wk.online(user);}
                            Worker worker = new Worker(this,in,out, user, service);
                            workers.add(worker);
                            worker.start();
                        }catch (Exception ex){
                            out.writeInt(Protocol.ERROR_LOGIN);
                            out.flush();
                            skt.close();
                            System.out.println("Conexion cerrada...");
                        }
                        break;
                        case Protocol.REGISTER:
                            try {
                                service.register((User) in.readObject());
                                out.writeInt(Protocol.ERROR_NO_ERROR);
                                out.flush();
                            }catch (Exception ex) {
                                out.writeInt(Protocol.ERROR_REGISTER);
                                out.flush();
                                skt.close();
                                System.out.println("Conexion cerrada...");
                            }
                            break;
                    default:
                        out.writeInt(Protocol.ERROR_LOGIN);
                        out.flush();
                        skt.close();
                        System.out.println("Conexion cerrada...");
                }
            }
            catch (Exception ex) {}
        }
    }
    
    private User login(ObjectInputStream in,ObjectOutputStream out,IService service) throws IOException, ClassNotFoundException, Exception{
        int method = in.readInt();
        if (method!=Protocol.LOGIN) throw new Exception("Should login first");
        User user=(User)in.readObject();                          
        user=service.login(user);
        out.writeInt(Protocol.ERROR_NO_ERROR);
        out.writeObject(user);
        out.flush();
        return user;
    }
    
    public void deliver(Message message){
        for(Worker wk:workers){
            if(message.getReceiver().equals(wk.user)) {
                wk.deliver(message);
                break;
            }
        }
    }
    
    public void remove(User u){
        Worker w=null;
        for(Worker wk:workers) {
            wk.offline(u);
            if(wk.user.equals(u)){
                w=wk;
            }
        }
        workers.remove(w);
        System.out.println("Quedan: " + workers.size());
    }
    
}