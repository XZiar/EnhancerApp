package xziar.enhancer.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.GroupBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class GroupAdapter extends CommonAdapter<GroupBean, GroupAdapter.GroupHolder>
{
	static class GroupHolder extends CommonHolder<GroupBean>
	{
		@BindView
		private TextView name, people, describe;

		public GroupHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
		}

		@Override
		public void setData(GroupBean data, int idx, int type)
		{
			name.setText(data.getName());
			describe.setText(data.getDescribe());
			people.setText(data.getPeople() + "��");
		}

	}

	public GroupAdapter(Context context)
	{
		super(context, GroupHolder.class);
		resID.add(R.layout.item_group);
		resID.add(R.layout.item_group_detail);
	}

	@Override
	public int compare(GroupBean lhs, GroupBean rhs)
	{
		return 0;
	}
}
