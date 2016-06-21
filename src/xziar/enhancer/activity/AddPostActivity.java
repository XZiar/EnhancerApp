package xziar.enhancer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.RichTextEditor;
import xziar.enhancer.widget.WaitDialog;

public class AddPostActivity extends AppCompatActivity implements OnClickListener
{
	private WaitDialog waitDialog;
	@BindView(R.id.posttitle)
	private EditText title;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	@BindView(R.id.editor)
	private RichTextEditor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_post);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setTitle("发布新话题");
		actbar.setBackButton(true);
		actbar.addMenu(R.id.action_send, R.drawable.icon_send, false);

		waitDialog = new WaitDialog(this, " 发布中... ...");

		editor.getEditor().setPlaceholder("在这里输入你的内容");
	}

	@Override
	public void onClick(View v)
	{
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			return true;
		case R.id.action_send:
			postTask.post("post.title", title.getText(), "post.describe", editor.getContent());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private NetBeanTask<String> postTask = new NetBeanTask<String>("/addpost", "msg", String.class,
			false)
	{
		@Override
		protected void onStart()
		{
			waitDialog.show();
		}

		@Override
		protected void onDone()
		{
			waitDialog.dismiss();
		}

		@Override
		protected void onSuccess(final String data)
		{
			Toast.makeText(AddPostActivity.this, "发布话题成功", Toast.LENGTH_SHORT).show();
			Intent it = new Intent();
			it.putExtra("forum_changed", true);
			it.putExtra("pid", Integer.parseInt(data));
			setResult(RESULT_OK, it);
			finish();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(AddPostActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

	};
}
