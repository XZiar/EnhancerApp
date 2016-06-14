package xziar.enhancer.fragment;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import xziar.enhancer.R;
import xziar.enhancer.pojo.StudentBean;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("view")
public class StuRegFragment extends Fragment
{
	private Activity act;
	private View view;
	@BindView(R.id.time_year)
	private NumberPicker time_year;
	@BindView(R.id.time_month)
	private NumberPicker time_month;
	@BindView(R.id.realname)
	private EditText name;
	@BindView(R.id.sgender)
	private RadioGroup sgender;
	@BindView(R.id.id_person)
	private EditText pid;
	@BindView(R.id.id_student)
	private EditText sid;
	@BindView(R.id.school)
	private EditText school;

	public StuRegFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_stu_reg, container, false);
		ViewInject.inject(this);
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

	public HashMap<String, String> getData()
	{
		HashMap<String, String> data = new HashMap<>();
		data.put("stu.name", name.getText().toString());
		data.put("stu.id_person", pid.getText().toString());
		data.put("stu.id_student", sid.getText().toString());
		data.put("stu.stime_enter", String.format("%04d-%02d",
				time_year.getValue(), time_month.getValue()));
		data.put("stu.sgender",
				String.valueOf(sgender.getCheckedRadioButtonId() == R.id.male));
		data.put("stu.school", school.getText().toString());
		return data;
	}
}
