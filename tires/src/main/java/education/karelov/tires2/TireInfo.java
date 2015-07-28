package education.karelov.tires2;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TireInfo implements Parcelable {
	public String result;
	public String year;
	public String makeName;
	public String modelName;
	public String submodel;
	public String frb;
	public String standardInd;
	public String vehtype;
	public String speedRating;
	public String crossSection;
	public String aspect;
	public String rimSize;
	public String notes;
	public String tireSize;
	public String loadIndex;
	public String loadRange;
	public String frontInf;
	public String rearInf;

	final static String LOG_TAG = "myLogs";

	// ������� �����������
	public TireInfo() {
		Log.d(LOG_TAG, "MyObject(String _s, int _i)");
	}

	public int describeContents() {
		return 0;
	}

	// ����������� ������ � Parcel
	public void writeToParcel(Parcel parcel, int flags) {
		Log.d(LOG_TAG, "writeToParcel");
		parcel.writeString(result);
		parcel.writeString(year);
		parcel.writeString(makeName);
		parcel.writeString(modelName);
		parcel.writeString(submodel);
		parcel.writeString(frb);
		parcel.writeString(standardInd);
		parcel.writeString(vehtype);
		parcel.writeString(speedRating);
		parcel.writeString(crossSection);
		parcel.writeString(aspect);
		parcel.writeString(rimSize);
		parcel.writeString(notes);
		parcel.writeString(tireSize);
		parcel.writeString(loadIndex);
		parcel.writeString(loadRange);
		parcel.writeString(frontInf);
		parcel.writeString(rearInf);
	}

	public static final Creator<TireInfo> CREATOR = new Creator<TireInfo>() {
		// ������������� ������ �� Parcel
		public TireInfo createFromParcel(Parcel in) {
			Log.d(LOG_TAG, "createFromParcel");
			return new TireInfo(in);
		}

		public TireInfo[] newArray(int size) {
			return new TireInfo[size];
		}
	};

	// �����������, ����������� ������ �� Parcel
	private TireInfo(Parcel parcel) {
		Log.d(LOG_TAG, "TireInfo(Parcel parcel)");
		result = parcel.readString();
		year = parcel.readString();
		makeName = parcel.readString();
		modelName = parcel.readString();
		submodel = parcel.readString();
		frb = parcel.readString();
		standardInd = parcel.readString();
		vehtype = parcel.readString();
		speedRating = parcel.readString();
		crossSection = parcel.readString();
		aspect = parcel.readString();
		rimSize = parcel.readString();
		notes = parcel.readString();
		tireSize = parcel.readString();
		loadIndex = parcel.readString();
		loadRange = parcel.readString();
		frontInf = parcel.readString();
		rearInf = parcel.readString();
	}

}
