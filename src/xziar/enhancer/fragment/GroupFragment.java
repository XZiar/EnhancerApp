package xziar.enhancer.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.pojo.AccountBean;
import xziar.enhancer.pojo.GroupBean;
import xziar.enhancer.util.CommonHolder.OnItemClickListener;
import xziar.enhancer.util.GroupAdapter;

public class GroupFragment extends Fragment
		implements OnItemClickListener<GroupBean>
{
	private Activity act;
	private View view;
	private RecyclerView list;
	private GroupAdapter adapter;
	private ArrayList<GroupBean> groups;

	public GroupFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_group, container, false);
		list = (RecyclerView) view.findViewById(R.id.list);
		adapter = new GroupAdapter(act);
		adapter.setItemClick(this);
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
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void OnClick(GroupBean data)
	{
		int type = adapter.changeViewType(data);
		Toast.makeText(act, data.getName() + " : " + type, Toast.LENGTH_SHORT)
				.show();
	}
}
