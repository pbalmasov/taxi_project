package ru.peppers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import model.Driver;
import model.Message;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

public class PozivnoiActivity extends Activity {
    private static final String MY_TAG = "My_tag";
    protected static final String PREFS_NAME = "MyNamePrefs1";
    protected Integer index;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean update = false;
        TaxiApplication.setDriver(new Driver(0, 0, 0, "", ""));
        Log.d("My_tag", "INIT DRIVER");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        try {
            if (settings.getInt("version", 0) != 0) // если не начальная версия
                if (settings.getInt("version", 0) != this.getPackageManager().getPackageInfo(
                        this.getPackageName(), 0).versionCode) { // если сохраненная версия не совпадает с
                                                                 // версией приложения
                    // обновление
                    editor.putInt("version", this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), 0).versionCode);
                    // если обновились стоит ли проверять еще раз на обновление?
                } else {
                    // последняя версия
                    Log.d("My_tag", "Newest version");
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("action", "getversion"));
                    Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);
                    if (doc != null) {
                        // проверяем версию на сервере
                        Node idNode = doc.getElementsByTagName("version").item(0);
                        index = Integer.valueOf(idNode.getTextContent());
                        if (settings.getInt("version", 0) < index) {
                            update = true;
                            // предлагаем перейти на сайт
                            initDialog();
                            // ничего не сохраняем потому что после обновления оно само сохранится
                        }
                    }
                }
            else {
                // начальная версия
                Log.d("My_tag", "First version");
                editor.putInt("version",
                        this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode);
                // если начальная версия стоит ли проверять еще раз на обновление?
            }
        } catch (NameNotFoundException e) {
            new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка").setMessage(e.toString())
                    .setNeutralButton("Закрыть", null).show();
        }
        editor.commit();
        if (!update) {
            init(settings);
        }
    }


    private void init(SharedPreferences settings) {
        boolean isFirstTime = settings.getBoolean("isFirstTime", false);
        if (!isFirstTime) {
            initRegistration();
            return;
        }
        String pozivnoi = settings.getString("pozivnoidata", "");

        if (pozivnoi == "") {
            setContentView(R.layout.login);
            EditText pozivnoiEditText = (EditText) findViewById(R.id.pozivnoiEditText);

            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                        int dend) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            pozivnoiEditText.setFilters(new InputFilter[] { filter });

            pozivnoiEditText.setOnKeyListener(new EditText.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    EditText pozivnoiEditText = (EditText) v;

                    if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        if (!event.isShiftPressed() && pozivnoiEditText.getText().toString().length() != 0)
                            return loginWithPozivnoi(pozivnoiEditText.getText().toString());
                        else
                            new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                                    .setMessage("Позывной не может быть пустым.")
                                    .setNeutralButton("Закрыть", null).show();

                    }
                    return false; // pass on to other listeners.
                }

            });

            Button passwordButton = (Button) findViewById(R.id.pozivnoiButton);
            passwordButton.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EditText pozivnoiEditText = (EditText) findViewById(R.id.pozivnoiEditText);
                    if (pozivnoiEditText.getText().toString().length() != 0)
                        loginWithPozivnoi(pozivnoiEditText.getText().toString());
                    else
                        new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                                .setMessage("Позывной не может быть пустым.")
                                .setNeutralButton("Закрыть", null).show();
                }

            });
        } else {
            loginWithPozivnoi(pozivnoi);
        }
    }

    private void initRegistration() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация");
        builder.setMessage("Введите ваш позывной или обратитесь к администратору");
        builder.setCancelable(false);

        final EditText input = new EditText(this);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                    int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        input.setFilters(new InputFilter[] { filter });

        input.setOnKeyListener(new EditText.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                EditText input = (EditText) v;

                if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed() && input.getText().toString().length() != 0){
                        getRequest(input.getText().toString());
                        return true;
                    }else
                        new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                                .setMessage("Позывной не может быть пустым.")
                                .setNeutralButton("Закрыть", null).show();

                }
                return false; // pass on to other listeners.
            }

        });

        builder.setView(input);
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                String pozivnoi = input.getText().toString();
                getRequest(pozivnoi);
            }

        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();

    String randomString( int len )
    {
       StringBuilder sb = new StringBuilder( len );
       for( int i = 0; i < len; i++ )
          sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
       return sb.toString();
    }

    private void getRequest(String pozivnoi) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String symbols = randomString(24);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "app"));
        nameValuePairs.add(new BasicNameValuePair("action","authrequest"));
        nameValuePairs.add(new BasicNameValuePair("devserial", symbols));
        nameValuePairs.add(new BasicNameValuePair("drvnumber",pozivnoi));
        //TODO: сохранить токен
        Log.d("My_tag", symbols);

        Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs,"https://www.abs-taxi.ru/fcgi-bin/office/cman.fcgi");
        if (doc != null) {
            Node errorNode = doc.getElementsByTagName("error").item(0);
            
            Log.d("My_tag",doc.getElementsByTagName("login").item(0).toString());
            
            if (Integer.parseInt(errorNode.getTextContent()) == 1)
                new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                        .setMessage("Неправильный позывной.").setNeutralButton("Закрыть", null).show();
            else {
                //TODO: сохранить логин пароль
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isFirstTime", true);
                editor.putString("pozivnoidata", pozivnoi);
                editor.commit();

                init(settings);
            }
        }
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Обновление");
        builder.setMessage("Вышло обновление скачать?");
        builder.setCancelable(false);
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://sandbox.peppers-studio.ru/dell/accelerometer/TaxiProject.apk"));
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Позже", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                init(settings);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean loginWithPozivnoi(String pozivnoi) {
        {
            Log.d(MY_TAG, pozivnoi);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("action", "savepozivnoi"));
            nameValuePairs.add(new BasicNameValuePair("pozivnoi", pozivnoi));

            Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);
            if (doc != null) {
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
            } else {

                // EditText pozivnoiEditText = (EditText) findViewById(R.id.pozivnoiEditText);
                // // may cause bugs
                //
                // InputFilter filter = new InputFilter() {
                // public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                // int dstart, int dend) {
                // for (int i = start; i < end; i++) {
                // if (!Character.isDigit(source.charAt(i))) {
                // return "";
                // }
                // }
                // return null;
                // }
                // };
                // pozivnoiEditText.setFilters(new InputFilter[] { filter });
                //
                // pozivnoiEditText.setOnKeyListener(new EditText.OnKeyListener() {
                //
                // @Override
                // public boolean onKey(View v, int keyCode, KeyEvent event) {
                //
                // EditText pozivnoiEditText = (EditText) v;
                //
                // if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
                // || event.getAction() == KeyEvent.ACTION_DOWN
                // && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                //
                // if (!event.isShiftPressed()
                // && pozivnoiEditText.getText().toString().length() != 0)
                // return loginWithPozivnoi(pozivnoiEditText.getText().toString());
                // else
                // new AlertDialog.Builder(PozivnoiActivity.this).setTitle("Ошибка")
                // .setMessage("Позывной не может быть пустым.")
                // .setNeutralButton("Закрыть", null).show();
                //
                // }
                // return false; // pass on to other listeners.
                // }
                //
                // });
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
        if (doc != null) {
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
            TaxiApplication.setDriverId(index);
            startActivity(intent);
            startService(new Intent(this, PhpService.class));
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

                    Document doc = PhpData.postData(PozivnoiActivity.this, nameValuePairs);
                    if (doc != null) {
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
                    TaxiApplication.setDriverId(index);
                    startActivity(intent);
                    startService(new Intent(PozivnoiActivity.this, PhpService.class));
                    finish();
                }
            }

        };
    }
}
