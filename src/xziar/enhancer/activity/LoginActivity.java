package xziar.enhancer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;

public class LoginActivity extends Activity implements OnClickListener
{
	private EditText un, pwd;
	private Button btn_login;
	private TextView txt_reg;

	private void initWidget()
	{
		un = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);
		btn_login = (Button) findViewById(R.id.btn_login);
		txt_reg = (TextView) findViewById(R.id.link_register);
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
			Toast.makeText(this,
					un.getText().toString() + " & " + pwd.getText().toString(),
					Toast.LENGTH_SHORT).show();
		else if (v == txt_reg)
			Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT)
					.show();
	}
}
