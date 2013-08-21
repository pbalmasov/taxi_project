package ru.peppers;

import org.w3c.dom.Document;

public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(Document doc);
}
