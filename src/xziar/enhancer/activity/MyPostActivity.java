package xziar.enhancer.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.PostAdapter;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetBeansTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.CompatRecyclerView;

public class MyPostActivity extends AppCompatActivity implements OnItemClickListener<PostBean>
{
	private static enum reqCode
	{
		viewpost
	}

	private ArrayList<PostBean> posts;
	@BindView
	private CompatRecyclerView list;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private PostAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_post);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setBackButton(true);
		adapter = new PostAdapter(this);
		list.setAdapter(adapter);
		adapter.setOnItemClickListener(this);
		listTask.post();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private NetBeansTask<PostBean> listTask = new NetBeansTask<PostBean>("/mypost", "posts",
			PostBean.class)
	{
		@Override
		protected void onSuccess(List<PostBean> data)
		{
			posts = new ArrayList<>(data);
			adapter.refresh(posts);
		}
	};

	@Override
	public void OnClick(PostBean data)
	{
		Intent it = new Intent(this, PostViewActivity.class);
		it.putExtra("pid", data.getPid());
		startActivityForResult(it, reqCode.viewpost.ordinal());
	}

}
