package xziar.enhancer.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.TaskAdapter;

public class TaskListFragment extends Fragment
{
	private Activity act;
	private View view;
	private RecyclerView list;
	private TaskAdapter adapter;

	public TaskListFragment(Activity act)
	{
		this.act = act;
	}

	private void initData()
	{
		ArrayList<TaskBean> ds = new ArrayList<>();
		TaskBean task;
		{
			task = new TaskBean();
			task.setTitle("aaaa");
			task.setTime_start(10000);
			ds.add(task);
		}
		{
			task = new TaskBean();
			task.setTitle("bbbb");
			task.setTime_start(20000);
			ds.add(task);
		}
		{
			task = new TaskBean();
			task.setTitle("cccccccc");
			task.setTime_start(5000);
			ds.add(task);
		}
		Log.v("init data", "ds size : " + ds.size());
		adapter.refresh(ds);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_tasklist, container, false);
		list = (RecyclerView) view.findViewById(R.id.list);
		adapter = new TaskAdapter(act);
		list.setAdapter(adapter);
		list.setLayoutManager(new LinearLayoutManager(act,
				LinearLayoutManager.VERTICAL, false));
		initData();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

}
