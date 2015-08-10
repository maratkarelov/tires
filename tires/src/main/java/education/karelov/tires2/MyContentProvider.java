package education.karelov.tires2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by user on 24.07.15.
 */

public class MyContentProvider extends ContentProvider {

    // // Константы для БД
    // БД
    static final String DB_NAME = "mydb1";
    static final int DB_VERSION = 2;

    // Таблица
    static final String YEARS_TABLE = "years";
    static final String MAKERS_TABLE = "makers";
    static final String MODELS_TABLE = "models";
    static final String SUBMODELS_TABLE = "submodels";

    // Поля
    static final String _ID = "_id";
    static final String VALUE = "value";
    static final String IN_CASH = "inCash";

    static final String DB_CREATE_YEARS = "create table " + YEARS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + IN_CASH + " boolean, "
            + "selection text, "
            + VALUE + " text);";
    static final String DB_CREATE_MAKERS = "create table " + MAKERS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + IN_CASH + " boolean, "
            + "selection text, "
            + VALUE + " text);";
    static final String DB_CREATE_MODELS = "create table " + MODELS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + IN_CASH + " boolean, "
            + "selection text, "
            + VALUE + " text);";
    static final String DB_CREATE_SUBMODELS = "create table " + SUBMODELS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + IN_CASH + " boolean, "
            + "selection text, "
            + VALUE + " text);";

    // // Uri
    // authority
    static final String AUTHORITY = "education.karelov.tires2";

    // path
    static final String YEARS_PATH = "years";
    static final String MAKERS_PATH = "makers";
    static final String MODELS_PATH = "models";
    static final String SUBMODELS_PATH = "submodels";
    static final String TIRE_INFO_PATH = "tireinfo";

