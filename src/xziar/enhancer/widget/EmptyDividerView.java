package xziar.enhancer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class EmptyDividerView extends View
{

	public EmptyDividerView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public EmptyDividerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public EmptyDividerView(Context context)
	{
		this(context, null);
	}

}
