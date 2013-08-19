package ru.peppers;

import java.util.List;

import org.apache.http.NameValuePair;
import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class MyTask extends AsyncTask<List<NameValuePair>, Void, Document> {

	private AsyncTaskCompleteListener<Document> callback;
	private Context activity;
	private ProgressDialog progress;
	public MyTask(Context activity,ProgressDialog progress,AsyncTaskCompleteListener<Document> callback){
		this.callback = callback;
		this.activity = activity;
		this.progress = progress;
	}
	
	@Override
	protected Document doInBackground(List<NameValuePair>... arg0) {
		return PhpData.postData(activity, arg0[0], PhpData.newURL);
	}
	
	 protected void onPreExecute() {
         this.progress.show();
     }

	 @Override
     protected void onPostExecute(Document doc) {
		 this.progress.hide();
		 callback.onTaskComplete(doc);
     }
	 
}
