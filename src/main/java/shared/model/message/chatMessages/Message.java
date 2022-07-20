package shared.model.message.chatMessages;

import shared.model.user.UserType;

public class Message {

    private String sender;
    private String receiver;
    private UserType senderType;
    private UserType receiverType;
    private String messageText;
    private String sendMessageTime;

    public Message(String sender, String receiver, UserType senderType, UserType receiverType,
                   String messageText, String sendMessageTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderType = senderType;
        this.receiverType = receiverType;
        this.messageText = messageText;
        this.sendMessageTime = sendMessageTime;
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
}
