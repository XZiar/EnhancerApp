package xziar.enhancer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class PostViewActivity extends AppCompatActivity
{
	private PostBean post;
	@BindView(R.id.describe)
	private TextView describe;
	@BindView(R.id.actbar)
	private ActionBar actbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_view);
		ViewInject.inject(this);
		actbar.setTitle("话题");
		actbar.setSubtitle("发起人");

		actbar.setupActionBar(this);
		actbar.setMenu(R.menu.menu_view);
		actbar.setBackButton(true);
		viewTask.init(this);
		int pid = getIntent().getIntExtra("pid", -1);
		viewTask.post("pid", pid);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 0x102002c:
		case R.id.action_back:
			finish();
			break;
		case R.id.action_add:
			Toast.makeText(this, "press add", Toast.LENGTH_SHORT).show();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private NetBeanTask<PostBean> viewTask = new NetBeanTask<PostBean>("/postview", "post",
			PostBean.class, false)
	{
		@Override
		protected void onSuccess(final PostBean data)
		{
			post = data;
			String txt = post.getDescribe() + post.getDescribe() + post.getDescribe();
			describe.setText(Html.fromHtml(txt));
			actbar.setTitle(post.getTitle());
			actbar.setSubtitle(post.getPoster());
		}
	};
}
