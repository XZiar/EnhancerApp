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
import xziar.enhancer.R;
import xziar.enhancer.activity.AddPostActivity;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.activity.PostViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.PostAdapter;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;
import xziar.enhancer.widget.ActionBar;

@ObjView("view")
public class ForumFragment extends Fragment
		implements OnItemClickListener<PostBean>, OnRefreshListener
{
	private View view;
	private ActionBar actbar;
	private PostAdapter adapter;
	ArrayList<PostBean> ds = new ArrayList<>();
	@BindView(R.id.listwrap)
	private SwipeRefreshLayout listwrap;
	@BindView(R.id.list)
	private RecyclerView list;

	private static enum reqCode
	{
		login, addpost, viewpost
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_forum, container, false);
		ViewInject.inject(this);
		actbar = ((MainActivity) getActivity()).getActbar();
		setHasOptionsMenu(true);
		listwrap.setOnRefreshListener(this);
		adapter = new PostAdapter(getActivity());
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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_add:
			if (MainActivity.user != null)
			{
				Intent it = new Intent(getActivity(), AddPostActivity.class);
				startActivityForResult(it, reqCode.addpost.ordinal());
			}
			else
			{
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(it, reqCode.login.ordinal());
			}
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
			if (data.getBooleanExtra("forum_changed", false))
			{
				refreshData();
				openPost(data.getIntExtra("pid", 0));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshData()
	{
		listTask.post("from", 0);
	}

	private void openPost(int pid)
	{
		Intent it = new Intent(getActivity(), PostViewActivity.class);
		it.putExtra("pid", pid);
		startActivityForResult(it, reqCode.viewpost.ordinal());
	}

	@Override
	public void OnClick(PostBean data)
	{
		openPost(data.getPid());
	}

	@Override
	public void onRefresh()
	{
		refreshData();
	}

	private NetBeanTask<List<PostBean>> listTask = new NetBeanTask<List<PostBean>>("/forum",
			"posts", PostBean.class, true)
	{
		@Override
		protected void onDone()
		{
			listwrap.setRefreshing(false);
		}

		@Override
		protected void onSuccess(List<PostBean> data)
		{
			ds = new ArrayList<>(data);
			adapter.refresh(ds);
		}
	};

}
