package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class ActionBar extends AppBarLayout
{
	private static final String LogTag = "ActionBar";
	android.support.v7.app.ActionBar supportActBar;
	@BindView(R.id.toolbar)
	private Toolbar toolbar;
	@BindView(R.id.title)
	private TextView tvTitle;
	@BindView(R.id.subtitle)
	private TextView tvSubtitle;

	private int transGravity(int val)
	{
		Log.v(LogTag, "gravity:" + val);
		switch (val)
		{
		case 1:
			return Gravity.START;
		case 2:
			return Gravity.END;
		case 0:
		default:
			return Gravity.CENTER;
		}
	}

	public ActionBar(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_actionbar, this, true);
		ViewInject.inject(this);
		toolbar.setContentInsetsAbsolute(0, 0);
		toolbar.setSubtitle("");
		toolbar.setTitle("");

		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionBar,
				defStyleAttr, 0);
		tvTitle.setGravity(transGravity(ta.getInt(R.styleable.ActionBar_gravityTitle, 0)));
		tvSubtitle.setGravity(transGravity(ta.getInt(R.styleable.ActionBar_gravitySubtitle, 0)));
		ta.recycle();
	}

	public ActionBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ActionBar(Context context)
	{
		this(context, null);
	}

	public Toolbar getToolbar()
	{
		return toolbar;
	}

	@Override
	public void setBackgroundColor(int color)
	{
		toolbar.setBackgroundColor(color);
	}

	@Override
	public void setBackgroundResource(int resid)
	{
		toolbar.setBackgroundResource(resid);
	}

	@Override
	public void setBackground(Drawable background)
	{
		toolbar.setBackground(background);
	}

	@Override
	public void setBackgroundDrawable(Drawable background)
	{
	}

	public void setupActionBar(AppCompatActivity activity)
	{
		activity.setSupportActionBar(toolbar);
		supportActBar = activity.getSupportActionBar();
	}

	public void setBackButton(boolean isEnable)
	{
		if (supportActBar != null)
		{
			supportActBar.setDisplayHomeAsUpEnabled(isEnable);
			toolbar.setContentInsetsAbsolute(0, 0);
		}
	}

	public void setSubtitle(CharSequence txt)
	{
		tvSubtitle.setText(txt);
	}

	public void setTitle(CharSequence txt)
	{
		tvTitle.setText(txt);
	}

}
