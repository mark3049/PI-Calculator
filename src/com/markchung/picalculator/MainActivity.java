package com.markchung.picalculator;

import java.text.NumberFormat;

import com.google.ads.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	private static final String TAG = "picalculator";
	private static final String myAdID = "a1505198753026e";
	private static final String myTestDevice = "BA76119486D364D047D0C789B4F61E46";
	private static final String myTestDevice2 = "CF95DC53F383F9A836FD749F3EF439CD";
	private AdView adview;
	private Spinner m_type;
	private Spinner m_interval;
	private EditText m_edit_year;
	private EditText m_edit_month;
	private int m_last_select;
	private View m_resultView;
	private Button m_button;
	private Button m_periodYearPlusButton;
	private Button m_periodYearMinusButton;
	private Button m_periodMonthPlusButton;
	private Button m_periodMonthMinusButton;
	ColorStateList defColor, defTextColor;
	int m_months;
	private EditText m_edits[] = new EditText[5];
	private TextView m_show[] = new TextView[5];
	private static final int ID_FV = 0;
	private static final int ID_RATE = 1;
	private static final int ID_NPER = 2;
	private static final int ID_PMT = 3;
	private static final int ID_PV = 4;
	private static final int resID[] = { R.id.editFV, R.id.editRATE,
			R.id.periodYearEdit, R.id.editPMT, R.id.editPV, };
	private static final int ShowResID[] = { R.id.TextFV, R.id.TextRATE,
			R.id.TextPeriod, R.id.TextPMT, R.id.TextPV };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_last_select = -1;
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);

		CreateAdRequest(this, (LinearLayout) findViewById(R.id.adview));
		m_type = (Spinner) findViewById(R.id.spinner_target);
		m_interval = (Spinner) findViewById(R.id.interval);
		for (int i = 0; i < resID.length; ++i) {
			m_edits[i] = (EditText) findViewById(resID[i]);
			m_show[i] = (TextView) findViewById(ShowResID[i]);
		}

		m_edit_year = m_edits[ID_NPER];
		m_edit_month = (EditText) findViewById(R.id.periodMonthEdit);

		m_button = (Button) findViewById(R.id.eval_calcelate);
		m_resultView = findViewById(R.id.result);
		m_periodYearPlusButton = (Button) findViewById(R.id.periodYearPlusButton);
		m_periodYearMinusButton = (Button) findViewById(R.id.periodYearMinusButton);
		m_periodMonthPlusButton = (Button) findViewById(R.id.periodMonthPlusButton);
		m_periodMonthMinusButton = (Button) findViewById(R.id.periodMonthMinusButton);

		m_periodYearPlusButton.setOnClickListener(this);
		m_periodYearMinusButton.setOnClickListener(this);
		m_periodMonthPlusButton.setOnClickListener(this);
		m_periodMonthMinusButton.setOnClickListener(this);
		m_button.setOnClickListener(this);
		m_type.setOnItemSelectedListener(this);

		defColor = m_edits[0].getTextColors();
		defTextColor = m_show[0].getTextColors();

		if (savedInstanceState == null) {
			SharedPreferences sets = getSharedPreferences(TAG, 0);
			m_type.setSelection(sets.getInt("TYPE", 0));
			m_interval.setSelection(sets.getInt("INTERVAL", 2));
			m_edits[ID_PV].setText(sets.getString("PV", "0"));
			m_edits[ID_PMT].setText(sets.getString("PMT", ""));
			m_months = sets.getInt("NPER", 20 * 12);
			m_edit_year.setText(Integer.toString(m_months / 12));
			m_edit_month.setText(Integer.toString(m_months % 12));
			m_edits[ID_RATE].setText(sets.getString("RATE", ""));
			m_edits[ID_FV].setText(sets.getString("FV", ""));
		}
		m_resultView.setVisibility(View.GONE);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
		// 0);
	}

	@Override
	protected void onDestroy() {
		if (adview != null)
			adview.destroy();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		SharedPreferences settings = getSharedPreferences(TAG, 0);
		SharedPreferences.Editor edit = settings.edit();
		edit.putInt("TYPE", m_type.getSelectedItemPosition());
		edit.putInt("INTERVAL", m_interval.getSelectedItemPosition());
		edit.putString("PV", m_edits[ID_PV].getText().toString());
		edit.putString("PMT", m_edits[ID_PMT].getText().toString());
		edit.putInt("NPER", m_months);
		edit.putString("RATE", m_edits[ID_RATE].getText().toString());
		edit.putString("FV", m_edits[ID_FV].getText().toString());
		edit.commit();
		super.onStop();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
	void CreateAdRequest(Activity activity, LinearLayout view) {
		adview = new AdView(activity, AdSize.BANNER, myAdID);
		view.addView(adview);
		AdRequest adRequest = new AdRequest();
		if (BuildConfig.DEBUG) {
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
			adRequest.addTestDevice(myTestDevice);
			adRequest.addTestDevice(myTestDevice2);
		}
		adview.loadAd(adRequest);
	}

	private double getDoubleValue(EditText e) throws Exception {
		try {
			return Double.parseDouble(e.getText().toString());
		} catch (Exception err) {
			e.requestFocus();
			throw err;
		}
	}
	private static final double Interval_scale[]={52,26,12,4,1};
	private void setRate(double rate){
		int i = this.m_interval.getSelectedItemPosition();
		rate *=Interval_scale[i]*100;
		NumberFormat nr = NumberFormat.getNumberInstance();
		nr.setMaximumFractionDigits(3);
		m_edits[ID_RATE].setText(nr.format(rate));		
	}
	private double getRate() throws Exception {
		double rate;
		rate = getDoubleValue(m_edits[ID_RATE])/100.0;
		int i = this.m_interval.getSelectedItemPosition();
		rate /= Interval_scale[i];
		return rate;
	}
	private int getNPER(){
		int i = this.m_interval.getSelectedItemPosition();
		double n = m_months/12.0*Interval_scale[i]; 
		return Math.round((float)n);
	}
	private void setNPER(int nper){
		int i = this.m_interval.getSelectedItemPosition();
		double m = nper*12.0/Interval_scale[i];
		m_months = Math.round((float)m);
	}

	private void updatePeriod() {
		m_edit_year.setText(Integer.toString(m_months / 12));
		m_edit_month.setText(Integer.toString(m_months % 12));
	}

	private void updateResult(double fv, double pv, int nper, double pmt) {
		TextView view = (TextView) this.findViewById(R.id.total_value);
		view.setText(Long.toString(Math.round((fv))));
		view = (TextView) this.findViewById(R.id.total_interest);
		view.setText(Long.toString(Math.round((fv - nper * pmt - pv))));
		view = (TextView) this.findViewById(R.id.total_payment);
		view.setText(Long.toString(Math.round((nper * pmt + pv))));

	}

	private void onCalculate() {
		int id = m_type.getSelectedItemPosition();
		double pmt, rate, fv, pv;
		int nper;
		NumberFormat nr = NumberFormat.getNumberInstance();
		nr.setMaximumFractionDigits(1);
		try {
			switch (id) {
			case ID_PV:
				// pv = getDoubleValue(this.m_edit_PV)/(12*100);
				pmt = getDoubleValue(this.m_edits[ID_PMT]);
				nper = getNPER();
				rate = getRate();
				fv = getDoubleValue(this.m_edits[ID_FV]);
				pv = Finance.PV(rate, nper, -pmt, fv, 1);
				m_edits[id].setText(Long.toString(Math.round(pv * -1)));
				break;
			case ID_PMT:
				pv = getDoubleValue(this.m_edits[ID_PV]);
				rate = getRate();
				fv = getDoubleValue(this.m_edits[ID_FV]);
				// nper = getIntValue(this.m_edit_year)*12;
				nper = getNPER();
				pmt = Finance.PMT(rate, nper, -pv, fv, 1) * -1;
				m_edits[id].setText(Long.toString(Math.round(pmt)));
				break;
			case ID_NPER:
				pv = getDoubleValue(this.m_edits[ID_PV]);
				pmt = getDoubleValue(this.m_edits[ID_PMT]);
				// nper = getIntValue(this.m_edit_NPER);
				rate = getRate();
				fv = getDoubleValue(this.m_edits[ID_FV]);
				nper = Finance.NPER(rate, -pmt, -pv, fv, 1);
				setNPER(nper);
				updatePeriod();
				break;
			case ID_FV:
				pv = getDoubleValue(this.m_edits[ID_PV]);
				pmt = getDoubleValue(this.m_edits[ID_PMT]);
				// nper = getIntValue(this.m_edit_year)*12;
				nper = getNPER();
				rate = getRate();
				// fv = getDoubleValue(this.m_edit_FV);
				fv = Finance.FV(rate, nper, -pmt, -pv, 1);
				m_edits[id].setText(Long.toString(Math.round(fv)));
				break;
			case ID_RATE:

				pv = getDoubleValue(this.m_edits[ID_PV]);
				pmt = getDoubleValue(this.m_edits[ID_PMT]);
				// nper = getIntValue(this.m_edit_year)*12;
				nper = getNPER();
				// rate = getDoubleValue(this.m_edit_RATE);
				fv = getDoubleValue(this.m_edits[ID_FV]);
				rate = Finance.RATE(nper, -pmt, -pv, fv);
				setRate(rate);
				break;
			default:
				return;
			}
			Log.v(TAG, String.format("nper=%d month=%d rate=%f", nper,m_months,rate*100));
			updateResult(fv, pv, nper, pmt);
			m_resultView.setVisibility(View.VISIBLE);
			Toast.makeText(this, this.getString(R.string.msgCalculated),
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, this.getString(R.string.msgEdit_field_isNull),
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.m_button) {
			this.onCalculate();
			return;
		} else if (v == m_periodYearPlusButton) {
			m_months += 12;
		} else if (v == m_periodYearMinusButton) {
			m_months -= 12;
			if (m_months < 1)
				m_months = 1;
		} else if (v == m_periodMonthPlusButton) {
			m_months += 1;
		} else if (v == m_periodMonthMinusButton) {
			m_months -= 1;
			if (m_months < 1)
				m_months = 1;
		} else {
			return;
		}
		this.updatePeriod();

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long id) {

		if (m_last_select != -1) {
			m_show[m_last_select].setTextColor(defTextColor);

			if (m_last_select != ID_NPER) {
				m_edits[m_last_select].setEnabled(true);

				m_edits[m_last_select].setFocusableInTouchMode(true);
				m_edits[m_last_select].setClickable(true);
				m_edits[m_last_select].setTextColor(defColor);
				m_edits[m_last_select].requestFocus();
			}
		}
		int color = getResources().getColor(R.color.result);
		if (position != ID_NPER) {
			m_edits[position].setText("");
			m_edits[position].setEnabled(false);
			m_edits[position].setFocusable(false);
			m_edits[position].setClickable(false);
			m_edits[position].setTextColor(color);
		}
		m_show[position].setTextColor(color);
		m_last_select = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
