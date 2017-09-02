package bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;
    @Column
   private String text;
    @Column
   private Date date;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_Sender")
   private User sender;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_Receiver")
   private User receiver;

    public Message() {
    }

    public Message(String text, Date date, User sender, User receiver) {
        this.text = text;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != message.id) return false;
        if (text != null ? !text.equals(message.text) : message.text != null) return false;
        if (date != null ? !date.equals(message.date) : message.date != null) return false;
        if (sender != null ? !sender.equals(message.sender) : message.sender != null) return false;
        return receiver != null ? receiver.equals(message.receiver) : message.receiver == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
