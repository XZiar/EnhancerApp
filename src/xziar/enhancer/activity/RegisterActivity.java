package xziar.enhancer.activity;

import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.fragment.CpnRegFragment;
import xziar.enhancer.fragment.StuRegFragment;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.WaitDialog;

public class RegisterActivity extends AppCompatActivity implements OnClickListener
{
	private FragmentManager fragMan;
	private Fragment currentFrag;
	private StuRegFragment stuFrag;
	private CpnRegFragment cpnFrag;
	private WaitDialog waitDialog;
	@BindView(R.id.username)
	private EditText un;
	@BindView(R.id.password)
	private EditText pwd;
	@BindView(R.id.btn_register)
	private Button btn_register;
	@BindView(R.id.btn_stu)
	private Button btn_stu;
	@BindView(R.id.btn_cpn)
	private Button btn_cpn;

	private void initWidget()
	{
		ViewInject.inject(this);
		btn_stu.setOnClickListener(this);
		btn_cpn.setOnClickListener(this);
		btn_register.setOnClickListener(this);

		waitDialog = new WaitDialog(this, " ×¢²áÖÐ... ...");
		stuFrag = new StuRegFragment();
		cpnFrag = new CpnRegFragment();
		fragMan = getFragmentManager();
		fragMan.beginTransaction().add(R.id.regcontent, currentFrag = stuFrag)
				.add(R.id.regcontent, cpnFrag).hide(cpnFrag).commit();
		btn_stu.setBackgroundResource(R.color.colorAccent);
		btn_cpn.setBackgroundResource(R.color.iron);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initWidget();
	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_register)
		{
			if (currentFrag == stuFrag)
			{
				HashMap<String, String> data = stuFrag.getData();
				data.put("stu.un", un.getText().toString());
				data.put("stu.pwd", pwd.getText().toString());
				regTask.post(data);
			}
			else
			{
				HashMap<String, Object> data = cpnFrag.getData();
				data.put("cpn.un", un.getText().toString());
				data.put("cpn.pwd", pwd.getText().toString());
				regTask.post(data, true);
			}
		}
		else if (v == btn_stu)
		{
			btn_stu.setBackgroundResource(R.color.colorAccent);
			btn_cpn.setBackgroundResource(R.color.iron);
			changeFrag(stuFrag);
		}
		else if (v == btn_cpn)
		{
			btn_cpn.setBackgroundResource(R.color.colorAccent);
			btn_stu.setBackgroundResource(R.color.iron);
			changeFrag(cpnFrag);
		}
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT).show();
	}

	private void changeFrag(Fragment frag)
	{
		if (currentFrag == frag)
			return;
		FragmentTransaction fragTrans = fragMan.beginTransaction().hide(currentFrag);
		fragTrans.show(currentFrag = frag).commit();
	}

	private NetTask<JSONObject> regTask = new NetTask<JSONObject>("/register", true)
	{
		@Override
		protected void onStart()
		{
			waitDialog.show();
		}

		@Override
		protected void onDone()
		{
			waitDialog.dismiss();
		}

		@Override
		protected JSONObject parse(ResponseBody data) throws IOException
		{
			return JSON.parseObject(data.string());
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(RegisterActivity.this, "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final JSONObject data)
		{
			Toast.makeText(RegisterActivity.this, data.getString("msg"), Toast.LENGTH_SHORT).show();
			if (data.getBooleanValue("success"))
			{
				setResult(RESULT_OK, null);
				finish();
			}
		}
	};
}
