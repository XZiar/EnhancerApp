package xziar.enhancer.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.GroupBean;

public class GroupAdapter
		extends CommonAdapter<GroupBean, GroupAdapter.GroupHolder>
{
	static class GroupHolder extends CommonHolder<GroupBean>
	{
		private TextView title, time, launcher, applycount;

		public GroupHolder(View itemView)
		{
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			launcher = (TextView) itemView.findViewById(R.id.launcher);
			time = (TextView) itemView.findViewById(R.id.time);
			applycount = (TextView) itemView.findViewById(R.id.applycount);
		}

		@Override
		public void setData(GroupBean data)
		{
			title.setText(data.getName());
			launcher.setText(data.getDescribe());
			time.setText(data.getLeaderID() + "");
			applycount.setText(data.getPeople() + "»À");
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
