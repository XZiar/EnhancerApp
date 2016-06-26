package xziar.enhancer.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import xziar.enhancer.R;
import xziar.enhancer.adapter.ApplyerAdapter;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.TaskAdapter;
import xziar.enhancer.pojo.AccountBean.Role;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;

public class TaskOgActivity extends AppCompatActivity
		implements OnClickListener, OnItemClickListener<TaskBean>
{
	private boolean isStu;
	private ArrayList<TaskBean> tasks;
	private TaskBean task;
	private AlertDialog acceptDlg, confirmDlg;
	@BindView
	private CompatRecyclerView list;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private ViewSwitcher switcher;
	private CompatRecyclerView applyerlist;
	private TaskAdapter adapter;
	private ApplyerAdapter apadapter;
	private TextView reason;
	private AppCompatCheckBox license;
	private View back, close, accept, confirm;

	private OnItemClickListener<JSONObject> onChooseApplicant = new OnItemClickListener<JSONObject>()
	{
		@Override
		public void OnClick(JSONObject data)
		{
			accept.setTag(data);
			reason.setText(data.getString("des"));
			switcher.showNext();
		}
	};

	private void initDialog()
	{
		if (isStu)
		{
			View content = getLayoutInflater().inflate(R.layout.dialog_confirm, null, false);
			reason = (TextView) content.findViewById(R.id.describe);
			license = (AppCompatCheckBox) content.findViewById(R.id.license);
			close = content.findViewById(R.id.action_close);
			confirm = content.findViewById(R.id.btn_confirm);
			close.setOnClickListener(this);
			confirm.setOnClickListener(this);
			confirmDlg = new AlertDialog.Builder(this).setView(content).create();
			confirmDlg.setCancelable(false);
			confirmDlg.setCanceledOnTouchOutside(false);
			try
			{
				InputStream ins = getResources().openRawResource(R.raw.license);
				String lictxt = CharStreams.toString(new InputStreamReader(ins, "UTF-8"));
				reason.setText(lictxt);
				ins.close();
			}
			catch (IOException e)
			{
				Log.e("ReadFile", "fail read lic", e);
			}
		}
		else
		{
			View content = getLayoutInflater().inflate(R.layout.dialog_accept, null, false);
			switcher = (ViewSwitcher) content.findViewById(R.id.switcher);
			applyerlist = (CompatRecyclerView) content.findViewById(R.id.applyerlist);
			reason = (TextView) content.findViewById(R.id.describe);
			license = (AppCompatCheckBox) content.findViewById(R.id.license);
			back = content.findViewById(R.id.action_back);
			accept = content.findViewById(R.id.btn_accept);
			switcher.setInAnimation(
					AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
			switcher.setOutAnimation(
					AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
			apadapter = new ApplyerAdapter(this);
			applyerlist.setAdapter(apadapter);
			apadapter.setOnItemClickListener(onChooseApplicant);
			back.setOnClickListener(this);
			accept.setOnClickListener(this);
			acceptDlg = new AlertDialog.Builder(this).setView(content).create();
			acceptDlg.setCancelable(false);
			acceptDlg.setCanceledOnTouchOutside(false);
		}
		reason.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_og);
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
		if (v == back)
		{
			View cur = switcher.getCurrentView();
			if (cur == applyerlist)
				acceptDlg.dismiss();
			else
				switcher.showPrevious();
		}
		else if (v == accept)
		{
			if (!license.isChecked())
				Toast.makeText(this, "请阅读并同意外包协议", Toast.LENGTH_SHORT).show();
			else
				acceptTask.post("tid", task.getTid(), "uid",
						((JSONObject) v.getTag()).getIntValue("uid"));
		}
		else if (v == close)
		{
			confirmDlg.dismiss();
		}
		else if (v == confirm)
		{
			if (!license.isChecked())
				Toast.makeText(this, "请阅读并同意外包协议", Toast.LENGTH_SHORT).show();
			else
				confirmTask.post("tid", task.getTid(), "uid", task.getAid());
		}
	}

	private NetBeanTask<String> confirmTask = new NetBeanTask<String>("/confirmapplyer", "msg",
			String.class)
	{
		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(TaskOgActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

		@Override
		protected void onSuccess(String data)
		{
			Toast.makeText(TaskOgActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
			viewTask.post();
			confirmDlg.dismiss();
		}
	};

	private NetBeanTask<String> acceptTask = new NetBeanTask<String>("/acceptapplyer", "msg",
			String.class)
	{
		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(TaskOgActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}

		@Override
		protected void onSuccess(String data)
		{
			Toast.makeText(TaskOgActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
			acceptDlg.dismiss();
		}
	};

	private NetBeansTask<TaskBean> viewTask = new NetBeansTask<TaskBean>("/ogtasks", "ogtasks",
			TaskBean.class)
	{
		@Override
		protected void onSuccess(List<TaskBean> data)
		{
			tasks = new ArrayList<>(data);
			adapter.refresh(tasks);
		}
	};

	private NetBeansTask<JSONObject> getapplicantsTask = new NetBeansTask<JSONObject>(
			"/getapplyers", "applicants", JSONObject.class)
	{
		@Override
		protected void onSuccess(List<JSONObject> data)
		{
			if (data != null)
			{
				apadapter.refresh(new ArrayList<>(data));
			}
			else
			{
				Toast.makeText(TaskOgActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
				switcher.showPrevious();
				acceptDlg.dismiss();
			}
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
			{
				if ("already".equals(data))
				{
					Toast.makeText(TaskOgActivity.this, "请勿重复申请", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			super.onUnsuccess(code, data);
		}

	};

	@Override
	public void OnClick(TaskBean data)
	{
		task = data;
		if (isStu)
		{
			confirmDlg.show();
		}
		else
		{
			getapplicantsTask.post("tid", data.getTid());
			acceptDlg.show();
		}
	}

}
