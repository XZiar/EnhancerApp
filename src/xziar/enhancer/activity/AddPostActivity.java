package xziar.enhancer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import jp.wasabeef.richeditor.RichEditor;
import xziar.enhancer.R;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.widget.ActionBar;
import xziar.enhancer.widget.RichTextEditor;

public class AddPostActivity extends AppCompatActivity implements OnClickListener
{
	@BindView(R.id.posttitle)
	private EditText title;
	@BindView(R.id.btn_post)
	private Button btn_post;
	@BindView(R.id.actbar)
	private ActionBar actbar;
	private CardView cv;
	@BindView(R.id.editor)
	private RichTextEditor editor;
	private RichEditor reditor;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_post);
		ViewInject.inject(this);
		actbar.setupActionBar(this);
		actbar.setTitle("发布新话题");
		btn_post.setOnClickListener(this);

		reditor = editor.getEditor();
		reditor.setEditorHeight(200);
		reditor.setEditorFontSize(22);
		reditor.setEditorFontColor(Color.RED);
		reditor.setPadding(10, 10, 10, 10);
		reditor.setPlaceholder("Insert text here...");

	}

	@Override
	public void onClick(View v)
	{
		if (v == btn_post)
		{
			Toast.makeText(this, "post a post", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_back:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
