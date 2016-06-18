package xziar.enhancer.fragment;

import java.io.IOException;
import java.util.ArrayList;
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
import xziar.enhancer.activity.TaskViewActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetTask;

public class TaskListFragment extends Fragment implements OnItemClickListener<TaskBean>
{
	private View view;
	private RecyclerView list;
	private TaskAdapter adapter;
	ArrayList<TaskBean> ds = new ArrayList<>();;

	private void refreshData()
	{
		listTask.post("from", 0);
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

		refreshData();
		return view;
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
		if(!hidden)
			refreshData();
	}
	
	@Override
	public void OnClick(TaskBean data)
	{
		Intent it = new Intent(getActivity(), TaskViewActivity.class);
		it.putExtra("tid", data.getTid());
		startActivityForResult(it, 1442);
	}

	private NetTask<List<TaskBean>> listTask = new NetTask<List<TaskBean>>("/app/task", true)
	{
		@Override
		protected List<TaskBean> parse(ResponseBody data)
				throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			if (obj.getBooleanValue("success"))
				return JSON.parseArray(obj.getString("tasks"), TaskBean.class);
			else
				throw new ParseResultFailException(obj.getString("msg"));
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(getActivity(), "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(List<TaskBean> data)
		{
			ds.clear();
			ds.addAll(data);
			adapter.refresh(ds);
		}
	};
	

}
