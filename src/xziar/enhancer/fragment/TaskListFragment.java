package xziar.enhancer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import xziar.enhancer.R;

public class TaskListFragment extends Fragment
{
	private Activity act;
	private View view;

	public TaskListFragment(Activity act)
	{
		this.act = act;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_tasklist, container, false);
		((TextView) view.findViewById(R.id.txt)).setText("TaskList");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

}
