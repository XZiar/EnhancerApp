package xziar.enhancer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.NetworkUtil;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("view")
public class ForumFragment extends Fragment
{
	private Activity act;
	private View view;
	@BindView(R.id.txt)
	private TextView txt;

	public ForumFragment(Activity act)
	{
		this.act = act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_forum, container, false);
		ViewInject.inject(this);
		txt.setText("Forum");
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
		if (!hidden)
		{
			NetworkUtil.Test(task);
		}
	}

	private NetTask task = new NetTask("")
	{
		@Override
		protected void onTimeout()
		{
			super.onTimeout();
			txt.setText(txt.getText().toString() + "\n" + this.toString()
					+ " : Timeout\nWith Handler: " + handler.toString());
		}

		@Override
		protected void onFail(final Exception e)
		{
			super.onFail(e);
			txt.setText(txt.getText().toString() + "\n" + this.toString()
					+ " : fail by " + e.getClass().getName()
					+ "\nWith Handler: " + handler.toString());
		}

		@Override
		protected void onSuccess(final String data)
		{
			super.onSuccess(data);
			txt.setText(txt.getText().toString() + "\nresponse:\n" + data);
		}
	};
}
