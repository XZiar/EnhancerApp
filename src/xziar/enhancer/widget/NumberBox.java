package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.SizeUtil;

public class NumberBox extends LinearLayout
{
	private static int defTextSize = SizeUtil.sp2px(22);
	TextView label, val;
	Context context;

	public NumberBox(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		View v = ((LayoutInflater) (context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
						.inflate(R.layout.item_infobox, this, true);
		label = (TextView) v.findViewById(R.id.label);
		val = (TextView) v.findViewById(R.id.val);
		{
			TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
					R.styleable.NumberBox, defStyleAttr, 0);
			float txtsize = SizeUtil.px2sp((int) ta.getDimension(
					R.styleable.NumberBox_android_textSize, defTextSize));
			val.setTextSize(txtsize);
			label.setTextSize(txtsize * 0.6f);
			CharSequence ltxt = ta.getText(R.styleable.NumberBox_android_label);
			if (ltxt == null)
				ltxt = "ITEM";
			setLabel(ltxt);
			setVal(ta.getText(R.styleable.NumberBox_android_text));
			ta.recycle();
		}
	}

	public NumberBox(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public NumberBox(Context context)
	{
		this(context, null);
	}

	public void setLabel(CharSequence txt)
	{
		label.setText(txt);
	}

	public void setVal(CharSequence value)
	{
		val.setText(value);
	}

	public void setVal(int value)
	{
		val.setText("" + value);
	}

	public String getVal()
	{
		return val.getText().toString();
	}

}
