package ru.ntechs;

import org.w3c.dom.Document;
/**
 * Callback по выполнению задачи
 * @author p.balmasov
 */
public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(Document doc);
}
