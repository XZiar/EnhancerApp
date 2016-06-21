package xziar.enhancer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class TaskViewActivity extends AppCompatActivity
{
	private TaskBean task;
	@BindView(R.id.describe)
	private TextView describe;
	@BindView(R.id.actbar)
	private ActionBar actbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setTitle("任务");
		actbar.setSubtitle("发起人");
		actbar.setMenu(R.menu.menu_view);
		actbar.setBackButton(true);
		int tid = getIntent().getIntExtra("tid", -1);
		viewTask.post("tid", tid);
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
			Toast.makeText(this, "press add", Toast.LENGTH_SHORT).show();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private NetBeanTask<TaskBean> viewTask = new NetBeanTask<TaskBean>("/taskview", "task",
			TaskBean.class)
	{
		@Override
		protected void onSuccess(TaskBean data)
		{
			task = data;
			String txt = task.getDescribe();
			describe.setText(Html.fromHtml(txt));
			actbar.setTitle(task.getTitle());
			actbar.setSubtitle(task.getLauncher());
		}
	};
}
