package xziar.enhancer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.SizeUtil;
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
	@BindView(R.id.shadow)
	private View shadow;

	private int transGravity(int val)
	{
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
		int color = ta.getColor(R.styleable.ActionBar_android_textColor,
				ContextCompat.getColor(context, android.R.color.black));
		tvTitle.setTextColor(color);
		tvSubtitle.setTextColor(color);
		setTitle(ta.getText(R.styleable.ActionBar_android_title));
		setSubtitle(ta.getText(R.styleable.ActionBar_android_subtitle));
		int elevation = (int) ta.getDimension(R.styleable.ActionBar_android_elevation,
				SizeUtil.dp2px(3));
		shadow.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, elevation));
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
		if (TextUtils.isEmpty(txt))
			tvSubtitle.setVisibility(View.GONE);
		else
			tvSubtitle.setVisibility(View.VISIBLE);
		tvSubtitle.setText(txt);
	}

	public void setTitle(CharSequence txt)
	{
		if (TextUtils.isEmpty(txt))
			tvTitle.setVisibility(View.GONE);
		else
			tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(txt);
	}

}
