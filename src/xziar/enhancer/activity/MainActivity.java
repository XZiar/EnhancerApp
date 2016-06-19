package xziar.enhancer.activity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import xziar.enhancer.R;
import xziar.enhancer.fragment.ForumFragment;
import xziar.enhancer.fragment.GroupFragment;
import xziar.enhancer.fragment.TaskListFragment;
import xziar.enhancer.fragment.UserFragment;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.FragManager;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;

public class MainActivity extends AppCompatActivity implements OnMenuTabClickListener
{
	private FragManager fragMan;
	private BottomBar bottombar;
	private TaskListFragment tasklistFrag;
	private ForumFragment forumFrag;
	private GroupFragment groupFrag;
	private UserFragment userFrag;
	@BindView(R.id.actbar)
	private ActionBar actbar;

	private static Context appcontext;

	public static UserBean user;

	private void initWidget()
	{
		tasklistFrag = new TaskListFragment();
		forumFrag = new ForumFragment();
		groupFrag = new GroupFragment();
		userFrag = new UserFragment();
		fragMan = new FragManager(this, R.id.main);
		fragMan.add(tasklistFrag, R.id.tasklist, 0xFFE91E63, "任务")
				.add(forumFrag, R.id.forum, 0xFF9C27B0, "论坛")
				.add(groupFrag, R.id.group, 0xFF3F51B5, "团队")
				.add(userFrag, R.id.user, 0xFF2196F3, "我").hideAll().doit();

		bottombar.setMaxFixedTabs(3);
		bottombar.setItems(R.menu.bottombar);
		bottombar.setOnMenuTabClickListener(this);
		bottombar.mapColorForTab(0, (int) fragMan.getData(R.id.tasklist));
		bottombar.mapColorForTab(1, (int) fragMan.getData(R.id.forum));
		bottombar.mapColorForTab(2, (int) fragMan.getData(R.id.group));
		bottombar.mapColorForTab(3, (int) fragMan.getData(R.id.user));
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

	private void changeFrag(int fragId)
	{
		fragMan.change(fragId).doit();
		actbar.setTitle((String) fragMan.getData(fragId, 1));
		actbar.setSubtitle("");
		actbar.setBackgroundColor((int) fragMan.getData(fragId));
	}

	@Override
	public void onMenuTabSelected(int menuItemId)
	{
		changeFrag(menuItemId);
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
