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
import xziar.enhancer.activity.PostViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.PostAdapter;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("view")
public class ForumFragment extends Fragment
		implements OnItemClickListener<PostBean>, OnRefreshListener
{
	private View view;
	private PostAdapter adapter;
	ArrayList<PostBean> ds = new ArrayList<>();
	@BindView(R.id.listwrap)
	private SwipeRefreshLayout listwrap;
	@BindView(R.id.list)
	private RecyclerView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_forum, container, false);
		ViewInject.inject(this);
		listwrap.setOnRefreshListener(this);
		adapter = new PostAdapter(getActivity());
		adapter.setItemClick(this);
		list.setAdapter(adapter);

		refreshData();
		return view;
	}

	private void refreshData()
	{
		listTask.post("from", 0);
	}

	@Override
	public void OnClick(PostBean data)
	{
		Intent it = new Intent(getActivity(), PostViewActivity.class);
		it.putExtra("pid", data.getPid());
		startActivityForResult(it, 1442);
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
