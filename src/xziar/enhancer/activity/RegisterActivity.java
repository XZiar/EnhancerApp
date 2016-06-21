package xziar.enhancer.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import xziar.enhancer.R;
import xziar.enhancer.fragment.CpnRegFragment;
import xziar.enhancer.fragment.StuRegFragment;
import xziar.enhancer.util.FragManager;
import xziar.enhancer.util.NetworkUtil.NetBeanTask;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.WaitDialog;

public class RegisterActivity extends AppCompatActivity implements OnClickListener
{
	private FragManager fragMan;
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
		fragMan = new FragManager(this, R.id.regcontent);
		fragMan.add(stuFrag, cpnFrag).hideAll().show(stuFrag).doit();
		btn_stu.setBackgroundResource(R.color.colorAccent);
		btn_cpn.setBackgroundResource(R.color.iron);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initWidget();
		regTask.init(this);
	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_register)
		{
			if (fragMan.getCurFrag() == stuFrag)
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
			fragMan.change(stuFrag).doit();
			;
		}
		else if (v == btn_cpn)
		{
			btn_cpn.setBackgroundResource(R.color.colorAccent);
			btn_stu.setBackgroundResource(R.color.iron);
			fragMan.change(cpnFrag).doit();
			;
		}
		else
			Toast.makeText(this, v.getClass().getName(), Toast.LENGTH_SHORT).show();
	}

	private NetBeanTask<String> regTask = new NetBeanTask<String>("/register", "msg",
			String.class)
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
		protected void onSuccess(String data)
		{
			Toast.makeText(RegisterActivity.this, data, Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK, null);
			finish();
		}

		@Override
		protected void onUnsuccess(int code, String data)
		{
			if (code == 200)
				Toast.makeText(RegisterActivity.this, data, Toast.LENGTH_SHORT).show();
			else
				super.onUnsuccess(code, data);
		}
	};
}
