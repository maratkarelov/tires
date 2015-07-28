package education.karelov.tires2;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by user on 27.07.15.
 */
public class MyObserver extends ContentObserver {
    public MyObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        String s = "";
        // do s.th.
        // depending on the handler you might be on the UI
        // thread, so be cautious!
    }
}