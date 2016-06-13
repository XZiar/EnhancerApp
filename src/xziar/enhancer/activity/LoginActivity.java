package xziar.enhancer.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.NetworkUtil.NetTask;
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
		waitDialog = new WaitDialog(this, " «Î…‘∫Û... ...");
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
			HashMap<String, Object> data = new HashMap<>();
			data.put("un", un.getText());
			data.put("pwd", pwd.getText());
			loginTask.post(data);
		}
		else if (v == txt_reg)
		{
			Intent it = new Intent(this, RegisterActivity.class);
			startActivityForResult(it, REQUESTCODE_REGISTER);
		}
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT)
					.show();
	}

	private NetTask loginTask = new NetTask("/login")
	{
		@Override
		protected void onStart()
		{
			waitDialog.show();
		}

		@Override
		protected void onTimeout()
		{
			super.onTimeout();
			waitDialog.dismiss();
			Toast.makeText(LoginActivity.this, "Timeout", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		protected void onFail(final Exception e)
		{
			super.onFail(e);
			waitDialog.dismiss();
			Toast.makeText(LoginActivity.this, e.getClass().getName(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onSuccess(final String data)
		{
			super.onSuccess(data);
			waitDialog.dismiss();
			Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
		}
	};
}
