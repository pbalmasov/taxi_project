/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Модель сообщения
 * @author p.balmasov
 */
public class Message implements Comparable<Message> {

    private String _text;
    private Date _date;
    private boolean _isRead;
    private int _index;



    public Message(String text, Date date, boolean isRead, int index) {
        _text = text;
        _date = date;
        _isRead = isRead;
        set_index(index);
    }


    public String toString() {
        return new SimpleDateFormat("dd.MM.yyyy").format(getDate()) + "\n" + getText();
    }

    @Override
    public int compareTo(Message message) {
        return getDate().compareTo(message.getDate());
    }

    /**
     * @param text the _text to set
     */
    public void setText(String text) {
        this._text = text;
    }

    /**
     * @param date the _date to set
     */
    public void setDate(Date date) {
        this._date = date;
    }

    /**
     * @return the _text
     */
    public String getText() {
        return _text;
    }

    /**
     * @return the _date
     */
    public Date getDate() {
        return _date;
    }

    public boolean is_isRead() {
        return _isRead;
    }

    public void set_isRead(boolean _isRead) {
        this._isRead = _isRead;
    }

    public int get_index() {
        return _index;
    }

    public void set_index(int _index) {
        this._index = _index;
    }



}
