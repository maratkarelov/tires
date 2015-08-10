package education.karelov.tires2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DataAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public DataAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
        String name = cur.getString(cur.getColumnIndex(MyContentProvider.VALUE));
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            String inCash = cur.getString(cur.getColumnIndex(MyContentProvider.IN_CASH));
            if (inCash.equals("false")) {
                view.setBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.GRAY);
            }
            holder.tvName.setText(name);
        }
    }

    @Override
    public View newView(Context ctx, Cursor cur, ViewGroup parent) {
        View root = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewHolder holder = new ViewHolder();
        TextView tvName = (TextView) root.findViewById(android.R.id.text1);
        holder.tvName = tvName;
        root.setTag(holder);
        return root;
    }

    public static class ViewHolder {
        public TextView tvName;
    }

}
