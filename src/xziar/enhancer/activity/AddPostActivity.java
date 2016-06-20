package xziar.enhancer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.EditText;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class AddPostActivity extends AppCompatActivity
{
	private TaskBean task;
	@BindView(R.id.posttitle)
	private EditText title;
	@BindView(R.id.content)
	private EditText content;
	@BindView(R.id.btn_post)
	private Button btn_post;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private CardView cv;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);
		ViewInject.inject(this);
		actbar.setTitle("����");
		actbar.setSubtitle("������");


		// viewTask.init(this);
		// int tid = getIntent().getIntExtra("tid", -1);
		// viewTask.post("tid", tid);
	}

	// private NetBeanTask<TaskBean> viewTask = new NetBeanTask<TaskBean>("/taskview", "task",
	// TaskBean.class, false)
	// {
	// @Override
	// protected void onSuccess(TaskBean data)
	// {
	// task = data;
	// String txt = task.getDescribe() + task.getDescribe() + task.getDescribe();
	// describe.setText(Html.fromHtml(txt));
	// actbar.setTitle(task.getTitle());
	// actbar.setSubtitle(task.getLauncher());
	// }
	// };
}