package xziar.enhancer.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.EditText;
import xziar.enhancer.R;
import xziar.enhancer.pojo.UserBean;

public class StuRegFragment extends Fragment
{
	private Activity act;
	private View view;
	private NumberPicker time_year, time_month;
	private UserBean user;

	public StuRegFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_stu_reg, container, false);
		time_year = (NumberPicker) view.findViewById(R.id.time_year);
		time_month = (NumberPicker) view.findViewById(R.id.time_month);
		time_year.setMinValue(1900);
		time_year.setMaxValue(2020);
		time_year.setValue(Calendar.getInstance().get(Calendar.YEAR));
		time_month.setMinValue(1);
		time_month.setMaxValue(12);
		time_month.setValue(Calendar.getInstance().get(Calendar.MONTH) + 1);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}
}
