package se.ju.students.axam1798.andromeda;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class MessageQueue extends Observable
{
    public static class Message extends Pair<MESSAGE_TYPE, String>
    {
        public Message(MESSAGE_TYPE type, String data) {
            super(type, data);
        }

        // Call this if you took care of the event and
        // want it removed from the queue.
        public Message handle()
        {
            MessageQueue.getInstance().popMessage(this);

            return this;
        }
    }

    private static MessageQueue m_instance = null;
    private List<Message> m_messageQueue = new ArrayList<Message>();

    private MessageQueue()
    {

    }

    public static MessageQueue getInstance()
    {
        if(m_instance == null)
            m_instance = new MessageQueue();

        return m_instance;
    }

    enum MESSAGE_TYPE
    {
        SEND_BLUETOOTH,
        RECIEVE_BLUETOOTH
    }

    public void pushMessage(MESSAGE_TYPE type, String data)
    {
        m_messageQueue.add(new Message(type, data));

        this.setChanged();
        this.notifyObservers();
    }

    public Message peekMessage()
    {
        return m_messageQueue.get(0);
    }

    private void popMessage(Message ref)
    {
        m_messageQueue.remove(
                m_messageQueue.indexOf(ref)
        );
    }
}
