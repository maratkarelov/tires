package education.karelov.tires2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by user on 24.07.15.
 */

public class MyContentProvider extends ContentProvider {

    // // Константы для БД
    // БД
    static final String DB_NAME = "mydb1";
    static final int DB_VERSION = 1;

    // Таблица
    static final String YEARS_TABLE = "years";
    static final String MAKERS_TABLE = "makers";
    private String table = "years";

    // Поля
    static final String _ID = "_id";
    static final String VALUE = "value";

    // Скрипт создания таблицы
    static final String DB_CREATE_YEARS = "create table " + YEARS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + VALUE + " text);";
    // Скрипт создания таблицы
    static final String DB_CREATE_MAKERS = "create table " + MAKERS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + VALUE + " text);";

    // // Uri
    // authority
    static final String AUTHORITY = "education.karelov.tires2";

    // path
    static final String YEARS_PATH = "years";
    static final String MAKERS_PATH = "makers";

    // Общий Uri
    public static final Uri YEARS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + YEARS_PATH);
    public static final Uri MAKERS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MAKERS_PATH);

    // Типы данных
    // набор строк
    static final String YEARS_TYPE = "vnd.android.cursor.dir/vnd.years"
            + AUTHORITY + "." + YEARS_PATH;
    static final String MAKERS_TYPE = "vnd.android.cursor.dir/vnd.makers"
            + AUTHORITY + "." + MAKERS_PATH;


    //// UriMatcher
    // общий Uri
    static final int YEARS = 1;
    static final int MAKERS = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, YEARS_PATH, YEARS);
        uriMatcher.addURI(AUTHORITY, MAKERS_PATH, MAKERS);
    }

    public static final String[] PROJECTION = new String[]{
            _ID,
            VALUE
    };

    private static HashMap<String, String> mProjectionMap = new HashMap<String, String>();

    static {
        for (int i = 0; i < PROJECTION.length; i++) {
            mProjectionMap.put(PROJECTION[i], PROJECTION[i]);
        }
    }

    DBHelper dbHelper;
    Handler mHandlerYears;
    Handler mHandlerMakers;
    SQLiteDatabase db;
    SQLiteQueryBuilder qb;
    private APIClient mAPIClient;
    Cursor cursor;
    Uri mUri;
    MyObserver myObserverYears;
    MyObserver myObserverMakers;

    private Callback<Respond> mResponseListener = new Callback<Respond>() {
        @Override
        public void failure(RetrofitError arg0) {
            Log.e("Error", arg0.getMessage());
        }

        @Override
        public void success(Respond arg0, Response arg1) {
            List<IMap> list = new ArrayList<IMap>(arg0.data.size());
            if (arg0.data.size() > 0) {
                ContentValues cv = new ContentValues();
                for (RespondData data : arg0.data) {
                    cv.put(VALUE, data.value);
                    insert(mUri, cv);
                }
                if (table.equals("years")) {
                    table = "makers";
                } else if (table.equals("makers")) {
                    table = "models";
                } else if (table.equals("models")) {
                    table = "subModels";
                }
            }
        }
    };

    private Callback<TireInfo> mInfoListener = new Callback<TireInfo>() {
        @Override
        public void failure(RetrofitError arg0) {
            Log.e("Error", arg0.getMessage());
        }

        @Override
        public void success(TireInfo arg0, Response arg1) {
//            startInfoActivity(arg0);
        }
    };

    private void requestData(String param) {
        if (table.equals("years")) {
            mAPIClient.getYears(mResponseListener);
        } else if (table.equals("makers")) {
            mAPIClient.getMakers(param, mResponseListener);
        } else if (table.equals("models")) {
            mAPIClient.getModels(param, mResponseListener);
        } else if (table.equals("subModels")) {
            mAPIClient.getInfo(param, mInfoListener);
        }
        Log.e("myLogs", "requestData table = " + table);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        qb = new SQLiteQueryBuilder();
        db.execSQL("DROP TABLE IF EXISTS " + YEARS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MAKERS_TABLE);
        db.execSQL(DB_CREATE_YEARS);
        db.execSQL(DB_CREATE_MAKERS);
        // create REST adapter
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://demo-mob.nedis.net.ua").setConverter(new GsonConverter(gson)).build();
        mAPIClient = restAdapter.create(APIClient.class);
        mHandlerYears = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.d("handler", msg.toString());
            }
        };
        mHandlerMakers = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.d("handler", msg.toString());
            }
        };
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String param = "";
        switch (uriMatcher.match(uri)) {
            case YEARS:
                qb.setTables(YEARS_TABLE);
                qb.setProjectionMap(mProjectionMap);
                myObserverYears = new MyObserver(mHandlerYears);
                getContext().getContentResolver().registerContentObserver(uri, true, myObserverYears);
                break;
            case MAKERS:
                param = selectionArgs[0];
                qb.setTables(MAKERS_TABLE);
                qb.setProjectionMap(mProjectionMap);
                myObserverMakers = new MyObserver(mHandlerMakers);
                getContext().getContentResolver().registerContentObserver(uri, true, myObserverMakers);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        mUri = uri;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        if (cursor.getCount() == 0) {
            requestData(param);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case YEARS:
                return YEARS_TYPE;
            case MAKERS:
                return MAKERS_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db.insert(table, null, values);
        if (table.equals("years")) {
            getContext().getContentResolver().notifyChange(uri, myObserverYears);
        } else if (table.equals("makers")) {
            getContext().getContentResolver().notifyChange(uri, myObserverMakers);
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_YEARS);
            db.execSQL(DB_CREATE_MAKERS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + YEARS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MAKERS_TABLE);
        }
    }
}
