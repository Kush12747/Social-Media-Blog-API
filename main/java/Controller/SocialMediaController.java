package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private final MessageService messageService;
    private final AccountService accountService;

    public SocialMediaController() {
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessageByAccountIdHandler);
        return app;
    }
    
    //create a user an account and check for flaws. 200 ok, 400 bad
    private void registerHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account registeredAccount = accountService.getLogin(account.getUsername(), account.getPassword());
        
        if (account.getUsername().isEmpty() || account.getPassword().length() < 4 || registeredAccount != null) {
            ctx.status(400);
            return;
        }

        Account insertAccount = accountService.registerAccount(account);
        if (insertAccount != null) {
            ctx.json(mapper.writeValueAsString(insertAccount));
        } else {
            ctx.status(400);
        }
    }

    //process user logins
    private void loginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account getAccount = accountService.getLogin(account.getUsername(), account.getPassword());
        
        if (getAccount != null) {
            ctx.json(mapper.writeValueAsString(getAccount));
        } else {
            ctx.status(401);
        }
    }
    
    //allows the user to create messages and insert them to the DB
    private void createMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Account account = accountService.getAccountById(message.getPosted_by());
       
        if (account == null) {ctx.status(400); return;}

        Message addedMessage = messageService.addMessage(message);
        
        if (addedMessage == null || addedMessage.getMessage_text().isEmpty() || message.getMessage_text().length() >= 255) {
            ctx.status(400);
        } else {
            ctx.json(mapper.writeValueAsString(addedMessage));
        }
    }

    //gets the list of messages
    private void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    //get messages by id
    private void getMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        
        if (message != null) {
            ctx.json(message);
        } else {
            ctx.status(200);
        }
    }
    
    //delete a certain message by its id
    private void deleteMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deleteMessage = messageService.getMessageById(messageId);
        boolean check = messageService.deleteMessage(messageId);
        
        if (check && deleteMessage != null) {
            ctx.status(200);
            ctx.json(deleteMessage);
        } else {
            ctx.status(200);
        }
    }
    
    //update a message
    private void updateMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));

        if (message.getMessage_text() == null || message.getMessage_text().isEmpty()) {
            ctx.status(400);
        }

        Message updatedMessage = messageService.updateMessageById(messageId, message);
        if (updatedMessage == null || updatedMessage.getMessage_text().length() >= 255 || updatedMessage.getMessage_text().length() <= 0) {
            ctx.status(400);
        } else {
            ctx.json(mapper.writeValueAsString(updatedMessage));
        }
    }

    //get certain messages by users account id
    private void getMessageByAccountIdHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessageByAccountId(accountId);
        ctx.json(messages);
    }
}