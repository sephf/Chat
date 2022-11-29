package chatProtocol;

import java.util.List;

public interface IService {
    public User login(User u) throws Exception;
    public void logout(User u) throws Exception; 
    public void post(Message m);
    public void register(User u) throws Exception;
    public User checkContact(User u)throws Exception;
    public void store();
    public List<User> contactsSearch(String filter)throws Exception;
    public List<Message> messagesSearch(String filtroContact)throws Exception;
    public void saveMessage(Message message);
    public void saveContact(User receiver);
    }
