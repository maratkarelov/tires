package education.karelov.tires2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import java.lang.reflect.Field;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private APIClient mAPIClient;
    private DataAdapter mAdapter;
    private LoaderManager.LoaderCallbacks loaderCallbacks;
    private Uri mUri;
    private int loaderID;

    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("myLogs", "onItemClick typeQuery = ");
            Cursor cur = (Cursor) mAdapter.getItem(position);
            cur.moveToPosition(position);
            String value = cur.getString(cur.getColumnIndexOrThrow("value"));
            String key = cur.getString(cur.getColumnIndexOrThrow("_id"));
            String[] selectionArgs = new String[1];
            Bundle bundle = new Bundle();
            if (mUri == MyContentProvider.YEARS_URI) {
                mUri = MyContentProvider.MAKERS_URI;
                loaderID = MyContentProvider.MAKERS;
                bundle.putString("selection", "selection = ?");
                selectionArgs = new String[]{value};
            } else if (mUri == MyContentProvider.MAKERS_URI) {
                mUri = MyContentProvider.MODELS_URI;
                loaderID = MyContentProvider.MODELS;
                bundle.putString("selection", "selection = ?");
                selectionArgs = new String[]{value};
            } else if (mUri == MyContentProvider.MODELS_URI) {
                mUri = MyContentProvider.SUBMODELS_URI;
                loaderID = MyContentProvider.SUBMODELS;
                bundle.putString("selection", "selection = ?");
                selectionArgs = new String[]{value};
            } else if (mUri == MyContentProvider.SUBMODELS_URI) {
                mUri = MyContentProvider.TIRE_INFO_URI;
                loaderID = MyContentProvider.TIRE_INFO;
                bundle.putString("selection", "baseId = ?");
                selectionArgs = new String[]{key};
            }
            bundle.putString("uri", mUri.toString());
            bundle.putStringArray("projection", MyContentProvider.PROJECTION);
            bundle.putStringArray("selectionArgs", selectionArgs);
            Loader loader = getSupportLoaderManager().getLoader(loaderID);
            if (loader != null) {
                getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
            }
            getSupportLoaderManager().initLoader(loaderID, bundle, loaderCallbacks);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        loaderCallbacks = this;
//        MyTask myTask = new MyTask();
//        myTask.execute();
        // create adapter for ListView
        mAdapter = new DataAdapter(this, null, 0);
        ListView listView = (ListView) findViewById(R.id.list_content);
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setAdapter(mAdapter);
        mUri = MyContentProvider.YEARS_URI;
        loaderID = MyContentProvider.YEARS;
        Bundle bundle = new Bundle();
        bundle.putString("uri", mUri.toString());
        bundle.putStringArray("projection", MyContentProvider.PROJECTION);
        getSupportLoaderManager().initLoader(loaderID, bundle, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUri == MyContentProvider.TIRE_INFO_URI) {
            mUri = MyContentProvider.SUBMODELS_URI;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String stringUri = arg1.getString("uri");
        Uri uri = Uri.parse(stringUri);
        String[] projection = arg1.getStringArray("projection");
        String selection = arg1.getString("selection");
        String[] selectionArgs = arg1.getStringArray("selectionArgs");

        return new MyCursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newData) {
        if (loaderID == MyContentProvider.TIRE_INFO) {
            startInfoActivity(newData);
        } else {
            mAdapter.changeCursor(newData);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void startInfoActivity(Cursor cursor) {
        cursor.moveToPosition(0);
        Intent intent = new Intent(this, Activity_TireInfo.class);
        TireInfo tireInfo = new TireInfo();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            String nameColumn = cursor.getColumnName(i);
            String valueColumn = cursor.getString(i);
            if (!valueColumn.isEmpty()) {
                Class<?> c = tireInfo.getClass();
                Field field = null;
                try {
                    field = c.getDeclaredField(nameColumn);
                    field.set(tireInfo, valueColumn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        intent.putExtra(TireInfo.class.getCanonicalName(), tireInfo);
        startActivity(intent);
    }

    public void clickBack(View view) {
        if (mUri == MyContentProvider.MAKERS_URI) {
            mUri = MyContentProvider.YEARS_URI;
        } else if (mUri == MyContentProvider.MODELS_URI) {
            mUri = MyContentProvider.MAKERS_URI;
        } else if (mUri == MyContentProvider.SUBMODELS_URI) {
            mUri = MyContentProvider.MODELS_URI;
        }
        loaderID = MyContentProvider.uriMatcher.match(mUri);
        Bundle bundle = new Bundle();
        bundle.putString("uri", mUri.toString());
        bundle.putStringArray("projection", MyContentProvider.PROJECTION);
        getSupportLoaderManager().getLoader(loaderID).forceLoad();
    }

    static class MyCursorLoader extends CursorLoader {
        Context context;
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;

        public MyCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
            this.context = context;
            this.uri = uri;
            this.projection = projection;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null, null);
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }
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