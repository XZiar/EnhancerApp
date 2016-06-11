package xziar.enhancer.fragment;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.CommonHolder.OnItemClickListener;
import xziar.enhancer.util.TaskAdapter;

public class TaskListFragment extends Fragment
		implements OnItemClickListener<TaskBean>
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
			task.setLauncher("company");
			task.setApplycount(2);
			task.setTime_start(new Date(116, 3, 1).getTime());
			ds.add(task);
		}
		{
			task = new TaskBean();
			task.setTitle("bbbb");
			task.setLauncher("company");
			task.setApplycount(2);
			task.setTime_start(new Date(116, 6, 11).getTime());
			ds.add(task);
		}
		{
			task = new TaskBean();
			task.setTitle("cccccccc");
			task.setLauncher("company0");
			task.setApplycount(10);
			task.setTime_start(new Date(116, 2, 17).getTime());
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
		adapter.setItemClick(this);
		list.setAdapter(adapter);

		initData();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void OnClick(TaskBean data)
	{
		Toast.makeText(act, data.getTitle(), Toast.LENGTH_SHORT).show();
	}

}
