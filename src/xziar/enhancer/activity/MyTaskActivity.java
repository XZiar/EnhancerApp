package xziar.enhancer.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;

public class MyTaskActivity extends AppCompatActivity implements OnItemClickListener<TaskBean>
{
	private static enum reqCode
	{
		viewtask
	}

	private ArrayList<TaskBean> tasks;
	@BindView
	private CompatRecyclerView list;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private TaskAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_task);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setBackButton(true);
		adapter = new TaskAdapter(this);
		list.setAdapter(adapter);
		adapter.setOnItemClickListener(this);
		listTask.post();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private NetBeansTask<TaskBean> listTask = new NetBeansTask<TaskBean>("/mytask", "tasks",
			TaskBean.class)
	{
		@Override
		protected void onSuccess(List<TaskBean> data)
		{
			tasks = new ArrayList<>(data);
			adapter.refresh(tasks);
		}
	};

	@Override
	public void OnClick(TaskBean data)
	{
		Intent it = new Intent(this, TaskViewActivity.class);
		it.putExtra("tid", data.getTid());
		startActivityForResult(it, reqCode.viewtask.ordinal());
	}

}
