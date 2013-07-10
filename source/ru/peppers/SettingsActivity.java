package ru.peppers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");

		final CheckBox box = (CheckBox) findViewById(R.id.checkBox3);
		SharedPreferences settings = getSharedPreferences(PozivnoiActivity.PREFS_NAME, 0);
		boolean checked = settings.getBoolean("isPassword", false);
		box.setChecked(checked);
		box.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveIsPassword(isChecked);
			}

		});

		final EditText passwordEditText = (EditText) findViewById(R.id.editText1);
		passwordEditText.setText(settings.getString("passwordApp", ""));
		passwordEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (passwordEditText.getText().length() == 0)
					box.setChecked(false);
				else
					box.setChecked(true);
				saveIsPassword(box.isChecked());

				SharedPreferences settings = getSharedPreferences(PozivnoiActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("passwordApp", passwordEditText.getText().toString());
				editor.commit();
				Log.d("My_tag", "passwordApp" + passwordEditText.getText().toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		final EditText pozivnoiEditText = (EditText) findViewById(R.id.editText2);
		InputFilter filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
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
						|| event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (!event.isShiftPressed() && pozivnoiEditText.getText().toString().length() != 0) {
						SharedPreferences settings = getSharedPreferences(PozivnoiActivity.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean("isFirstTime", false);
						editor.putString("pozivnoidata", pozivnoiEditText.getText().toString());
						editor.putString("password", "");
						editor.putString("login", "");
						editor.commit();

						setResult(RESULT_OK);
						finish();
					} else
						emptyPozivnoiError();

				}
				return false; // pass on to other listeners.
			}

		});

	}

	private void saveIsPassword(boolean isChecked) {
		SharedPreferences settings = getSharedPreferences(PozivnoiActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isPassword", isChecked);

		// Commit the edits!
		editor.commit();
	}

	private void emptyPozivnoiError() {
		new AlertDialog.Builder(this).setTitle(this.getString(R.string.error_title))
				.setMessage(this.getString(R.string.empty_pozivnoi))
				.setNeutralButton(this.getString(R.string.close), null).show();
	}
}
