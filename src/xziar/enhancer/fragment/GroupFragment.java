package xziar.enhancer.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.GroupAdapter;
import xziar.enhancer.pojo.AccountBean;
import xziar.enhancer.pojo.GroupBean;
import xziar.enhancer.widget.ActionBar;

public class GroupFragment extends Fragment implements OnItemClickListener<GroupBean>
{
	private View view;
	private ActionBar actbar;
	private RecyclerView list;
	private GroupAdapter adapter;
	private ArrayList<GroupBean> groups;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_group, container, false);
		list = (RecyclerView) view.findViewById(R.id.list);
		actbar = ((MainActivity) getActivity()).getActbar();
		setHasOptionsMenu(true);
		adapter = new GroupAdapter(getActivity());
		adapter.setOnItemClickListener(this);
		list.setAdapter(adapter);

		initData();
		return view;
	}

	private void initData()
	{
		groups = new ArrayList<>();
		{
			GroupBean group = new GroupBean(new AccountBean());
			group.setName("asdf");
			group.setDescribe("describe");
			group.addMember(14, 0);
			groups.add(group);
		}
		{
			GroupBean group = new GroupBean(new AccountBean());
			group.setName("qwer");
			group.setDescribe("describe");
			group.addMember(14, 0);
			groups.add(group);
		}
		{
			GroupBean group = new GroupBean(new AccountBean());
			group.setName("zxcv");
			group.setDescribe("describe");
			group.addMember(14, 0);
			groups.add(group);
		}
		adapter.refresh(groups);
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
}
