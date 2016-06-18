package xziar.enhancer.activity;

import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.pojo.TaskBean;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class TaskViewActivity extends AppCompatActivity
{
	private TaskBean task;
	@BindView(R.id.describe)
	TextView describe;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);
		ViewInject.inject(this);
		int tid = getIntent().getIntExtra("tid", -1);
		HashMap<String, Integer> dat = new HashMap<>();
		dat.put("tid", tid);
		viewTask.post(dat);
	}

	private NetTask<TaskBean> viewTask = new NetTask<TaskBean>("/app/taskview")
	{
		@Override
		protected TaskBean parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			String msg = obj.getString("msg");
			if (obj.getBooleanValue("success"))
			{
				return JSON.parseObject(obj.getString("task"), TaskBean.class);
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
			Toast.makeText(TaskViewActivity.this, "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final TaskBean data)
		{
			task = data;
			describe.setText(Html.fromHtml(data.getDescribe()));
		}
	};
}
