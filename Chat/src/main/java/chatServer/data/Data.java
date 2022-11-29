package chatServer.data;
import chatProtocol.Message;
import chatProtocol.User;
import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<User> users;
    private List<Message> messages;

    public Data() {
        users = new ArrayList<>();
        messages=new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}