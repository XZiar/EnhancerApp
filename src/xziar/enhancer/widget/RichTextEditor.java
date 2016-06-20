package xziar.enhancer.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.wasabeef.richeditor.RichEditor;
import xziar.enhancer.R;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class RichTextEditor extends LinearLayout implements OnClickListener
{
	private boolean isEdit = true, isTxtColor = false, isBgColor = false;
	@BindView()
	private HorizontalScrollView tool;
	@BindView()
	private RichEditor reditor;
	@BindView()
	private TextView preview;
	@BindView(onClick = "this")
	private ImageButton action_undo, action_redo, action_bold, action_italic, action_subscript,
			action_superscript, action_strikethrough, action_underline, action_heading1,
			action_heading2, action_heading3, action_heading4, action_heading5, action_heading6,
			action_txt_color, action_bg_color, action_indent, action_outdent, action_align_left,
			action_align_center, action_align_right, action_blockquote, action_insert_image,
			action_insert_link;

	public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.layout_rich_text_editor, this, true);
		ViewInject.inject(this);
		preview.setVisibility(View.GONE);

		reditor.setEditorFontSize(16);
		reditor.setEditorFontColor(Color.BLACK);
		reditor.setPadding(10, 10, 10, 10);
	}

	public RichTextEditor(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public RichTextEditor(Context context)
	{
		this(context, null);
	}

	public void chgMode()
	{
		setMode(!isEdit);
	}

	public void setMode(boolean isEdit)
	{
		if (this.isEdit != isEdit)
		{
			if (this.isEdit = isEdit)
			{
				preview.setVisibility(View.GONE);
				tool.setVisibility(View.VISIBLE);
				reditor.setVisibility(View.VISIBLE);
			}
			else
			{
				preview.setVisibility(View.VISIBLE);
				String content = reditor.getHtml();
				if (content != null)
					preview.setText(Html.fromHtml(content));
				else
					preview.setTag("");
				tool.setVisibility(View.GONE);
				reditor.setVisibility(View.GONE);
			}
		}
	}

	public RichEditor getEditor()
	{
		return reditor;
	}

	public String getContent()
	{
		return reditor.getHtml();
	}

	@Override
	public void onClick(View v)
	{
		if (v == action_undo)
			reditor.undo();
		else if (v == action_redo)
			reditor.redo();
		else if (v == action_bold)
			reditor.setBold();
		else if (v == action_italic)
			reditor.setItalic();
		else if (v == action_subscript)
			reditor.setSubscript();
		else if (v == action_superscript)
			reditor.setSuperscript();
		else if (v == action_strikethrough)
			reditor.setStrikeThrough();
		else if (v == action_underline)
			reditor.setUnderline();
		else if (v == action_heading1)
			reditor.setHeading(1);
		else if (v == action_heading2)
			reditor.setHeading(2);
		else if (v == action_heading3)
			reditor.setHeading(3);
		else if (v == action_heading4)
			reditor.setHeading(4);
		else if (v == action_heading5)
			reditor.setHeading(5);
		else if (v == action_heading6)
			reditor.setHeading(6);
		else if (v == action_txt_color)
		{
			reditor.setTextColor(isTxtColor ? Color.RED : Color.BLACK);
			isTxtColor = !isTxtColor;
		}
		else if (v == action_bg_color)
		{
			reditor.setTextBackgroundColor(isBgColor ? Color.YELLOW : Color.TRANSPARENT);
			isBgColor = !isBgColor;
		}
		else if (v == action_indent)
			reditor.setIndent();
		else if (v == action_outdent)
			reditor.setOutdent();
		else if (v == action_align_left)
			reditor.setAlignLeft();
		else if (v == action_align_center)
			reditor.setAlignCenter();
		else if (v == action_align_right)
			reditor.setAlignRight();
		else if (v == action_blockquote)
			reditor.setBlockquote();
		else if (v == action_insert_image)
			reditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
					"dachshund");
		else if (v == action_insert_link)
			reditor.insertLink("https://github.com/wasabeef", "wasabeef");
	}
}
