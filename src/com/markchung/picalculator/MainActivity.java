package com.markchung.picalculator;

import java.text.NumberFormat;

import com.google.ads.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	private static final String TAG = "picalculator";
	private static final String myAdID = "a1505198753026e";
	private static final String myTestDevice = "BA76119486D364D047D0C789B4F61E46";
	private static final String myTestDevice2 = "CF95DC53F383F9A836FD749F3EF439CD";
	private AdView adview;
	private Spinner m_type;
	private EditText m_edit_PMT;
	private EditText m_edit_NPER;
	private EditText m_edit_RATE;
	private EditText m_edit_FV;
	private EditText m_last_edit;
	private Button m_button;
	ColorStateList defColor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);

		// CreateAdRequest(this, (LinearLayout) findViewById(R.id.adview));
		m_type = (Spinner) findViewById(R.id.spinner_target);
		m_edit_PMT = (EditText) findViewById(R.id.editPMT);
		m_edit_NPER = (EditText) findViewById(R.id.editNPER);
		m_edit_RATE = (EditText) findViewById(R.id.editRATE);
		m_edit_FV = (EditText) findViewById(R.id.editFV);
		m_button = (Button) findViewById(R.id.eval_calcelate);
		m_button.setOnClickListener(this);
		m_type.setOnItemSelectedListener(this);
		defColor = m_edit_FV.getTextColors();
		if (savedInstanceState == null) {
			SharedPreferences sets = getSharedPreferences(TAG, 0);
			m_type.setSelection(sets.getInt("TYPE", 0));
			m_edit_PMT.setText(sets.getString("PMT", ""));
			m_edit_NPER.setText(sets.getString("NPER", ""));
			m_edit_RATE.setText(sets.getString("RATE", ""));
			m_edit_FV.setText(sets.getString("FV", ""));
		}
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
		edit.putString("PMT", m_edit_PMT.getText().toString());
		edit.putString("NPER", m_edit_NPER.getText().toString());
		edit.putString("RATE", m_edit_RATE.getText().toString());
		edit.putString("FV", m_edit_FV.getText().toString());
		edit.commit();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	void CreateAdRequest(Activity activity, LinearLayout view) {
		adview = new AdView(activity, AdSize.BANNER, myAdID);
		view.addView(adview);
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice(myTestDevice);
		adRequest.addTestDevice(myTestDevice2);
		adview.loadAd(adRequest);
	}

	private double getDoubleValue(EditText e) throws Exception {
		try{
			return Double.parseDouble(e.getText().toString());
		}catch(Exception err){
			e.requestFocus();
			throw err;
		}
	}

	private int getIntValue(EditText e) throws Exception{
		try{
			return Integer.parseInt(e.getText().toString());
		}catch(Exception err){
			e.requestFocus();
			throw err;
		}
	}

	private EditText getSelect(int position) {
		EditText edit = null;
		switch (position) {
		case 3:
			edit = m_edit_PMT;
			break;
		case 2:
			edit = m_edit_NPER;
			break;
		case 1:
			edit = m_edit_RATE;
			break;
		case 0:
			edit = m_edit_FV;
			break;
		}
		return edit;
	}

	@Override
	public void onClick(View v) {
		EditText edit = getSelect(m_type.getSelectedItemPosition());
		double pmt, rate, fv;
		int nper;
		NumberFormat nr = NumberFormat.getNumberInstance();
		nr.setMaximumFractionDigits(1);

		try {
			if (edit == m_edit_PMT) {
				rate = getDoubleValue(this.m_edit_RATE)/(12*100);
				fv = getDoubleValue(this.m_edit_FV);
				nper = getIntValue(this.m_edit_NPER);
				pmt = Finance.PMT(rate, nper, 0, fv) * -1;
				edit.setText(Long.toString(Math.round(pmt)));
			}else if(edit == m_edit_NPER){
				pmt = getDoubleValue(this.m_edit_PMT);
				//nper = getIntValue(this.m_edit_NPER);
				rate = getDoubleValue(this.m_edit_RATE)/(12*100);
				fv = getDoubleValue(this.m_edit_FV);
				nper = Finance.NPER(rate, -pmt, 0,fv);
				edit.setText(Integer.toString(nper));
			}else if(edit == m_edit_FV){
				pmt = getDoubleValue(this.m_edit_PMT);
				nper = getIntValue(this.m_edit_NPER);
				rate = getDoubleValue(this.m_edit_RATE)/(12*100);
				//fv = getDoubleValue(this.m_edit_FV);
				fv = Finance.FV(rate, nper, -pmt);
				edit.setText(Long.toString(Math.round(fv)));
			}else if(edit == m_edit_RATE){
				pmt = getDoubleValue(this.m_edit_PMT);
				nper = getIntValue(this.m_edit_NPER);
				//rate = getDoubleValue(this.m_edit_RATE);
				fv = getDoubleValue(this.m_edit_FV);
				rate = Finance.RATE(nper, -pmt, 0,fv)*12*100;
				edit.setText(nr.format(rate));
				
			}
			Toast.makeText(this, this.getString(R.string.msgCalculated),
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, this.getString(R.string.msgEdit_field_isNull),
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long id) {
		EditText edit = getSelect(position);
		if (m_last_edit != null) {
			m_last_edit.setEnabled(true);

			m_last_edit.setFocusableInTouchMode(true);
			m_last_edit.setClickable(true);
			m_last_edit.setTextColor(defColor);
			m_last_edit.requestFocus();
		}
		if (edit != null) {
			edit.setText("");
			edit.setEnabled(false);
			edit.setFocusable(false);
			edit.setClickable(false);
			edit.setTextColor(getResources().getColor(R.color.result));
		}
		m_last_edit = edit;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
