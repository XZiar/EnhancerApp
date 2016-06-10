package xziar.enhancer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.activity.LoginActivity;
import xziar.enhancer.activity.MainActivity;
import xziar.enhancer.pojo.UserBean;

public class UserFragment extends Fragment
{
	private static final int REQUESTCODE_LOGIN = 1;
	private Activity act;
	private View view;
	private UserBean user;

	public UserFragment(Activity act)
	{
		this.act = act;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_user, container, false);
		((TextView) view.findViewById(R.id.txt)).setText("User");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		if(!hidden)
			refreshData();
	}
	
	private void refreshData()
	{
		user = MainActivity.user;
		refreshView();
	}
	
	private void refreshView()
	{
		if(user == null)
		{
			Intent it = new Intent(act, LoginActivity.class);
			startActivityForResult(it, REQUESTCODE_LOGIN);
		}
		else
		{
			
		}
	}
}
