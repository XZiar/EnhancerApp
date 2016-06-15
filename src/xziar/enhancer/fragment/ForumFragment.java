package xziar.enhancer.fragment;

import java.util.ArrayList;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.PostAdapter;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("view")
public class ForumFragment extends Fragment implements OnItemClickListener<PostBean>
{
	private View view;
	@BindView(R.id.list)
	private RecyclerView list;
	private PostAdapter adapter;
	ArrayList<PostBean> ds = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_forum, container, false);
		ViewInject.inject(this);
		adapter = new PostAdapter(getActivity());
		adapter.setItemClick(this);
		list.setAdapter(adapter);

		refreshData();
		return view;
	}

	private void refreshData()
	{
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		if (!hidden)
		{
			NetworkUtil.Test(task);
		}
	}

	private NetTask<String> task = new NetTask<String>("")
	{
		@Override
		protected void onSuccess(final String data)
		{
			super.onSuccess(data);
			PostBean post = new PostBean();
			post.setTitle(data);
			post.setPoster("company");
			post.setReplycount(2);
			post.setTime_post(System.currentTimeMillis());
			ds.add(post);
			adapter.refresh(ds);
		}
	};

	@Override
	public void OnClick(PostBean data)
	{
		PostBean post = new PostBean();
		post.setTitle(System.currentTimeMillis() + "");
		post.setPoster(data.getPoster());
		post.setReplycount(2);
		post.setTime_post(new Date(116, 3, 1).getTime());
		ds.add(post);
		adapter.refresh(ds);
	}
}
