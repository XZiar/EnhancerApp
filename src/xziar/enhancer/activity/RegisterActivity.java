package xziar.enhancer.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.fragment.StuRegFragment;
import xziar.enhancer.widget.WaitDialog;

public class RegisterActivity extends Activity implements OnClickListener
{
	private EditText un, pwd;
	private Button btn_register;
	private TextView txt_reg;

	private void initWidget()
	{
		un = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);

		FragmentManager fragMan = getFragmentManager();
		Fragment frag = new StuRegFragment(this);
		fragMan.beginTransaction().add(R.id.regcontent, frag).show(frag).commit();
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

		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT)
					.show();
	}
}
