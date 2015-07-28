package education.karelov.tires2;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private APIClient mAPIClient;
    private DataAdapter mAdapter;

    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("myLogs", "onItemClick typeQuery = ");
            Cursor cur = (Cursor) mAdapter.getItem(position);
            cur.moveToPosition(position);
            String value = cur.getString(cur.getColumnIndexOrThrow("value"));
            String selection = "value = ?";
            String[] selectionArgs = {value};
            Cursor cursor = getContentResolver().query(MyContentProvider.MAKERS_URI, MyContentProvider.PROJECTION, selection, selectionArgs, null, null);
            mAdapter.swapCursor(cursor);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        MyTask myTask = new MyTask();
//        myTask.execute();
        // create adapter for ListView
        mAdapter = new DataAdapter(this, null, 0);
        ListView listView = (ListView) findViewById(R.id.list_content);
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(
                this,
                MyContentProvider.YEARS_URI,
                MyContentProvider.PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newData) {
        mAdapter.swapCursor(newData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    class MyTask extends AsyncTask<Void, Void, String> {
        private String regId;

        @Override
        protected String doInBackground(Void... params) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplication());
            String msg = "";
            try {
                regId = gcm.register("67304131357");
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return regId;
        }

        @Override
        protected void onPostExecute(String regId) {
            super.onPostExecute(regId);
            Gson gson = new GsonBuilder().create();
            PostJsonForPushNotification obj = new PostJsonForPushNotification(regId, "moto");
            String json = gson.toJson(obj);
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://192.168.1.136:8080/push/rest/devices").setConverter(new GsonConverter(gson)).build();
            mAPIClient = restAdapter.create(APIClient.class);
            mAPIClient.register(obj, callback);

        }
    }

    private Callback<ResponseForPushNotification> callback = new Callback<ResponseForPushNotification>() {
        @Override
        public void failure(RetrofitError arg0) {
            Log.e("Error", arg0.getMessage());
        }

        @Override
        public void success(ResponseForPushNotification arg0, Response arg1) {
            Log.e("success", arg0.toString());
        }
    };
}