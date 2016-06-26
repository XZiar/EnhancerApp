package xziar.enhancer.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.AccountBean.Role;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.pojo.TaskBean.Status;
import xziar.enhancer.util.NetworkUtil;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;

public class TaskFnActivity extends AppCompatActivity
		implements OnClickListener, OnItemClickListener<TaskBean>, DialogInterface.OnClickListener
{
	private boolean isStu;
	private ArrayList<TaskBean> tasks;
	private TaskBean task;
	private AlertDialog finishDlg, commentDlg;
	@BindView
	private CompatRecyclerView list;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private TaskAdapter adapter;
	private EditText comment;
	private AppCompatSpinner score;
	private View close, confirm;

	private void initDialog()
	{
		View content = getLayoutInflater().inflate(R.layout.dialog_comment, null, false);
		comment = (EditText) content.findViewById(R.id.describe);
		score = (AppCompatSpinner) content.findViewById(R.id.score);
		confirm = content.findViewById(R.id.btn_confirm);
		close = content.findViewById(R.id.action_close);
		close.setOnClickListener(this);
		confirm.setOnClickListener(this);
		commentDlg = new AlertDialog.Builder(this).setView(content).create();
		commentDlg.setCancelable(false);
		commentDlg.setCanceledOnTouchOutside(false);

		finishDlg = new AlertDialog.Builder(this).setTitle("完结任务").setMessage("确定该任务已完结吗？")
				.setPositiveButton("确认", this).setNegativeButton("放弃", null).create();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_fn);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setBackButton(true);
		adapter = new TaskAdapter(this);
		list.setAdapter(adapter);
		adapter.setOnItemClickListener(this);
		viewTask.post();
		isStu = MainActivity.user.getAccountRole() == Role.student;
		initDialog();
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

	@Override
	public void onClick(View v)
	{
		if (v == close)
		{
			commentDlg.dismiss();
		}
		else if (v == confirm)
		{
			commentTask.post("cmt", comment.getText(), "tid", task.getTid(), "score",
					score.getSelectedItemPosition() + 1);
		}
	}

	private NetBeanTask<String> finTask = new NetBeanTask<String>("/fintask", "msg", String.class)
	{
		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(TaskFnActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

		@Override
		protected void onSuccess(String data)
		{
			viewTask.post();
			Toast.makeText(TaskFnActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
		}
	};

	private NetBeanTask<JSONObject> getcommentTask = new NetBeanTask<JSONObject>("/getcomment",
			"msg", JSONObject.class)
	{

		@Override
		protected JSONObject parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			try
			{
				JSONObject obj = JSON.parseObject(data.string());
				if (obj.getBooleanValue("success"))
					return obj;
				else
					throw new ParseResultFailException(obj.getString("msg"));
			}
			catch (JSONException e)
			{
				Log.w(NetworkUtil.LogTag, "error when parse response to json", e);
				throw new ParseResultFailException("error syntax");
			}
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(TaskFnActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

		@Override
		protected void onSuccess(JSONObject data)
		{
			comment.setText(data.getString("comment"));
			int sval = data.getIntValue("score");
			if (sval < 0)
				sval = 3;
			score.setSelection(sval - 1);
		}
	};

	private NetBeansTask<TaskBean> viewTask = new NetBeansTask<TaskBean>("/fntasks", "fntasks",
			TaskBean.class)
	{
		@Override
		protected void onSuccess(List<TaskBean> data)
		{
			tasks = new ArrayList<>(data);
			adapter.refresh(tasks);
		}
	};

	private NetBeanTask<String> commentTask = new NetBeanTask<String>("/comment", "msg",
			String.class)
	{
		@Override
		protected void onSuccess(String data)
		{
			Toast.makeText(TaskFnActivity.this, "评价成功", Toast.LENGTH_SHORT).show();
			commentDlg.dismiss();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(TaskFnActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

	};

	@Override
	public void OnClick(TaskBean data)
	{
		task = data;
		if (data.getTaskStatus() == Status.ongoing)
		{
			finishDlg.show();
		}
		else
		{
			getcommentTask.post("tid", task.getTid());
			commentDlg.show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if (dialog == finishDlg && which == DialogInterface.BUTTON_POSITIVE)
		{
			finTask.post("tid", task.getTid());
		}
	}

}
