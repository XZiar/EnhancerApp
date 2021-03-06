package xziar.enhancer.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.activity.MyPostActivity;
import xziar.enhancer.activity.MyTaskActivity;
import xziar.enhancer.activity.TaskFnActivity;
import xziar.enhancer.activity.TaskOgActivity;
import xziar.enhancer.pojo.AccountBean.Role;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.BaseApplication;
import xziar.enhancer.util.ImageUtil;
import xziar.enhancer.util.NetworkUtil;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.NumberBox;

@ObjView("view")
public class UserFragment extends Fragment
		implements OnClickListener, DialogInterface.OnClickListener
{
	private static enum reqCode
	{
		login, chghead, ongoing, finish, mypost, mytask
	}

	private View view;
	private ActionBar actbar;
	private UserBean user;
	private AlertDialog logoutDlg, chgpwdDlg, chgdesDlg, chgheadDlg, aboutDlg, settingDlg;
	private EditText oldpwd, newpwd, newdes;
	@BindView
	private LinearLayout loginarea, oparea;
	@BindView
	private ImageView headimg;
	@BindView(onClick = "this")
	private NumberBox score, task_finish, task_ongoing;
	@BindView(onClick = "this")
	private TextView name, des, chgpwd, mypost, mytask, about;

	@SuppressLint("InflateParams")
	private void initDialog(Context context)
	{
		logoutDlg = new AlertDialog.Builder(context).setTitle("退出登录").setMessage("确定退出登录吗？")
				.setPositiveButton("退出", this).setNegativeButton("取消", null).create();

		View chgpwdView = LayoutInflater.from(context).inflate(R.layout.dialog_chginfo, null);
		oldpwd = (EditText) chgpwdView.findViewById(R.id.oldpwd);
		newpwd = (EditText) chgpwdView.findViewById(R.id.newpwd);
		chgpwdView.findViewById(R.id.input3).setVisibility(View.GONE);
		chgpwdDlg = new AlertDialog.Builder(context).setTitle("修改密码").setView(chgpwdView)
				.setPositiveButton("修改", this).setNegativeButton("放弃", null).create();
		chgpwdDlg.setCanceledOnTouchOutside(false);

		View chgdesView = LayoutInflater.from(context).inflate(R.layout.dialog_chginfo, null);
		newdes = (EditText) chgdesView.findViewById(R.id.newdes);
		newdes.setHint("简介");
		chgdesView.findViewById(R.id.input1).setVisibility(View.GONE);
		chgdesView.findViewById(R.id.input2).setVisibility(View.GONE);
		chgdesDlg = new AlertDialog.Builder(context).setTitle("修改简介").setView(chgdesView)
				.setPositiveButton("修改", this).setNegativeButton("放弃", null).create();
		chgdesDlg.setCanceledOnTouchOutside(false);

		chgheadDlg = new AlertDialog.Builder(context).setTitle("头像").setPositiveButton("OK", this)
				.setNegativeButton("CANCEL", null).create();

		aboutDlg = new AlertDialog.Builder(context).setTitle("关于Enhancer")
				.setMessage("这是一个还没有完成的软件").setPositiveButton("OK", null).create();

		settingDlg = new AlertDialog.Builder(context).setTitle("设置网络目标(DEBUG)")
				.setItems(NetworkUtil.getChoices(), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						NetworkUtil.chooseChoices(which);
					}
				}).create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_user, container, false);
		ViewInject.inject(this);
		actbar = ((MainActivity) getActivity()).getActbar();
		setHasOptionsMenu(true);
		headimg.setOnClickListener(this);
		loginarea.setOnClickListener(this);
		initDialog(getActivity());
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		refreshData();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data != null)
		{
			if (data.getBooleanExtra("user_changed", false))
			{
				refreshData();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (!hidden)
		{
			refreshData();
		}

		super.onHiddenChanged(hidden);
	}

	private void refreshData()
	{
		user = MainActivity.user;
		refreshView();
	}

	private void refreshView()
	{
		actbar.removeAllMenu();
		actbar.setMenu(R.menu.menu_user);
		if (user == null)
		{
			actbar.delMenu(R.id.action_logout);
			headimg.setImageDrawable(ImageUtil.getCircleDrawable(R.drawable.defaulthead));
			name.setText("未登录");
			des.setText("");
			score.setVal("*");
			task_finish.setVal("*");
			task_ongoing.setVal("*");
			oparea.setVisibility(View.GONE);
		}
		else
		{
			actbar.delMenu(R.id.action_login);
			if (user.getAccountRole() == Role.company)
			{
				headimg.setImageDrawable(ImageUtil.getCircleDrawable(R.drawable.companyhead));
				mytask.setVisibility(View.VISIBLE);
			}
			else
			{
				headimg.setImageDrawable(ImageUtil.getCircleDrawable(R.drawable.studenthead));
				mytask.setVisibility(View.GONE);
			}
			name.setText(user.getName());
			des.setText(user.getDescribe());
			score.setVal(user.getScore());
			task_finish.setVal(user.getTask_finish());
			task_ongoing.setVal(user.getTask_ongoing());
			oparea.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_logout:
			logoutDlg.show();
			break;
		case R.id.action_login:
			Intent it = new Intent(getActivity(), LoginActivity.class);
			startActivityForResult(it, reqCode.login.ordinal());
			break;
		case R.id.action_setting:
			settingDlg.show();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		if (user == null)
		{
			if (v == loginarea || v == headimg || v == des)
			{
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(it, reqCode.login.ordinal());
			}
			return;
		}
		if (v == headimg)
		{
			chgheadDlg.show();
		}
		else if (v == chgpwd)
		{
			oldpwd.setText("");
			newpwd.setText("");
			chgpwdDlg.show();
		}
		else if (v == des)
		{
			newdes.setText(user.getDescribe());
			chgdesDlg.show();
		}
		else if (v == about)
			aboutDlg.show();
		else if (v == task_ongoing)
		{
			Intent it = new Intent(getActivity(), TaskOgActivity.class);
			startActivityForResult(it, reqCode.ongoing.ordinal());
		}
		else if (v == task_finish)
		{
			Intent it = new Intent(getActivity(), TaskFnActivity.class);
			startActivityForResult(it, reqCode.finish.ordinal());
		}
		else if (v == mypost)
		{
			Intent it = new Intent(getActivity(), MyPostActivity.class);
			startActivityForResult(it, reqCode.mypost.ordinal());
		}
		else if (v == mytask)
		{
			Intent it = new Intent(getActivity(), MyTaskActivity.class);
			startActivityForResult(it, reqCode.mytask.ordinal());
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if (which != DialogInterface.BUTTON_POSITIVE)
		{
			dialog.dismiss();
			return;
		}
		if (dialog == logoutDlg)
		{
			logoutTask.post();
		}
		else if (dialog == chgpwdDlg)
		{
			chginfoTask.withData(new String[] { "pwd", newpwd.getText().toString() }).post("oldpwd",
					oldpwd.getText(), "newpwd", newpwd.getText(), "des", user.getDescribe());
		}
		else if (dialog == chgdesDlg)
		{
			chginfoTask.withData(new String[] { "des", newdes.getText().toString() }).post("oldpwd",
					"", "newpwd", "", "des", user.getDescribe());
		}
		else if (dialog == chgheadDlg)
		{
			dialog.dismiss();
		}
	}

	private NetTask<String> logoutTask = new NetTask<String>("/chgmyinfo", true)
	{
		@Override
		protected void onSuccess(String data)
		{
			MainActivity.user = null;
			logoutDlg.dismiss();
			refreshData();
		}
	};

	private NetBeanTask<String> chginfoTask = new NetBeanTask<String>("/chgmyinfo", "msg",
			String.class)
	{
		@Override
		protected void onSuccess(String data)
		{
			String[] dat = (String[]) getTaskdata();
			if (dat[0].equals("pwd"))
				user.setPwd(dat[1]);
			else if (dat[0].equals("des"))
			{
				user.setDescribe(dat[1]);
				des.setText(dat[1]);
			}
			chgpwdDlg.dismiss();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(BaseApplication.getContext(), data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}
	};
}
