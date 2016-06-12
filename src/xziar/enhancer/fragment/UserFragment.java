package xziar.enhancer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.CircleImageUtil;
import xziar.enhancer.widget.NumberBox;

public class UserFragment extends Fragment implements OnClickListener
{
	private static final int REQUESTCODE_LOGIN = 1;
	private Activity act;
	private View view;
	private UserBean user;
	private ImageView headimg;
	private TextView name;
	private NumberBox score, task_finish, task_ongoing;

	public UserFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_user, container, false);
		headimg = (ImageView) view.findViewById(R.id.headimg);
		name = (TextView) view.findViewById(R.id.name);
		score = (NumberBox) view.findViewById(R.id.score);
		task_finish = (NumberBox) view.findViewById(R.id.task_finish);
		task_ongoing = (NumberBox) view.findViewById(R.id.task_ongoing);
		headimg.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		refreshData();
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
			headimg.setImageDrawable(
					CircleImageUtil.getCircleDrawable(R.drawable.defaulthead));
			name.setText("Î´µÇÂ¼");
			score.setVal("*");
			task_finish.setVal("*");
			task_ongoing.setVal("*");
		}
		else
		{
			name.setText(user.getName());
			score.setVal(user.getScore());
			task_finish.setVal(user.getTask_finish());
			task_ongoing.setVal(user.getTask_ongoing());
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v == headimg)
		{
			if (user == null)
			{
				Intent it = new Intent(act, LoginActivity.class);
				startActivityForResult(it, REQUESTCODE_LOGIN);
			}
			else
			{
				// alertdialog
			}
		}

	}
}
