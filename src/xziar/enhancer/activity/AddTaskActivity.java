package xziar.enhancer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NumberFilter;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.RichTextEditor;
import xziar.enhancer.widget.WaitDialog;

public class AddTaskActivity extends AppCompatActivity implements OnClickListener
{
	private WaitDialog waitDialog;
	@BindView(R.id.posttitle)
	private EditText title;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	@BindView(R.id.editor)
	private RichTextEditor editor;
	@BindView()
	private EditText limit_people, payment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		ViewInject.inject(this);
		waitDialog = new WaitDialog(this, " 发布中... ...");
		actbar.setupActionBar(this);
		actbar.setTitle("发布新任务");
		actbar.setBackButton(true);
		actbar.addMenu(R.id.action_send, R.drawable.icon_send, false);

		NumberFilter filter_people = new NumberFilter(1, 10)
		{
			@Override
			protected void onOutRange()
			{
				Toast.makeText(AddTaskActivity.this, "人数在1-10人内", Toast.LENGTH_SHORT).show();
			}
		};
		limit_people.setFilters(new InputFilter[] { filter_people });
		limit_people.setOnFocusChangeListener(filter_people);

		NumberFilter filter_payemnt = new NumberFilter(1, 10000)
		{
			@Override
			protected void onOutRange()
			{
				Toast.makeText(AddTaskActivity.this, "薪酬在1-10000范围内", Toast.LENGTH_SHORT).show();
			}
		};
		payment.setFilters(new InputFilter[] { filter_payemnt });
		payment.setOnFocusChangeListener(filter_payemnt);

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
			postTask.post("task.title", title.getText(), "task.limit_people",
					limit_people.getText(), "task.payment", payment.getText(), "task.describe",
					editor.getContent());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private NetBeanTask<String> postTask = new NetBeanTask<String>("/addtask", "msg", String.class)
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
			Toast.makeText(AddTaskActivity.this, "发布任务成功", Toast.LENGTH_SHORT).show();
			Intent it = new Intent();
			it.putExtra("tasklist_changed", true);
			it.putExtra("tid", Integer.parseInt(data));
			setResult(RESULT_OK, it);
			finish();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(AddTaskActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

	};
}
