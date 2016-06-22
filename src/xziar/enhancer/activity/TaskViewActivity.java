package xziar.enhancer.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import xziar.enhancer.R;
import xziar.enhancer.adapter.ApplyerAdapter;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.pojo.AccountBean.Role;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.pojo.TaskBean.Status;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;

public class TaskViewActivity extends AppCompatActivity
		implements OnClickListener, OnItemClickListener<JSONObject>
{
	private static enum reqCode
	{
		login, apply
	}

	private TaskBean task;
	private AlertDialog applyDlg;
	@BindView
	private TextView describe, payment, limit_people;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	@BindView(onClick = "this")
	private Button btn_apply;
	private ViewSwitcher switcher;
	private CompatRecyclerView applyerlist;
	private ApplyerAdapter adapter;
	private ArrayList<JSONObject> applyers = new ArrayList<>();
	private EditText reason;
	private View back, apply;

	private void initDialog()
	{
		View content = getLayoutInflater().inflate(R.layout.dialog_apply, null, false);
		switcher = (ViewSwitcher) content.findViewById(R.id.switcher);
		applyerlist = (CompatRecyclerView) content.findViewById(R.id.applyerlist);
		reason = (EditText) content.findViewById(R.id.describe);
		back = content.findViewById(R.id.action_back);
		apply = content.findViewById(R.id.btn_apply);
		switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
		switcher.setOutAnimation(
				AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
		adapter = new ApplyerAdapter(this);
		applyerlist.setAdapter(adapter);
		adapter.setItemClick(this);
		back.setOnClickListener(this);
		apply.setOnClickListener(this);
		applyDlg = new AlertDialog.Builder(this).setView(content).create();
		applyDlg.setCancelable(false);
		applyDlg.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setTitle("任务");
		actbar.setSubtitle("发起人");
		actbar.setBackButton(true);
		refreshView();
		int tid = getIntent().getIntExtra("tid", -1);
		viewTask.post("tid", tid);
		initDialog();
		btn_apply.setVisibility(View.GONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			break;
		case R.id.action_login:
			Intent it = new Intent(this, LoginActivity.class);
			startActivityForResult(it, reqCode.login.ordinal());
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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

	protected void refreshView()
	{
		actbar.delMenu(R.id.action_login);
		btn_apply.setOnClickListener(null);
		btn_apply.setBackgroundColor(ContextCompat.getColor(this, R.color.aluminum));
		if (MainActivity.user == null)
		{
			actbar.addMenu(R.id.action_login, R.drawable.icon_user, false);
		}
		else if (MainActivity.user.getAccountRole() == Role.student && task != null
				&& task.getTaskStatus() == Status.onapply)
		{
			btn_apply.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
			btn_apply.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_apply)
		{
			applyTask.post("tid", task.getTid());
			// switcher.showPrevious();
			applyDlg.show();
		}
		else if (v == back)
		{
			View cur = switcher.getCurrentView();
			if (cur == applyerlist)
				applyDlg.dismiss();
			else
				switcher.showPrevious();
		}
		else if (v == apply)
		{
			applyTask.post("tid", task.getTid(), "des", reason.getText(), "uid",
					((JSONObject) v.getTag()).getIntValue("uid"));
		}
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
			payment.setText("" + task.getPayment());
			limit_people.setText("" + task.getLimit_people());
			if (task.getTaskStatus() == Status.onapply)
				btn_apply.setText("申请任务");
			else
				btn_apply.setText("任务" + task.getTaskStatus().toTxt());
			btn_apply.setVisibility(View.VISIBLE);
			refreshView();
		}
	};

	private NetBeansTask<JSONObject> applyTask = new NetBeansTask<JSONObject>("/apply",
			"applicants", JSONObject.class)
	{
		@Override
		protected void onSuccess(List<JSONObject> data)
		{
			if (data != null)
			{
				applyers = new ArrayList<>(data);
				adapter.refresh(applyers);
			}
			else
			{
				Toast.makeText(TaskViewActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
				switcher.showPrevious();
				applyDlg.dismiss();
			}
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
			{
				if ("already".equals(data))
				{
					Toast.makeText(TaskViewActivity.this, "请勿重复申请", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			super.onUnsuccess(code, data);
		}

	};

	@Override
	public void OnClick(JSONObject data)
	{
		apply.setTag(data);
		switcher.showNext();
	}

}
