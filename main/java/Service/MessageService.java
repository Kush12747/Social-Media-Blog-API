package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    //get message by id
    public Message getMessageById(int id) {
        return messageDAO.getMessageById(id);
    }

    //get all message by a list
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    //delete message from DB
    public boolean deleteMessage(int message_id) {
        return messageDAO.deleteMessage(message_id);    
    }

    //update message by id
    public Message updateMessageById(int messageId, Message message) {
        Message existingMessage = messageDAO.getMessageById(messageId);
        
        if (existingMessage == null) {
            return null;
        } else {
            existingMessage.setMessage_text(message.getMessage_text());
            return existingMessage;
        }
    }

    //get message by account id
    public List<Message> getMessageByAccountId(int accountId) {
        return messageDAO.getMessagesByAccountId(accountId);
    }

    //insert a message into DB
    public Message addMessage(Message message) {
        if (message == null) {return null;}
        return messageDAO.insertMessage(message);
    }
}