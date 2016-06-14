package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.SizeUtil;

public class FloatLabelLayout extends LinearLayout
{
	TextView label;
	Context context;
	static final int pad = SizeUtil.dp2px(4), dheight = SizeUtil.dp2px(1);
	static ColorDrawable dividerImg = new ColorDrawable(0xff777777);

	public FloatLabelLayout(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		((LayoutInflater) (context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
						.inflate(R.layout.layout_float_label, this, true);
		label = (TextView) findViewById(R.id.hintlabel);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.hint }, 0, 0);
		String hint = ta.getString(0);
		if (hint == null)
			hint = "Label";
		label.setText(hint);
		ta.recycle();
		setPadding(pad, 0, pad, pad);
		ImageView divider = new ImageView(context);
		divider.setImageDrawable(dividerImg);
		divider.setPadding(0, pad, 0, 0);
		divider.setScaleType(ImageView.ScaleType.FIT_XY);
		super.addView(divider, -1,
				new LayoutParams(LayoutParams.MATCH_PARENT, pad + dheight));
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
		else
			index = getChildCount() - 1;
		super.addView(child, index, params);
	}

}
