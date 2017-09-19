package bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Message bean
 */
@SuppressWarnings("unused")
public class Message implements Serializable {
    private int id;
    private String text;
    private String sender;
    private Timestamp date;

    public Message() {
    }

    public Message(String text, String sender) {
        this.text = text;
        this.date = new Timestamp(new Date().getTime());
        this.sender = sender;
    }

    public Message(int id, String text, String sender, Timestamp date) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != message.id) return false;
        if (text != null ? !text.equals(message.text) : message.text != null) return false;
        if (sender != null ? !sender.equals(message.sender) : message.sender != null) return false;
        return date != null ? date.equals(message.date) : message.date == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", sender='" + sender + '\'' +
                ", date=" + date +
                '}';
    }
}