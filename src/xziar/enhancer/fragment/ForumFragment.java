package xziar.enhancer.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.activity.PostViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.PostAdapter;
import xziar.enhancer.pojo.PostBean;
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
		HashMap<String, Integer> dat = new HashMap<>();
		dat.put("from", 0);
		listTask.post(dat);
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
			refreshData();
	}

	@Override
	public void OnClick(PostBean data)
	{
		Intent it = new Intent(getActivity(), PostViewActivity.class);
		it.putExtra("pid", data.getPid());
		startActivityForResult(it, 1442);
	}

	private NetTask<List<PostBean>> listTask = new NetTask<List<PostBean>>("/app/forum")
	{
		@Override
		protected List<PostBean> parse(ResponseBody data)
				throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			String msg = obj.getString("msg");
			if (obj.getBooleanValue("success"))
			{
				return JSON.parseArray(obj.getString("posts"), PostBean.class);
			}
			throw new ParseResultFailException(msg);
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(getActivity(), "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final List<PostBean> data)
		{
			ds.clear();
			ds.addAll(data);
			adapter.refresh(ds);
		}
	};
}
