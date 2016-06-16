package xziar.enhancer.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
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
		HashMap<String, Integer> dat = new HashMap<>();
		dat.put("from", 0);
		listTask.post(dat);
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
		HashMap<String, Integer> dat = new HashMap<>();
		dat.put("tid", data.getTid());
		viewTask.post(dat);
	}

	private NetTask<List<TaskBean>> listTask = new NetTask<List<TaskBean>>("/app/task")
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
	
	private NetTask<TaskBean> viewTask = new NetTask<TaskBean>("/app/taskview")
	{
		@Override
		protected TaskBean parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			String msg = obj.getString("msg");
			if (obj.getBooleanValue("success"))
			{
				return JSON.parseObject(obj.getString("task"), TaskBean.class);
			}
			throw new ParseResultFailException(msg);
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			super.onUnsuccess(code, data);
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(getActivity(), "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final TaskBean data)
		{
			AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("»°Ìâ")
					.setView(R.layout.dialog_post).setPositiveButton("OK", null)
					.setNegativeButton("CANCEL", null).create();
			dlg.show();
			((TextView) dlg.findViewById(R.id.txt)).setText(Html.fromHtml(data.getDescribe()));
		}
	};
}
