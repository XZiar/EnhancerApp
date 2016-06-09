package xziar.enhancer;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements OnMenuTabClickListener
{
	private BottomBar bottombar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bottombar = BottomBar.attach(this, savedInstanceState);
		bottombar.setMaxFixedTabs(2);
		bottombar.setItems(R.menu.bottombar);
		bottombar.setOnMenuTabClickListener(this);
		bottombar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
		bottombar.mapColorForTab(1, 0xFF5D4037);
		bottombar.mapColorForTab(2, "#7B1FA2");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		bottombar.onSaveInstanceState(outState);
	}

	@Override
	public void onMenuTabSelected(int menuItemId)
	{
		switch (menuItemId)
		{
		case R.id.tasklist:
			Toast.makeText(this, "tasklist", Toast.LENGTH_SHORT).show();
			break;
		case R.id.forum:
			Toast.makeText(this, "forum", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user:
			Toast.makeText(this, "user", Toast.LENGTH_SHORT).show();
			break;
		}

	}

	@Override
	public void onMenuTabReSelected(int menuItemId)
	{
		// TODO Auto-generated method stub

	}
}
