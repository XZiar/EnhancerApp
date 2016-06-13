package xziar.enhancer.activity;

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

	private void initWidget()
	{
		// un = (EditText) findViewById(R.id.username);
		// pwd = (EditText) findViewById(R.id.password);
		// btn_login = (Button) findViewById(R.id.btn_login);
		// txt_reg = (TextView) findViewById(R.id.link_register);
		ViewInject.inject(this);
		btn_login.setOnClickListener(this);
		txt_reg.setOnClickListener(this);
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
			final WaitDialog waitDialog = new WaitDialog(this,
					un.getText().toString() + " & " + pwd.getText().toString()
							+ " «Î…‘∫Û... ...");
			waitDialog.show();
			new android.os.Handler().postDelayed(new Runnable()
			{
				public void run()
				{
					waitDialog.dismiss();
				}
			}, 3000);
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
}
