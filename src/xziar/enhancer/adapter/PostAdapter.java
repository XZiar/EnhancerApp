package xziar.enhancer.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class PostAdapter extends CommonAdapter<PostBean, PostAdapter.PostHolder>
{
	static class PostHolder extends CommonHolder<PostBean>
	{
		@BindView
		private TextView title, time, poster, replycount;
		private Date sDate;
		@SuppressLint("SimpleDateFormat")
		private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		public PostHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
			sDate = new Date();
		}

		@Override
		public void setData(PostBean data, int idx, int type)
		{
			title.setText(data.getTitle());
			poster.setText(data.getPoster());
			sDate.setTime(data.getTime_post());
			time.setText(sdf.format(sDate));
			replycount.setText(data.getReplycount() + "»Àªÿ∏¥");
		}

	}

	public PostAdapter(Context context)
	{
		super(context, PostHolder.class);
		resID.add(R.layout.item_post);
	}

	@Override
	public int compare(PostBean lhs, PostBean rhs)
	{
		return (int) -(lhs.getTime_post() - rhs.getTime_post());// desc
	}
}
