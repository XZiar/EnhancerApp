package xziar.enhancer.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.ReplyBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class ReplyAdapter extends CommonAdapter<ReplyBean, ReplyAdapter.TaskHolder>
{
	static class TaskHolder extends CommonHolder<ReplyBean>
	{
		@BindView
		private TextView content, replyer, time, floor;
		private Date sDate;
		@SuppressLint("SimpleDateFormat")
		private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		public TaskHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
			sDate = new Date();
		}

		@Override
		public void setData(ReplyBean data, int idx)
		{
			content.setText(data.getDescribe());
			replyer.setText(data.getReplyer());
			sDate.setTime(data.getTime_reply());
			time.setText(sdf.format(sDate));
			floor.setText(idx + "¥");
		}

	}

	public ReplyAdapter(Context context)
	{
		super(context, TaskHolder.class);
		resID.add(R.layout.item_reply);
	}

	@Override
	public int compare(ReplyBean lhs, ReplyBean rhs)
	{
		return (int) (lhs.getTime_reply() - rhs.getTime_reply());// asc
	}
}
