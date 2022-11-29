package chatClient.presentation;

import chatClient.Application;
import chatClient.logic.ServiceProxy;
import chatProtocol.Message;
import chatProtocol.User;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Controller {
    View view;
    Model model;
    ServiceProxy localService;
    
    public Controller(View view, Model model) throws Exception {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.instance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
    }
    public void login(User u) throws Exception{
        User logged=ServiceProxy.instance().login(u);
        model.setCurrentUser(logged);
        model.setUsers(ServiceProxy.instance().contactsSearch(""));
        model.commit(Model.USER+Model.CHAT);
    }

    public void post(String text, User user){
        Message message = new Message();
        message.setMessage(text);
        message.setSender(model.getCurrentUser());
        message.setReceiver(user);
        ServiceProxy.instance().post(message);
        model.getMessages().add(message);
        model.commit(Model.CHAT);
        ServiceProxy.instance().saveMessage(message);
    }
    public void logout(){
        try {
            ServiceProxy.instance().logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        model.commit(Model.USER+Model.CHAT);
    }
        
    public void deliver(Message message){
        for(User user: model.getUsers()){
            if(Objects.equals(user.getId(), message.getSender().getId())){
                model.messages.add(message);
                model.commit(Model.CHAT);
                ServiceProxy.instance().saveMessage(message);
                break;
            }
        }
    }
    public void online(User user){
        for(User u: model.getUsers()) {
            if (Objects.equals(user,u)){
                u.setOnline(true);
            }
        }
        model.commit(Model.CHAT);
    }
    public void offline(User user){
        for(User u: model.getUsers()) {
            if (Objects.equals(user,u)){
                u.setOnline(false);
            }
        }
        model.commit(Model.CHAT);
    }
    public void register(User user) throws Exception {
            ServiceProxy.instance().register(user);
            model.commit(Model.USER);
    }
    public void addContact(User user)throws Exception{
        ServiceProxy.instance().checkContact(user);
    }
    public void searchContact(String filter) throws Exception {
        List<User> rows=ServiceProxy.instance().contactsSearch(filter);
        model.setUsers(rows);
        model.commit(Model.USER);
    }
    public void noErrorContact(User contact) throws Exception {
       User result = model.getUsers().stream().filter(e->e.getId().equals(contact.getId())).findFirst().orElse(null);
        if (result==null) {
            ServiceProxy.instance().saveContact(contact);
        }
        else throw new Exception("Contacto ya existe");
        model.getUsers().add(contact);
        model.commit(Model.CHAT);
    }
    public void selectChat(int row) throws Exception {
        model.setMessages(ServiceProxy.instance().messagesSearch(model.getUsers().get(row).getId()));
        model.commit(Model.CHAT);
    }
}
