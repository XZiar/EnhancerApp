package xziar.enhancer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;

public class TaskAdapter extends CommonAdapter<TaskBean, TaskAdapter.TaskHolder>
{
	static class TaskHolder extends CommonHolder<TaskBean>
	{
		private TextView title, time, launcher, applycount;
		private Date sDate;
		@SuppressLint("SimpleDateFormat")
		private static SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd");

		public TaskHolder(View itemView)
		{
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			launcher = (TextView) itemView.findViewById(R.id.launcher);
			time = (TextView) itemView.findViewById(R.id.time);
			applycount = (TextView) itemView.findViewById(R.id.applycount);
			sDate = new Date();
		}

		@Override
		public void setData(TaskBean data)
		{
			super.setData(data);
			title.setText(data.getTitle());
			launcher.setText(data.getLauncher());
			sDate.setTime(data.getTime_start());
			time.setText(sdf.format(sDate));
			applycount.setText(data.getApplycount() + "»À…Í«Î");
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
