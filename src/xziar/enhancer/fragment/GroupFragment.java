package xziar.enhancer.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.GroupAdapter;
import xziar.enhancer.pojo.AccountBean;
import xziar.enhancer.pojo.GroupBean;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;
import xziar.enhancer.widget.ActionBar;

@ObjView("view")
public class GroupFragment extends Fragment
		implements OnItemClickListener<GroupBean>, OnRefreshListener
{
	private View view;
	private ActionBar actbar;
	private GroupAdapter adapter;
	private ArrayList<GroupBean> groups;
	@BindView(R.id.listwrap)
	private SwipeRefreshLayout listwrap;
	@BindView(R.id.list)
	private RecyclerView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_group, container, false);
		ViewInject.inject(this);
		actbar = ((MainActivity) getActivity()).getActbar();
		setHasOptionsMenu(true);
		adapter = new GroupAdapter(getActivity());
		adapter.setOnItemClickListener(this);
		list.setAdapter(adapter);
		listwrap.setOnRefreshListener(this);
		listwrap.setRefreshing(true);
		refreshData();

		return view;
	}

	private void refreshData()
	{
		listTask.post();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (!hidden)
			actbar.setMenu(R.menu.menu_view);
		super.onHiddenChanged(hidden);
	}

	@Override
	public void OnClick(GroupBean data)
	{
		int type = adapter.changeViewType(data);
		Toast.makeText(getActivity(), data.getName() + " : " + type, Toast.LENGTH_SHORT).show();
	}

	private NetBeansTask<GroupBean> listTask = new NetBeansTask<GroupBean>("/mygroups", "groups",
			GroupBean.class)
	{

		@Override
		protected List<GroupBean> parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			try
			{
				JSONObject obj = JSON.parseObject(data.string());
				if (obj.getBooleanValue("success"))
				{
					List<GroupBean> groups = new ArrayList<>();
					List<JSONObject> jos = JSON.parseArray(obj.getString(datname),
							JSONObject.class);
					for (JSONObject jo : jos)
					{
						GroupBean group = new GroupBean(new AccountBean());
						group.setDescribe(jo.getString("describe"));
						group.setName(jo.getString("name"));
						ArrayList<String> stus = new ArrayList<>(
								JSON.parseArray(jo.getString("stus"), String.class));
						group.setStus(stus);
						int k = 1;
						for (String n : group.getStus())
							group.addMember(k++, 1);
						groups.add(group);
					}
					return groups;
				}
				else
					throw new ParseResultFailException(obj.getString("msg"));
			}
			catch (JSONException e)
			{
				Log.w("", "error when parse response to json array", e);
				throw new ParseResultFailException("error syntax");
			}
		}

		@Override
		protected void onDone(Message msg)
		{
			listwrap.setRefreshing(false);
		}

		@Override
		protected void onSuccess(List<GroupBean> data)
		{
			groups = new ArrayList<>(data);
			adapter.refresh(groups);
		}
	};

	@Override
	public void onRefresh()
	{
		refreshData();
	}
}
