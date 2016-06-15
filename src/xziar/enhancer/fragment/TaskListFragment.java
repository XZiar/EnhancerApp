package xziar.enhancer.fragment;

import java.util.ArrayList;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.TaskBean;

public class TaskListFragment extends Fragment implements OnItemClickListener<TaskBean>
{
	private View view;
	private RecyclerView list;
	private TaskAdapter adapter;
	ArrayList<TaskBean> ds;

	private void initData()
	{
		ds = new ArrayList<>();
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
		adapter.refresh(ds);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_tasklist, container, false);
		list = (RecyclerView) view.findViewById(R.id.list);
		adapter = new TaskAdapter(getActivity());
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
		Toast.makeText(getActivity(), data.getTitle(), Toast.LENGTH_SHORT).show();

		{
			TaskBean task = new TaskBean();
			task.setTitle(System.currentTimeMillis() + "");
			task.setLauncher("company");
			task.setApplycount(2);
			task.setTime_start(new Date(116, 3, 1).getTime());

			ds.add(task);
			adapter.refresh(ds);
		}
	}

}
