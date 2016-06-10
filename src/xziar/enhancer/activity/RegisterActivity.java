package xziar.enhancer.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.fragment.CpnRegFragment;
import xziar.enhancer.fragment.StuRegFragment;
import xziar.enhancer.widget.WaitDialog;

public class RegisterActivity extends Activity implements OnClickListener
{
	private EditText un, pwd;
	private Button btn_register, btn_stu, btn_cpn;
	private FragmentManager fragMan;
	private Fragment currentFrag;
	private StuRegFragment stuFrag;
	private CpnRegFragment cpnFrag;

	private void initWidget()
	{
		un = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);
		btn_stu = (Button) findViewById(R.id.btn_stu);
		btn_stu.setOnClickListener(this);
		btn_cpn = (Button) findViewById(R.id.btn_cpn);
		btn_cpn.setOnClickListener(this);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);

		fragMan = getFragmentManager();
		stuFrag = new StuRegFragment(this);
		cpnFrag = new CpnRegFragment(this);
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
			final WaitDialog waitDialog = new WaitDialog(this,
					un.getText().toString() + " & " + pwd.getText().toString()
							+ " «Î…‘∫Û... ...");
			waitDialog.show();
			new Handler().postDelayed(new Runnable()
			{
				public void run()
				{
					waitDialog.dismiss();
				}
			}, 3000);
		}
		else if(v == btn_stu)
		{
			btn_stu.setBackgroundResource(R.color.colorAccent);
			btn_cpn.setBackgroundResource(R.color.iron);
			changeFrag(stuFrag);
		}
		else if(v == btn_cpn)
		{
			btn_cpn.setBackgroundResource(R.color.colorAccent);
			btn_stu.setBackgroundResource(R.color.iron);
			changeFrag(cpnFrag);
		}
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT)
					.show();
	}

	private void changeFrag(Fragment frag)
	{
		if (currentFrag == frag)
			return;
		FragmentTransaction fragTrans = fragMan.beginTransaction()
				.hide(currentFrag);
		fragTrans.show(currentFrag = frag).commit();
	}
}
