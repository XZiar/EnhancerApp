package xziar.enhancer.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonAdapter;

public class CompatRecyclerView extends RecyclerView
{
	private static final String LogTag = "CompatRecyclerView";
	private final ArrayList<View> tmpView = new ArrayList<>();
	private RecyclerScrollHelper scrollHelper = new RecyclerScrollHelper();
	private Context context;

	public CompatRecyclerView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
		setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CompatRecyclerView, defStyleAttr, 0);
		if (ta.getBoolean(R.styleable.CompatRecyclerView_useDivider, true))
		{
			int rid = ta.getResourceId(R.styleable.CompatRecyclerView_android_divider,
					R.drawable.divider_horizontal);
			addItemDecoration(new DividerItemDecoration(context, rid));
		}
		ta.recycle();
		addOnScrollListener(scrollHelper);
	}

	public CompatRecyclerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CompatRecyclerView(Context context)
	{
		this(context, null);
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params)
	{
		if (this.getAdapter() != null)
			super.addView(child, index, params);
		else
		{
			Log.v(LogTag, "add:" + child + " at " + index + " with " + params);
			child.setLayoutParams(params);
			tmpView.add(child);
		}
	}

	protected View findViewTraversal(@IdRes int id)
	{
		if (id == this.getId())
			return this;
		for (View v : tmpView)
		{
			View res = v.findViewById(id);
			if (res != null)
				return res;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void scrollToIndex(int idx)
	{
		LinearLayoutManager llman = (LinearLayoutManager) getLayoutManager();
		int firstItem = llman.findFirstVisibleItemPosition();
		int lastItem = llman.findLastVisibleItemPosition();
		int pos = idx;
		CommonAdapter cadapter;
		if (getAdapter() != null && getAdapter() instanceof CommonAdapter)
		{
			cadapter = (CommonAdapter) getAdapter();
			pos = cadapter.idx2pos(idx);
		}
		if (pos <= firstItem)// upper than screen
		{
			smoothScrollToPosition(pos);
		}
		else if (pos <= lastItem)// already on screen
		{
			int top = getChildAt(pos - firstItem).getTop();
			smoothScrollBy(0, top);
		}
		else
		{
			smoothScrollToPosition(pos);
			scrollHelper.isStillMove = true;
			scrollHelper.objpos = pos;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setAdapter(Adapter adapter)
	{
		super.setAdapter(adapter);
		if (adapter instanceof CommonAdapter)
		{
			View header = tmpView.size() > 0 ? tmpView.get(0) : null;
			View footer = tmpView.size() > 1 ? tmpView.get(1) : null;
			((CommonAdapter) adapter).setHeaderFooter(header, footer);
		}
	}
}

class RecyclerScrollHelper extends RecyclerView.OnScrollListener
{
	boolean isStillMove = false;
	int objpos;

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy)
	{
		super.onScrolled(recyclerView, dx, dy);
		if (isStillMove)
		{
			isStillMove = false;
			int dev = objpos - ((LinearLayoutManager) recyclerView.getLayoutManager())
					.findFirstVisibleItemPosition();
			if (0 <= dev && dev < recyclerView.getChildCount())
			{
				int top = recyclerView.getChildAt(dev).getTop();
				recyclerView.smoothScrollBy(0, top);
			}
		}
	}

}

class DividerItemDecoration extends RecyclerView.ItemDecoration
{

	private static final int[] ATTRS = new int[] { android.R.attr.listDivider };

	private Drawable divider;

	public DividerItemDecoration(Context context)
	{
		final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
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

			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + divider.getIntrinsicHeight();

			divider.setBounds(left, top, right, bottom);
			divider.draw(c);
		}
	}
}