package education.karelov.tires2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class Activity_TireInfo extends Activity {

	public TireInfo mTirePressure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tireinfo);
		mTirePressure = (TireInfo) getIntent().getParcelableExtra(TireInfo.class.getCanonicalName());
		fillData();

	}

	protected void fillData() {
		if (mTirePressure != null) {
			((TextView) findViewById(R.id.frg_tire_pressure_content_name)).setText("" + mTirePressure.year + " " + mTirePressure.makeName + "\n("
					+ mTirePressure.modelName + " " + mTirePressure.submodel + ")");
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_frb)).setText(mTirePressure.frb);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_size)).setText(mTirePressure.tireSize);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_speed_rating)).setText(mTirePressure.speedRating);
			
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_front_inflation)).setText(mTirePressure.frontInf);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_rear_inflation)).setText(mTirePressure.rearInf);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_standart_optional)).setText(mTirePressure.standardInd);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_vehtype)).setText(mTirePressure.vehtype);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_cross_section)).setText(mTirePressure.crossSection);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_aspect)).setText(mTirePressure.aspect);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_rim_size)).setText(mTirePressure.rimSize);
			 ((TextView) findViewById(R.id.frg_tire_pressure_content_notes)).setText(mTirePressure.notes);
			 
		}
	}

	public void clickBack(View view) {
		finish();
	}

}
