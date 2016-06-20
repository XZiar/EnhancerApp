package xziar.enhancer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import xziar.enhancer.R;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.activity.TaskViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;
import xziar.enhancer.widget.ActionBar;

@ObjView("view")
public class TaskListFragment extends Fragment
		implements OnItemClickListener<TaskBean>, OnRefreshListener
{
	private View view;
	private ActionBar actbar;
	ArrayList<TaskBean> ds = new ArrayList<>();
	private TaskAdapter adapter;
	@BindView(R.id.listwrap)
	private SwipeRefreshLayout listwrap;
	@BindView(R.id.list)
	private RecyclerView list;

	private void refreshData()
	{
		listTask.post("from", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_tasklist, container, false);
		ViewInject.inject(this);
		actbar = ((MainActivity) getActivity()).getActbar();
		setHasOptionsMenu(true);
		listwrap.setOnRefreshListener(this);
		adapter = new TaskAdapter(getActivity());
		adapter.setItemClick(this);
		list.setAdapter(adapter);
		listTask.init(getActivity());
		refreshData();
		return view;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (!hidden)
			actbar.setMenu(R.menu.menu_view);
		super.onHiddenChanged(hidden);
	}

	@Override
	public void OnClick(TaskBean data)
	{
		Intent it = new Intent(getActivity(), TaskViewActivity.class);
		it.putExtra("tid", data.getTid());
		startActivityForResult(it, 1442);
	}

	@Override
	public void onRefresh()
	{
		refreshData();
	}

	private NetBeanTask<List<TaskBean>> listTask = new NetBeanTask<List<TaskBean>>(
			"/task", "tasks", TaskBean.class, true)
	{
		@Override
		protected void onDone()
		{
			listwrap.setRefreshing(false);
		}

		@Override
		protected void onSuccess(List<TaskBean> data)
		{
			ds = new ArrayList<>(data);
			adapter.refresh(ds);
		}
	};

}
