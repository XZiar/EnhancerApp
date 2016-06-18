package xziar.enhancer.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class TaskViewActivity extends AppCompatActivity
{
	private TaskBean task;
	public boolean tmp = false;
	@BindView(R.id.describe)
	TextView describe;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);
		ViewInject.inject(this);
		int tid = getIntent().getIntExtra("tid", -1);
		HashMap<String, Integer> dat = new HashMap<>();
		dat.put("tid", tid);
		viewTask.post(dat);
	}

	private NetBeanTask<TaskBean> viewTask = new NetBeanTask<TaskBean>("/taskview", "task",
			TaskBean.class, false)
	{
		@Override
		protected void onSuccess(TaskBean data)
		{
			task = data;
			describe.setText(Html.fromHtml(data.getDescribe()));
		}
	};
}
