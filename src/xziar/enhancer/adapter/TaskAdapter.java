package xziar.enhancer.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class TaskAdapter extends CommonAdapter<TaskBean, TaskAdapter.TaskHolder>
{
	static class TaskHolder extends CommonHolder<TaskBean>
	{
		@BindView(R.id.title)
		private TextView title;
		@BindView(R.id.time)
		private TextView time;
		@BindView(R.id.launcher)
		private TextView launcher;
		@BindView(R.id.applycount)
		private TextView applycount;
		private Date sDate;
		@SuppressLint("SimpleDateFormat")
		private static SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd");

		public TaskHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
			sDate = new Date();
		}

		@Override
		public void setData(TaskBean data)
		{
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
		resID.add(R.layout.item_task);
	}

	@Override
	public int compare(TaskBean lhs, TaskBean rhs)
	{
		return (int) (lhs.getTime_start() - rhs.getTime_start());
	}
}
