package education.karelov.tires2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by alex on 23.09.14.
 */
public class PushNotificationUtil {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String regId;
    private static GoogleCloudMessaging mGcm;

    public static boolean checkPlayServices(Activity activity, final Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //todo add behavior
            }
            return false;
        }
        return true;
    }
    public static void registerInBackground(GoogleCloudMessaging gcm, final Context context) {
        mGcm = gcm;//todo пересмотреть решение

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = mGcm.register("287028139260");
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String o) {
                super.onPostExecute(o);
            }
        }.execute();

    }

}


