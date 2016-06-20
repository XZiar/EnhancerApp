package xziar.enhancer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import jp.wasabeef.richeditor.RichEditor;
import xziar.enhancer.R;

public class RichTextEditor extends LinearLayout
{
	private Context context;
	private RichEditor reditor;

	public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.layout_rich_text_editor, this, true);
		reditor = (RichEditor) findViewById(R.id.reditor);
	}

	public RichTextEditor(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public RichTextEditor(Context context)
	{
		this(context, null);
	}

	public RichEditor getEditor()
	{
		return reditor;
	}
}
