package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import xziar.enhancer.R;

public class CompatRecyclerView extends RecyclerView
{

	public CompatRecyclerView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		setLayoutManager(new LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false));
		this.addItemDecoration(
				new DividerItemDecoration(context, R.drawable.divider_horizontal));
	}

	public CompatRecyclerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CompatRecyclerView(Context context)
	{
		this(context, null);
	}

}

class DividerItemDecoration extends RecyclerView.ItemDecoration
{

	private static final int[] ATTRS = new int[] { android.R.attr.listDivider };

	private Drawable divider;

	public DividerItemDecoration(Context context)
	{
		final TypedArray styledAttributes = context
				.obtainStyledAttributes(ATTRS);
		divider = styledAttributes.getDrawable(0);
		styledAttributes.recycle();
	}

	public DividerItemDecoration(Context context, int resId)
	{
		divider = ContextCompat.getDrawable(context, resId);
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
	{
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();

		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			View child = parent.getChildAt(i);

			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
					.getLayoutParams();

			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + divider.getIntrinsicHeight();

			divider.setBounds(left, top, right, bottom);
			divider.draw(c);
		}
	}
}