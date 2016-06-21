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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.activity.AddTaskActivity;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.activity.TaskViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.AccountBean.Role;
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

	private static enum reqCode
	{
		login, addtask, viewtask
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
		{
			actbar.setMenu(R.menu.menu_view);
			if (MainActivity.user != null && MainActivity.user.getAccountRole() != Role.company)
				actbar.disableButton(R.id.action_add);
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_add:
			if (MainActivity.user != null)
			{
				if (MainActivity.user.getAccountRole() == Role.company)
				{
					Intent it = new Intent(getActivity(), AddTaskActivity.class);
					startActivityForResult(it, reqCode.addtask.ordinal());
				}
				else
					Toast.makeText(getActivity(), "只有企业用户才能发布任务", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(it, reqCode.login.ordinal());
			}
			break;
		case R.id.action_top:
			list.smoothScrollToPosition(0);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data != null)
		{
			if (data.getBooleanExtra("tasklist_changed", false))
			{
				refreshData();
				openTask(data.getIntExtra("tid", 0));
			}
			if (data.getBooleanExtra("user_changed", false))
			{
				if (MainActivity.user != null & MainActivity.user.getAccountRole() != Role.company)
					actbar.disableButton(R.id.action_add);
				else
					actbar.enableButton(R.id.action_add);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void openTask(int tid)
	{
		Intent it = new Intent(getActivity(), TaskViewActivity.class);
		it.putExtra("tid", tid);
		startActivityForResult(it, reqCode.viewtask.ordinal());
	}

	@Override
	public void OnClick(TaskBean data)
	{
		openTask(data.getTid());
	}

	@Override
	public void onRefresh()
	{
		refreshData();
	}

	private void refreshData()
	{
		listTask.post("from", 0);
	}

	private NetBeanTask<List<TaskBean>> listTask = new NetBeanTask<List<TaskBean>>("/task", "tasks",
			TaskBean.class, true)
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
