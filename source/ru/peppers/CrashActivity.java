package ru.peppers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
/**
 * При ошибке
 * @author p.balmasov
 */
public class CrashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Отсутсвуют данные. Отправьте отчет об ошибке по возможности.");

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { "shadrinov@ntechs.ru" });
        i.putExtra(Intent.EXTRA_SUBJECT, "Ошибка");
        i.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("crash"));
        try {
            startActivity(Intent.createChooser(i, "Отправка письма"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "У вас не настроен почтовый сервис", Toast.LENGTH_SHORT).show();
        }

    }
}
