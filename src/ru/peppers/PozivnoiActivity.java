package ru.peppers;

import hello.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class PozivnoiActivity extends Activity {
    private static final String MY_TAG = "My_tag";
    protected static final String PREFS_NAME = "MyNamePrefs1";
    protected Integer index;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        String pozivnoi = settings.getString("pozivnoidata", "");

        if (pozivnoi == "") {
            EditText pozivnoiEditText = (EditText) findViewById(R.id.pozivnoiEditText);

            pozivnoiEditText.setOnKeyListener(new EditText.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    EditText pozivnoiEditText = (EditText) v;

                    if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        if (!event.isShiftPressed() && pozivnoiEditText.getText().toString().length() != 0)
                            return loginWithPozivnoi(pozivnoiEditText.getText().toString());
                    }
                    return false; // pass on to other listeners.
                }

            });
        } else {
            loginWithPozivnoi(pozivnoi);
        }
    }

    private boolean loginWithPozivnoi(String pozivnoi) {
        {
            Log.d(MY_TAG, pozivnoi);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("action", "savepozivnoi"));
            nameValuePairs.add(new BasicNameValuePair("pozivnoi", pozivnoi));

            Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);

            Node errorNode = doc.getElementsByTagName("error").item(0);

            if (Integer.parseInt(errorNode.getTextContent()) == 1)
                new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                        .setMessage("Неправильный позывной.").setNeutralButton("Закрыть", null).show();
            else {

                // save pozivnoi if all ok

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("pozivnoidata", pozivnoi);
                editor.commit();

                initMessages(doc);
            }
            return true;
        }
    }

    private void initMessages(Document doc) {
        List<NameValuePair> nameValuePairs;
        Node errorNode;
        Node idNode = doc.getElementsByTagName("id").item(0);
        index = Integer.valueOf(idNode.getTextContent());
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "messagedata"));
        nameValuePairs.add(new BasicNameValuePair("id", idNode.getTextContent()));

        doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);

        errorNode = doc.getElementsByTagName("error").item(0);

        if (Integer.parseInt(errorNode.getTextContent()) == 1)
            new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                    .setMessage("Ошибка в получении сообщений").setNeutralButton("Закрыть", null).show();
        else {
            try {
                getMessages(doc);
            } catch (ParseException e) {
                new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                        .setMessage("Сервер недоступен перезагрузите приложение.")
                        .setNeutralButton("Закрыть", null).show();
            }
        }
    }

    private void getMessages(Document doc) throws ParseException {
        NodeList nodeList = doc.getElementsByTagName("message");
        final ArrayList<Message> unreaded = new ArrayList<Message>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            boolean isRead = Boolean.parseBoolean(attributes.getNamedItem("readed").getTextContent());
            int index = Integer.valueOf(attributes.getNamedItem("index").getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date").getTextContent());
            String text = nodeList.item(i).getTextContent();

            Message message = new Message(text, date, isRead, index);
            if (!isRead) {
                unreaded.add(message);
            }
        }
        if (unreaded.size() == 0) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            // if password type password then check password then loginWithPozivnoi
            Intent intent;
            boolean isPassword = settings.getBoolean("isPassword", false);
            if (!isPassword)
                intent = new Intent(PozivnoiActivity.this, MainListActivity.class);
            else
                intent = new Intent(PozivnoiActivity.this, PasswordActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt("id", index);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PozivnoiActivity.this);
            builder.setTitle(unreaded.get(0).getDate().toGMTString());
            builder.setMessage(unreaded.get(0).getText());
            builder.setNeutralButton("Ок", onMessageClick(unreaded));
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private OnClickListener onMessageClick(final ArrayList<Message> readed) {
        return new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                readed.remove(0);
                if (readed.size() != 0) {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("action", "sendmessage"));
                    nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(index)));
                    nameValuePairs.add(new BasicNameValuePair("idMessage", String.valueOf(readed.get(0)
                            .get_index())));

                    PhpData.postData(PozivnoiActivity.this, nameValuePairs);

                    Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);

                    Node errorNode = doc.getElementsByTagName("error").item(0);

                    if (Integer.parseInt(errorNode.getTextContent()) == 1)
                        new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                                .setMessage("Сервер недоступен перезагрузите приложение.")
                                .setNeutralButton("Закрыть", null).show();
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PozivnoiActivity.this);
                        builder.setTitle(readed.get(0).getDate().toGMTString());
                        builder.setMessage(readed.get(0).getText());
                        builder.setNeutralButton("Ок", onMessageClick(readed));
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    // if password type password then check password then loginWithPozivnoi
                    Intent intent;
                    boolean isPassword = settings.getBoolean("isPassword", false);
                    if (!isPassword)
                        intent = new Intent(PozivnoiActivity.this, MainListActivity.class);
                    else
                        intent = new Intent(PozivnoiActivity.this, PasswordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", index);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }

        };
    }
}
