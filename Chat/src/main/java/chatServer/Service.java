package chatServer;


import chatClient.data.XmlPersister;
import chatProtocol.User;
import chatProtocol.IService;
import chatProtocol.Message;
import chatServer.data.Data;
import chatServer.data.UsuarioDao;

import java.util.List;
import java.util.Objects;

public class Service implements IService{

    private Data data;
    private UsuarioDao usuarioDao;
    
    public Service() {
        usuarioDao=new UsuarioDao();
        data = new Data();
    }
    public void register(User user) throws Exception {
            usuarioDao.create(user);
    }
    
    public void post(Message m){
        // if wants to save messages, ex. recivier no logged on
    }
    
    public User login(User p) throws Exception{
        //for(User u:data.getUsers()) if(p.equals(u)) return u;
        //throw new Exception("User does not exist");
        //p.setNombre(p.getId()); return p;
        return usuarioDao.read(p.getId());
    } 

    public void logout(User p) throws Exception{
        //nothing to do
    }
    public User checkContact(User user) throws Exception {
        return usuarioDao.read(user.getId());
    }
    @Override
    public void store() {
        //nothing to do
    }
    public List<User> contactsSearch(String filter)throws Exception{
        return null;
    }
    public List<Message> messagesSearch(String filtroContact)throws Exception{
        return null;
    }
    public void saveMessage(Message message){
        //nothing to do
    }
    public void saveContact(User receiver){
        //nothing to do
    }
}
