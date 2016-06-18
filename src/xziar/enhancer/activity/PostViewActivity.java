package xziar.enhancer.activity;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.pojo.PostBean;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class PostViewActivity extends AppCompatActivity
{
	private PostBean post;
	@BindView(R.id.describe)
	TextView describe;
	@BindView(R.id.actbar)
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_view);
		ViewInject.inject(this);
		toolbar.setTitle("Hello Here");

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int pid = getIntent().getIntExtra("pid", -1);
		viewTask.post("pid", pid);
	}

	private NetTask<PostBean> viewTask = new NetTask<PostBean>("/app/postview", true)
	{
		@Override
		protected PostBean parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			String msg = obj.getString("msg");
			if (obj.getBooleanValue("success"))
			{
				return JSON.parseObject(obj.getString("post"), PostBean.class);
			}
			throw new ParseResultFailException(msg);
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			super.onUnsuccess(code, data);
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(PostViewActivity.this, "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final PostBean data)
		{
			post = data;
			String txt = data.getDescribe() + data.getDescribe() + data.getDescribe();
			describe.setText(Html.fromHtml(txt));
		}
	};
}
