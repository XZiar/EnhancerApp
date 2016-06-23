package xziar.enhancer.activity;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.pojo.AccountBean;
import xziar.enhancer.pojo.CompanyBean;
import xziar.enhancer.pojo.StudentBean;
import xziar.enhancer.pojo.UserBean;
import xziar.enhancer.util.NetworkUtil.NetTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.WaitDialog;

public class LoginActivity extends AppCompatActivity implements OnClickListener
{
	private static final int REQUESTCODE_REGISTER = 1;
	@BindView(R.id.username)
	private EditText un;
	@BindView(R.id.password)
	private EditText pwd;
	@BindView(R.id.btn_login)
	private Button btn_login;
	@BindView(R.id.link_register)
	private TextView txt_reg;

	private WaitDialog waitDialog;

	private void initWidget()
	{
		ViewInject.inject(this);
		btn_login.setOnClickListener(this);
		txt_reg.setOnClickListener(this);
		waitDialog = new WaitDialog(this, " µÇÂ½ÖÐ... ...");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initWidget();
	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_login)
		{
			loginTask.post("un", un.getText(), "pwd", pwd.getText());
		}
		else if (v == txt_reg)
		{
			Intent it = new Intent(this, RegisterActivity.class);
			startActivityForResult(it, REQUESTCODE_REGISTER);
		}
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT).show();
	}

	private NetTask<UserBean> loginTask = new NetTask<UserBean>("/app/login", true)
	{
		@Override
		protected UserBean parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			JSONObject obj = JSON.parseObject(data.string());
			String msg = obj.getString("msg");
			if (obj.getBooleanValue("success"))
			{
				obj = obj.getJSONObject("user");
				switch (AccountBean.Role.values()[obj.getIntValue("role")])
				{
				case company:
					CompanyBean cpn = JSON.parseObject(obj.toJSONString(), CompanyBean.class);
					return cpn;
				case student:
					StudentBean stu = JSON.parseObject(obj.toJSONString(), StudentBean.class);
					return stu;
				default:
					break;
				}
			}
			throw new ParseResultFailException(msg);
		}

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
		protected void onFail()
		{
			Toast.makeText(LoginActivity.this, "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final UserBean data)
		{
			Toast.makeText(LoginActivity.this, data.getName(), Toast.LENGTH_SHORT).show();
			MainActivity.user = data;
			setResult(RESULT_OK, new Intent().putExtra("user_changed", true));
			finish();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if(code == 200)
				Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}
	};
}
