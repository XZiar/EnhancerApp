package xziar.enhancer.widget;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.SimpleImageUtil;
import xziar.enhancer.util.SizeUtil;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class ActionBar extends AppBarLayout implements OnClickListener
{
	private static final String LogTag = "ActionBar";
	private static final int imgPad, defElevaton;
	public final MenuItem action_top, action_back;
	private SoftReference<AppCompatActivity> ref;
	private Context context;
	private MenuInflater menuInf;
	private int tintColor;
	private ArrayList<View> padLeft = new ArrayList<>(), padRight = new ArrayList<>();
	private HashMap<ImageButton, MenuItem> btnMap = new HashMap<>();
	@BindView
	private LinearLayout leftBar, midBar, rightBar;
	@BindView(R.id.toolbar)
	private Toolbar toolbar;
	@BindView(R.id.title)
	private TextView tvTitle;
	@BindView(R.id.subtitle)
	private TextView tvSubtitle;
	@BindView(R.id.shadow)
	private View shadow;

	static
	{
		imgPad = SizeUtil.dp2px(12);
		defElevaton = SizeUtil.dp2px(4);
	}

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
		this.context = context;
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
		tintColor = ta.getColor(R.styleable.ActionBar_tintColor,
				ContextCompat.getColor(context, R.color.colorAccent));
		tvTitle.setTextColor(color);
		tvSubtitle.setTextColor(color);
		setTitle(ta.getText(R.styleable.ActionBar_android_title));
		setSubtitle(ta.getText(R.styleable.ActionBar_android_subtitle));
		int elevation = (int) ta.getDimension(R.styleable.ActionBar_android_elevation, defElevaton);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shadow.getLayoutParams();
		lp.height = elevation;
		shadow.setLayoutParams(lp);
		ta.recycle();

		action_top = new ActionMenuItem(context, 0, R.id.action_top, 100, 0, "BackToTop");
		action_back = new ActionMenuItem(context, 0, R.id.action_back, 100, 0, "Back");
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

	private ImageButton genButton()
	{
		ImageButton btn = new ImageButton(context, null, R.style.Widget_AppCompat_ActionButton);
		btn.setPadding(imgPad, 0, imgPad, 0);
		btn.setScaleType(ScaleType.FIT_CENTER);
		return btn;
	}

	protected ImageButton genButton(int resId)
	{
		return genButton(ContextCompat.getDrawable(context, resId));
	}

	protected ImageButton genButton(Drawable icon)
	{
		ImageButton btn = genButton();
		btn.setImageDrawable(SimpleImageUtil.tintDrawable(icon, tintColor));
		return btn;
	}

	protected void addButton(MenuItem item, ImageButton button, boolean isLeft)
	{
		btnMap.put(button, item);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		ArrayList<View> padObj, padOther;
		LinearLayout barObj, barOther;
		if (isLeft)
		{
			padObj = padLeft;
			padOther = padRight;
			barObj = leftBar;
			barOther = rightBar;
		}
		else
		{
			padObj = padRight;
			padOther = padLeft;
			barObj = rightBar;
			barOther = leftBar;
		}
		if (padObj.size() > padOther.size())
		{
			// replace object padding
			barObj.removeView(padObj.get(0));
			padObj.remove(0);
			barObj.addView(button, barObj.getChildCount() - padObj.size(), lp);
		}
		else
		{
			// add other padding
			View padView = genButton(R.drawable.icon_add);
			padOther.add(padView);
			barOther.addView(padView, -1);
			padView.setVisibility(View.INVISIBLE);
			barObj.addView(button, -1, lp);
		}
		button.setOnClickListener(this);
	}

	protected void delButton(ImageButton v)
	{
		MenuItem item = btnMap.remove(v);
		ArrayList<View> padObj, padOther;
		LinearLayout barObj, barOther;
		if (item.getGroupId() == R.id.leftMenu)
		{
			padObj = padLeft;
			padOther = padRight;
			barObj = leftBar;
			barOther = rightBar;
		}
		else
		{
			padObj = padRight;
			padOther = padLeft;
			barObj = rightBar;
			barOther = leftBar;
		}
		if (padObj.size() < padOther.size())
		{
			// remove other padding
			barOther.removeView(padOther.get(0));
			padOther.remove(0);
			barObj.removeView(v);
		}
		else
		{
			// add object padding
			View padView = genButton(R.drawable.icon_add);
			padObj.add(padView);
			barObj.removeView(v);
			barObj.addView(padView, -1);
			padView.setVisibility(View.INVISIBLE);
		}
	}

	protected ImageButton findButton(int menuId)
	{
		for (Map.Entry<ImageButton, MenuItem> e : btnMap.entrySet())
		{
			if (e.getValue().getItemId() == menuId)
				return e.getKey();
		}
		return null;
	}

	public void setMenu(int resId)
	{
		removeAllMenu();
		PopupMenu pm = new PopupMenu(context, null);
		Menu menu = pm.getMenu();
		menuInf.inflate(resId, menu);
		// add button
		for (int a = 0; a < menu.size(); a++)
		{
			MenuItem item = menu.getItem(a);
			boolean isLeft = (item.getGroupId() == R.id.leftMenu);
			ImageButton btn = genButton(item.getIcon());
			addButton(item, btn, isLeft);
		}
	}

	public void addMenu(int menuId, int resId, boolean isLeft)
	{
		ImageButton btn = genButton(resId);
		MenuItem item = new ActionMenuItem(context, isLeft ? R.id.leftMenu : R.id.rightMenu, menuId,
				100, 0, "");
		addButton(item, btn, isLeft);
	}

	public void setBackButton(boolean isEnable)
	{
		if (isEnable && !btnMap.values().contains(action_back))
		{
			ImageButton btn = genButton(R.drawable.icon_back);
			addButton(action_back, btn, true);
		}
	}

	public boolean enableButton(int menuId)
	{
		ImageButton btn = findButton(menuId);
		if (btn != null)
		{
			btn.setImageDrawable(SimpleImageUtil.tintDrawable(btn.getBackground(), tintColor));
			btn.setOnClickListener(this);
			return true;
		}
		return false;
	}

	public boolean disableButton(int menuId)
	{
		ImageButton btn = findButton(menuId);
		if (btn != null)
		{
			btn.setImageDrawable(SimpleImageUtil.tintDrawable(btn.getDrawable(), 0xAAAAAAAA));
			btn.setOnClickListener(null);
			return true;
		}
		return false;
	}

	public boolean delMenu(int menuId)
	{
		ImageButton btn = findButton(menuId);
		if (btn != null)
		{
			delButton(btn);
			return true;
		}
		return false;
	}

	public void removeAllMenu()
	{
		// remove all exist menu
		leftBar.removeAllViews();
		rightBar.removeAllViews();
		padLeft.clear();
		padRight.clear();
		btnMap.clear();
	}

	public void setupActionBar(AppCompatActivity activity)
	{
		this.ref = new SoftReference<AppCompatActivity>(activity);
		menuInf = activity.getMenuInflater();
	}

	public void setSubtitle(CharSequence txt)
	{
		tvSubtitle.setText(txt);
		if (TextUtils.isEmpty(txt))
			tvSubtitle.setVisibility(View.GONE);
		else
			tvSubtitle.setVisibility(View.VISIBLE);
	}

	public void setTitle(CharSequence txt)
	{
		tvTitle.setText(txt);
		if (TextUtils.isEmpty(txt))
			tvTitle.setVisibility(View.GONE);
		else
			tvTitle.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v)
	{
		MenuItem item = btnMap.get(v);
		Activity activity = ref.get();
		if (item != null && activity != null)
		{
			activity.onOptionsItemSelected(item);
		}
	}

}
