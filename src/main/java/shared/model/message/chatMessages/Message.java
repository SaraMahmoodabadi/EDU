package shared.model.message.chatMessages;

import shared.model.user.UserType;

public class Message {

    private String sender;
    private String receiver;
    private UserType senderType;
    private UserType receiverType;
    private String messageText;
    private String sendMessageTime;
    private String name;
    private String type;
    private boolean isMedia;
    private String user;
    private boolean isSender;

    public Message() {}

    public Message(String sender, String receiver, UserType senderType, UserType receiverType,
                   String messageText, String sendMessageTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderType = senderType;
        this.receiverType = receiverType;
        this.messageText = messageText;
        this.sendMessageTime = sendMessageTime;
    }

    public Message(String name, String user, String userMessage, String date, String type) {
        this.name = name;
        this.user = user;
        this.messageText = userMessage;
        this.sendMessageTime = date;
        this.type = type;
    }

    public Message(String user, String message, boolean isSender) {
        this.user = user;
        this.messageText = message;
        this.isSender = isSender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public UserType getSenderType() {
        return senderType;
    }

    public void setSenderType(UserType senderType) {
        this.senderType = senderType;
    }

    public UserType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(UserType receiverType) {
        this.receiverType = receiverType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSendMessageTime() {
        return sendMessageTime;
    }

    public void setSendMessageTime(String sendMessageTime) {
        this.sendMessageTime = sendMessageTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public void setMedia(boolean media) {
        isMedia = media;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }
}
