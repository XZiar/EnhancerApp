package xziar.enhancer.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;

public class TaskAdapter extends CommonAdapter<TaskBean, TaskAdapter.TaskHolder>
{
	static class TaskHolder extends CommonAdapter.CommonHolder<TaskBean>
	{
		private TextView title, time;

		public TaskHolder(View itemView)
		{
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			time = (TextView) itemView.findViewById(R.id.time);
		}

		@Override
		public void setData(TaskBean data)
		{
			title.setText(data.getTitle());
			time.setText(data.getTime_start() + "");
		}

	}

	public TaskAdapter(Context context)
	{
		super(context, TaskHolder.class);
		resID = R.layout.item_task;
	}

	@Override
	public int compare(TaskBean lhs, TaskBean rhs)
	{
		return (int) (lhs.getTime_start() - rhs.getTime_start());
	}
}
