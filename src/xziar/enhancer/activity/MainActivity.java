package xziar.enhancer.activity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.fragment.ForumFragment;
import xziar.enhancer.fragment.TaskListFragment;
import xziar.enhancer.fragment.UserFragment;

public class MainActivity extends AppCompatActivity
		implements OnMenuTabClickListener
{
	private FragmentManager fragMan;
	private BottomBar bottombar;
	private Fragment current;
	private TaskListFragment tasklist;
	private ForumFragment forum;
	private UserFragment user;

	private void initWidget()
	{
		fragMan = getFragmentManager();
		tasklist = new TaskListFragment();
		Log.v("checker", tasklist.toString());
		forum = new ForumFragment();
		Log.v("checker", forum.toString());
		user = new UserFragment();
		Log.v("checker", user.toString());
		FragmentTransaction fragTrans = fragMan.beginTransaction()
				.add(R.id.main, tasklist).add(R.id.main, forum)
				.add(R.id.main, user);
		fragTrans.hide(tasklist).hide(forum).hide(user);
		fragTrans.show(current = tasklist).commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initWidget();

		bottombar = BottomBar.attach(this, savedInstanceState);
		bottombar.setMaxFixedTabs(2);
		bottombar.setItems(R.menu.bottombar);
		bottombar.setOnMenuTabClickListener(this);
		bottombar.mapColorForTab(0,
				ContextCompat.getColor(this, R.color.colorAccent));
		bottombar.mapColorForTab(1, 0xFF5D4037);
		bottombar.mapColorForTab(2, "#7B1FA2");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		bottombar.onSaveInstanceState(outState);
	}

	private void changeFrag(Fragment frag)
	{
		FragmentTransaction fragTrans = fragMan.beginTransaction()
				.hide(current);
		fragTrans.show(current = frag).commit();
	}

	@Override
	public void onMenuTabSelected(int menuItemId)
	{
		switch (menuItemId)
		{
		case R.id.tasklist:
			Toast.makeText(this, "tasklist", Toast.LENGTH_SHORT).show();
			changeFrag(tasklist);
			break;
		case R.id.forum:
			Toast.makeText(this, "forum", Toast.LENGTH_SHORT).show();
			changeFrag(forum);
			break;
		case R.id.user:
			Toast.makeText(this, "user", Toast.LENGTH_SHORT).show();
			changeFrag(user);
			break;
		}

	}

	@Override
	public void onMenuTabReSelected(int menuItemId)
	{
		// TODO Auto-generated method stub

	}
}
