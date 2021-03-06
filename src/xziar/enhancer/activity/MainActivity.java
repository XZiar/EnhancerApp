package xziar.enhancer.activity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
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
	public static UserBean user;
	private FragManager fragMan;
	private BottomBar bottombar;
	private TaskListFragment tasklistFrag;
	private ForumFragment forumFrag;
	private GroupFragment groupFrag;
	private UserFragment userFrag;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	@BindView(R.id.main)
	private LinearLayout mainholder;

	private void initWidget()
	{
		tasklistFrag = new TaskListFragment();
		forumFrag = new ForumFragment();
		groupFrag = new GroupFragment();
		userFrag = new UserFragment();
		fragMan = new FragManager(this, R.id.main);
		fragMan.add(tasklistFrag, R.id.task, 0xFFE91E63, "任务")
				.add(forumFrag, R.id.forum, 0xFF9C27B0, "论坛")
				.add(groupFrag, R.id.group, 0xFF3F51B5, "团队")
				.add(userFrag, R.id.user, 0xFF2196F3, "我").hideAll().doit();

		bottombar.setMaxFixedTabs(3);
		bottombar.setItems(R.menu.bottombar);
		bottombar.setOnMenuTabClickListener(this);
		bottombar.mapColorForTab(0, (int) fragMan.getData(R.id.task));
		bottombar.mapColorForTab(1, (int) fragMan.getData(R.id.forum));
		bottombar.mapColorForTab(2, (int) fragMan.getData(R.id.group));
		bottombar.mapColorForTab(3, (int) fragMan.getData(R.id.user));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewInject.inject(this);

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
		fragMan.getCurFrag().onOptionsItemSelected(actbar.action_top);
		mainholder.scrollTo(0, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return fragMan.getCurFrag().onOptionsItemSelected(item);
	}

	public ActionBar getActbar()
	{
		return actbar;
	}

	public void setTitle(String title)
	{
		actbar.setTitle(title);
	}
}
