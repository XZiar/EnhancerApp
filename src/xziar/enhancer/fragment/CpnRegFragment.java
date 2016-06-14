package xziar.enhancer.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import xziar.enhancer.R;
import xziar.enhancer.pojo.UserBean;

public class CpnRegFragment extends Fragment
{
	private Activity act;
	private View view;
	
	public CpnRegFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_cpn_reg, container, false);
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
		/*data.put("stu.name", name.getText().toString());
		data.put("stu.id_person", pid.getText().toString());
		data.put("stu.id_student", sid.getText().toString());
		data.put("stu.stime_enter", String.format("%04d-%02d",
				time_year.getValue(), time_month.getValue()));
		data.put("stu.sgender",
				String.valueOf(sgender.getCheckedRadioButtonId() == R.id.male));
		data.put("stu.school", school.getText().toString());*/
		return data;
	}
}
