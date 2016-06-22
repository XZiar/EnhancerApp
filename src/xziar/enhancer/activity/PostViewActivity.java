package xziar.enhancer.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.adapter.ReplyAdapter;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.pojo.ReplyBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;
import xziar.enhancer.widget.FloatLabelLayout;
import xziar.enhancer.widget.RichTextEditor;

public class PostViewActivity extends AppCompatActivity implements OnClickListener
{
	private static enum reqCode
	{
		login
	}

	private PostBean post;
	private ReplyAdapter adapter;
	private ArrayList<ReplyBean> replys = new ArrayList<>();
	@BindView
	private FloatLabelLayout replypart;
	@BindView(onClick = "this")
	private Button btn_send;
	@BindView(R.id.editor)
	private RichTextEditor editor;
	@BindView(R.id.describe)
	private TextView describe;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	@BindView(R.id.comments)
	private CompatRecyclerView comments;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_view);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setTitle("话题");
		actbar.setSubtitle("发起人");
		actbar.setMenu(R.menu.menu_view);
		actbar.setBackButton(true);

		adapter = new ReplyAdapter(this);
		comments.setAdapter(adapter);
		refreshView();
		int pid = getIntent().getIntExtra("pid", -1);
		viewTask.post("pid", pid);
		replyTask.post("pid", pid);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			break;
		case R.id.action_add:
			if (MainActivity.user == null)
			{
				Intent it = new Intent(this, LoginActivity.class);
				startActivityForResult(it, reqCode.login.ordinal());
			}
			else
			{
				comments.scrollToIndex(-2);
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void refreshView()
	{
		if (MainActivity.user != null)
			adapter.setFooterView(replypart);
		else
			adapter.setFooterView(null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data != null)
		{
			if (data.getBooleanExtra("user_changed", false))
			{
				refreshView();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_send)
			postTask.post("reply.pid", post.getPid(), "reply.describe", editor.getContent());
	}

	private NetBeanTask<PostBean> viewTask = new NetBeanTask<PostBean>("/postview", "post",
			PostBean.class)
	{
		@Override
		protected void onSuccess(final PostBean data)
		{
			post = data;
			String txt = post.getDescribe();
			describe.setText(Html.fromHtml(txt));
			actbar.setTitle(post.getTitle());
			actbar.setSubtitle(post.getPoster());
		}
	};

	private NetBeansTask<ReplyBean> replyTask = new NetBeansTask<ReplyBean>("/replyview", "replys",
			ReplyBean.class)
	{
		@Override
		protected void onSuccess(final List<ReplyBean> data)
		{
			replys = new ArrayList<>(data);
			adapter.refresh(replys);
		}
	};

	private NetBeanTask<String> postTask = new NetBeanTask<String>("/addreply", "post",
			String.class)
	{
		@Override
		protected void onSuccess(String data)
		{
			replyTask.post("pid", post.getPid());
		}
	};

}
