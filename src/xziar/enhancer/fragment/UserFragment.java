package xziar.enhancer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.pojo.AccountBean.Role;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.CircleImageUtil;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;
import xziar.enhancer.widget.NumberBox;

@ObjView("view")
public class UserFragment extends Fragment implements OnClickListener
{
	private static final int REQUESTCODE_LOGIN = 1;
	private View view;
	private UserBean user;
	@BindView(R.id.loginarea)
	private LinearLayout loginarea;
	@BindView(R.id.headimg)
	private ImageView headimg;
	@BindView(R.id.name)
	private TextView name;
	@BindView(R.id.des)
	private TextView des;
	@BindView(R.id.score)
	private NumberBox score;
	@BindView(R.id.task_finish)
	private NumberBox task_finish;
	@BindView(R.id.task_ongoing)
	private NumberBox task_ongoing;
	@BindView(R.id.oparea)
	private LinearLayout oparea;
	@BindView(R.id.chgpwd)
	private TextView chgpwd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_user, container, false);
		ViewInject.inject(this);
		headimg.setOnClickListener(this);
		loginarea.setOnClickListener(this);
		chgpwd.setOnClickListener(this);
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
			if (data.getBooleanExtra("changed", false))
			{
				refreshData();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		if (!hidden)
			refreshData();
	}

	private void refreshData()
	{
		user = MainActivity.user;
		refreshView();
	}

	private void refreshView()
	{
		if (user == null)
		{
			headimg.setImageDrawable(CircleImageUtil.getCircleDrawable(R.drawable.defaulthead));
			name.setText("δ��¼");
			des.setText("");
			score.setVal("*");
			task_finish.setVal("*");
			task_ongoing.setVal("*");
			oparea.setVisibility(View.GONE);
		}
		else
		{
			headimg.setImageDrawable(
					CircleImageUtil.getCircleDrawable(user.getAccountRole() == Role.company
							? R.drawable.companyhead : R.drawable.studenthead));
			name.setText(user.getName());
			des.setText(user.getDescribe());
			score.setVal(user.getScore());
			task_finish.setVal(user.getTask_finish());
			task_ongoing.setVal(user.getTask_ongoing());
			oparea.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (user == null)
		{
			if (v == loginarea || v == headimg)
			{
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(it, REQUESTCODE_LOGIN);
			}
			return;
		}
		if (v == headimg)
		{
			AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("ͷ��").setView(headimg)
					.setPositiveButton("OK", null).setNegativeButton("CANCEL", null).create();
			dlg.show();
		}
		else if (v == chgpwd)
		{
			AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("�޸�")
					.setView(R.layout.dialog_chgpwd).setPositiveButton("�޸�", null)
					.setNegativeButton("����", null).create();
			dlg.show();
		}
	}
}
