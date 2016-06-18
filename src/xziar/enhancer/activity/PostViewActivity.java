package xziar.enhancer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class PostViewActivity extends AppCompatActivity
{
	private PostBean post;
	@BindView(R.id.describe)
	TextView describe;
	@BindView(R.id.actbar)
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_view);
		ViewInject.inject(this);
		toolbar.setTitle("Hello Here");

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int pid = getIntent().getIntExtra("pid", -1);
		viewTask.post("pid", pid);
	}

	private NetBeanTask<PostBean> viewTask = new NetBeanTask<PostBean>("/postview", "post",
			PostBean.class, false)
	{
		@Override
		protected void onSuccess(final PostBean data)
		{
			post = data;
			String txt = data.getDescribe() + data.getDescribe() + data.getDescribe();
			describe.setText(Html.fromHtml(txt));
		}
	};
}
