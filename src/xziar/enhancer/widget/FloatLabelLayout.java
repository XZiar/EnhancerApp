package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import xziar.enhancer.R;

public class FloatLabelLayout extends LinearLayout
{
	TextView label;
	Context context;

	public FloatLabelLayout(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		((LayoutInflater) (getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
						.inflate(R.layout.layout_float_label, this, true);
		label = (TextView) findViewById(R.id.hintlabel);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.hint }, 0, 0);
		String hint = ta.getString(0);
		Log.i("FloatLabelLayout", hint);
		if (hint == null)
			hint = "HINT";
		label.setText(hint);
		ta.recycle();
	}

	public FloatLabelLayout(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public FloatLabelLayout(Context context)
	{
		this(context, null);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params)
	{
		if (index >= 0)
			index++;
		super.addView(child, index, params);
	}

}
