package xziar.enhancer.activity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import xziar.enhancer.R;
import xziar.enhancer.fragment.ForumFragment;
import xziar.enhancer.fragment.GroupFragment;
import xziar.enhancer.fragment.TaskListFragment;
import xziar.enhancer.fragment.UserFragment;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class MainActivity extends AppCompatActivity implements OnMenuTabClickListener
{
	private FragmentManager fragMan;
	private BottomBar bottombar;
	private Fragment currentFrag;
	private TaskListFragment tasklistFrag;
	private ForumFragment forumFrag;
	private GroupFragment groupFrag;
	private UserFragment userFrag;
	@BindView(R.id.actbar)
	private ActionBar actbar;

	private static Context appcontext;

	public static UserBean user;
	public static TaskBean objtask;

	private void initWidget()
	{
		tasklistFrag = new TaskListFragment();
		forumFrag = new ForumFragment();
		groupFrag = new GroupFragment();
		userFrag = new UserFragment();
		fragMan = getFragmentManager();
		FragmentTransaction fragTrans = fragMan.beginTransaction().add(R.id.main, tasklistFrag)
				.add(R.id.main, forumFrag).add(R.id.main, groupFrag).add(R.id.main, userFrag);
		fragTrans.hide(currentFrag = tasklistFrag).hide(forumFrag).hide(groupFrag).hide(userFrag)
				.commit();

		bottombar.setMaxFixedTabs(3);
		bottombar.setItems(R.menu.bottombar);
		bottombar.setOnMenuTabClickListener(this);
		bottombar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
		bottombar.mapColorForTab(1, 0xFF5D4037);
		bottombar.mapColorForTab(2, "#7B1FA2");
		bottombar.mapColorForTab(3, "#E43F3F");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		appcontext = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewInject.inject(this);

		actbar.setTitle("biaoti-xxxxx");
		actbar.setSubtitle("subtitle");
		actbar.setupActionBar(this);

		bottombar = BottomBar.attach(this, savedInstanceState);
		initWidget();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		bottombar.onSaveInstanceState(outState);
	}

	private void changeFrag(Fragment frag)
	{
		FragmentTransaction fragTrans = fragMan.beginTransaction().hide(currentFrag);
		fragTrans.show(currentFrag = frag).commit();
	}

	@Override
	public void onMenuTabSelected(int menuItemId)
	{
		switch (menuItemId)
		{
		case R.id.tasklist:
			changeFrag(tasklistFrag);
			break;
		case R.id.forum:
			changeFrag(forumFrag);
			break;
		case R.id.group:
			changeFrag(groupFrag);
			break;
		case R.id.user:
			changeFrag(userFrag);
			break;
		}

	}

	@Override
	public void onMenuTabReSelected(int menuItemId)
	{
		// TODO Auto-generated method stub
	}

	public static Context getAppContext()
	{
		return appcontext;
	}

	public void setTitle(String title)
	{
		actbar.setTitle(title);
	}
}
