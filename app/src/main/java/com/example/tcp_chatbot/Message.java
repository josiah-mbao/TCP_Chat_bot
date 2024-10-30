public class Message {
    private String id;
    private String text;
    private String senderId;
    private String receiverId;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String id, String text, String senderId, String receiverId) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
