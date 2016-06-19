package xziar.enhancer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class AddTaskActivity extends AppCompatActivity
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
		actbar.setTitle("话题");
		actbar.setSubtitle("发起人");

		actbar.setupActionBar(this);
		actbar.setBackButton(true);

		viewTask.init(this);
		int tid = getIntent().getIntExtra("tid", -1);
		viewTask.post("tid", tid);
	}

	private NetBeanTask<TaskBean> viewTask = new NetBeanTask<TaskBean>("/taskview", "task",
			TaskBean.class, false)
	{
		@Override
		protected void onSuccess(TaskBean data)
		{
			task = data;
			String txt = task.getDescribe() + task.getDescribe() + task.getDescribe();
			describe.setText(Html.fromHtml(txt));
			actbar.setTitle(task.getTitle());
			actbar.setSubtitle(task.getLauncher());
		}
	};
}