    // Общий Uri
    public static final Uri YEARS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + YEARS_PATH);
    public static final Uri MAKERS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MAKERS_PATH);
    public static final Uri MODELS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MODELS_PATH);
    public static final Uri SUBMODELS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + SUBMODELS_PATH);
    public static final Uri TIRE_INFO_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TIRE_INFO_PATH);

    // Типы данных
    // набор строк
    static final String YEARS_TYPE = "vnd.android.cursor.dir/vnd.years"
            + AUTHORITY + "." + YEARS_PATH;
    static final String MAKERS_TYPE = "vnd.android.cursor.dir/vnd.makers"
            + AUTHORITY + "." + MAKERS_PATH;
    static final String MODELS_TYPE = "vnd.android.cursor.dir/vnd.models"
            + AUTHORITY + "." + MODELS_PATH;
    static final String SUBMODELS_TYPE = "vnd.android.cursor.dir/vnd.submodels"
            + AUTHORITY + "." + SUBMODELS_PATH;
    static final String TIRE_INFO_TYPE = "vnd.android.cursor.dir/vnd.tireinfo"
            + AUTHORITY + "." + TIRE_INFO_PATH;


    //// UriMatcher
    // общий Uri
    static final int YEARS = 1;
    static final int MAKERS = 2;
    static final int MODELS = 3;
    static final int SUBMODELS = 4;
    static final int TIRE_INFO = 5;

    // описание и создание UriMatcher
    protected static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, YEARS_PATH, YEARS);
        uriMatcher.addURI(AUTHORITY, MAKERS_PATH, MAKERS);
        uriMatcher.addURI(AUTHORITY, MODELS_PATH, MODELS);
        uriMatcher.addURI(AUTHORITY, SUBMODELS_PATH, SUBMODELS);
        uriMatcher.addURI(AUTHORITY, TIRE_INFO_PATH, TIRE_INFO);
    }

    public static final String[] PROJECTION = new String[]{_ID, VALUE, IN_CASH};

    private static HashMap<String, String> mProjectionMap = new HashMap<String, String>();

    static {
        for (int i = 0; i < PROJECTION.length; i++) {
            mProjectionMap.put(PROJECTION[i], PROJECTION[i]);
        }
    }

    DBHelper dbHelper;
    SQLiteDatabase db;
    SQLiteQueryBuilder qb;
    private APIClient mAPIClient;
    Cursor cursor;
    List<RespondData> mYearsList;
    List<RespondData> mMakersList;
    List<RespondData> mModelsList;
    List<RespondData> mSubModelsList;
    TireInfo mTireInfo;

    private void requestData(int loaderID, String param) {
        if (loaderID == YEARS) {
//            Respond respond = mAPIClient.getModels("BMW");
            Respond respond = mAPIClient.getYears();
            mYearsList = respond.data;
        } else if (loaderID == MAKERS) {
            Respond respond = mAPIClient.getMakers(param);
            mMakersList = respond.data;
        } else if (loaderID == MODELS) {
            Respond respond = mAPIClient.getModels(param);
            mModelsList = respond.data;
        } else if (loaderID == SUBMODELS) {
            Respond respond = mAPIClient.getSubModels(param);
            mSubModelsList = respond.data;
        } else if (loaderID == TIRE_INFO) {
            mTireInfo = mAPIClient.getInfo(param);
        }
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        qb = new SQLiteQueryBuilder();
        db.execSQL("DROP TABLE IF EXISTS " + YEARS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MAKERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MODELS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBMODELS_TABLE);
        db.execSQL(DB_CREATE_YEARS);
        db.execSQL(DB_CREATE_MAKERS);
        db.execSQL(DB_CREATE_MODELS);
        db.execSQL(DB_CREATE_SUBMODELS);
        // create REST adapter
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://demo-mob.nedis.net.ua").setConverter(new GsonConverter(gson)).build();
        mAPIClient = restAdapter.create(APIClient.class);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String param = "";
        cursor = new MatrixCursor(new String[]{"_id", "value"});
        Cursor cursorExist;
        ContentValues cv = new ContentValues();
        int row;
        switch (uriMatcher.match(uri)) {
            case YEARS:
                // проверка существования записей
                cursorExist = db.query(YEARS_TABLE, projection, selection, selectionArgs, null, null, null);
                if (cursorExist.getCount() == 0) {
                    requestData(YEARS, "");
                    cv.clear();
                    for (int i = 0; i < mYearsList.size(); i++) {
//                        cv.put("_id", String.valueOf(i));
                        cv.put("inCash", "false");
                        cv.put("selection", mYearsList.get(i).getValue());
                        cv.put("value", mYearsList.get(i).getValue());
                        db.insert(YEARS_TABLE, null, cv);
                    }
                }
                cursor = db.query(YEARS_TABLE, projection, selection, selectionArgs, null, null, null);
//                for (RespondData respondData : mYearsList) {
//                    ((MatrixCursor)cursor).addRow(new String[]{"0", respondData.getValue()});
//                }
                return cursor;
            case MAKERS:
                cv.clear();
                cv.put("inCash", "true");
                row = db.update(YEARS_TABLE, cv, "value = ?", selectionArgs);
                // проверка существования записей
                cursorExist = db.query(MAKERS_TABLE, projection, selection, selectionArgs, null, null, null);
                if (cursorExist.getCount() == 0) {
                    param = selectionArgs[0];
                    requestData(MAKERS, param);
                    cv.clear();
                    for (int i = 0; i < mMakersList.size(); i++) {
                        cv.put("inCash", "false");
                        cv.put("selection", param);
                        cv.put("value", mMakersList.get(i).getValue());
                        long row1 = db.insert(MAKERS_TABLE, null, cv);
                    }
                }
                cursor = db.query(MAKERS_TABLE, projection, selection, selectionArgs, null, null, null);
//                for (RespondData respondData : mMakersList) {
//                    cursor.addRow(new String[]{"0", respondData.getValue()});
//                }
                return cursor;
            case MODELS:
                cv.clear();
                cv.put("inCash", "true");
                row = db.update(MAKERS_TABLE, cv, "value = ?", selectionArgs);
                // проверка существования записей
                cursorExist = db.query(MODELS_TABLE, projection, selection, selectionArgs, null, null, null);
                if (cursorExist.getCount() == 0) {
                    param = selectionArgs[0];
                    requestData(MODELS, param);
                    cv.clear();
                    for (int i = 0; i < mModelsList.size(); i++) {
                        cv.put("inCash", "false");
                        cv.put("selection", param);
                        cv.put("value", mModelsList.get(i).getValue());
                        long row1 = db.insert(MODELS_TABLE, null, cv);
                    }
                }
                cursor = db.query(MODELS_TABLE, projection, selection, selectionArgs, null, null, null);
//                for (RespondData respondData : mModelsList) {
//                    cursor.addRow(new String[]{"0", respondData.getValue()});
//                }
                return cursor;
            case SUBMODELS:
                cv.clear();
                cv.put("inCash", "true");
                row = db.update(MODELS_TABLE, cv, "value = ?", selectionArgs);
                // проверка существования записей
                cursorExist = db.query(SUBMODELS_TABLE, projection, selection, selectionArgs, null, null, null);
                if (cursorExist.getCount() == 0) {
                    param = selectionArgs[0];
                    requestData(SUBMODELS, param);
                    cv.clear();
                    for (int i = 0; i < mSubModelsList.size(); i++) {
                        cv.put("inCash", "false");
                        cv.put("selection", param);
                        cv.put("value", mSubModelsList.get(i).getValue());
                        long row1 = db.insert(SUBMODELS_TABLE, null, cv);
                    }
                }
                cursor = db.query(SUBMODELS_TABLE, projection, selection, selectionArgs, null, null, null);
//                for (RespondData respondData : mSubModelsList) {
//                    cursor.addRow(new String[]{respondData.getKey(), respondData.getValue()});
//                }
                return cursor;
            case TIRE_INFO:
                param = selectionArgs[0];
                requestData(TIRE_INFO, param);
//                qb.setTables(MAKERS_TABLE);
//                qb.setProjectionMap(mProjectionMap);
//                myObserverMakers = new MyObserver(mHandlerMakers);
//                getContext().getContentResolver().registerContentObserver(uri, true, myObserverMakers);
//                break;
                String[] fieldsString = new String[TireInfo.class.getFields().length + 1];
                String[] valuesString = new String[TireInfo.class.getFields().length + 1];
                Field[] fields = TireInfo.class.getFields();
                for (int i = 0; i < fields.length; i++) {
                    fieldsString[i] = fields[i].getName();
                    Field field = fields[i];
                    String value = "";
                    if (field.getType().getName().equals("java.lang.String")) {
                        try {
                            value = (String) field.get(mTireInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    valuesString[i] = value;
                }
                fieldsString[fields.length] = "_id";
                valuesString[fields.length] = "0";
//                cursor = new MatrixCursor(fieldsString);
//                cursor.addRow(valuesString);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

//        cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
//        mUri = uri;
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        if (cursor.getCount() == 0) {
//            requestData(param);
//        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case YEARS:
                return YEARS_TYPE;
            case MAKERS:
                return MAKERS_TYPE;
            case MODELS:
                return MODELS_TYPE;
            case SUBMODELS:
                return SUBMODELS_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
//        db.insert(table, null, values);
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
            db.execSQL("DROP table " + YEARS_TABLE + " if exist;");
            db.execSQL("DROP table " + MAKERS_TABLE + " if exist;");
            db.execSQL("DROP table " + MODELS_TABLE + " if exist;");
            db.execSQL("DROP table " + SUBMODELS_TABLE + " if exist;");
            db.execSQL(DB_CREATE_YEARS);
            db.execSQL(DB_CREATE_MAKERS);
            db.execSQL(DB_CREATE_MODELS);
            db.execSQL(DB_CREATE_SUBMODELS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (oldVersion == 1 && newVersion == 2) {

                ContentValues cv = new ContentValues();
                db.beginTransaction();
                try {
                    cv.put("isCash", "false");
                    db.execSQL("DROP table " + YEARS_TABLE + " if exist;");
                    db.execSQL("DROP table " + MAKERS_TABLE + " if exist;");
//                    db.execSQL("alter table " + YEARS_TABLE + " add column inCash boolean NOT NULL default false;");
//                    db.execSQL("alter table " + MAKERS_TABLE + " add column inCash boolean NOT NULL default false;");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }

    }
}
