package xziar.enhancer.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class NumberFilter implements InputFilter, OnFocusChangeListener
{
	private int min, max;

	public NumberFilter(int min, int max)
	{
		this.max = Math.max(min, max);
		this.min = Math.min(min, max);
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
			int dend)
	{
		try
		{
			String txt = dest.toString();
			// Remove the string out of destination that is to be replaced
			String newVal = txt.substring(0, dstart) + txt.substring(dend, txt.length());
			// Add the new string in
			newVal = newVal.substring(0, dstart) + source.toString()
					+ newVal.substring(dstart, newVal.length());
			int input = Integer.parseInt(newVal);
			if (min <= input && max >= input)
				return null;
			else
				onOutRange();
		}
		catch (NumberFormatException nfe)
		{
			onWrongType();
		}
		return "";
	}

	protected void onWrongType()
	{
	}

	protected void onOutRange()
	{
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if (!hasFocus)
		{
			EditText et = (EditText) v;
			if (TextUtils.isEmpty(et.getText()))
				et.setText("" + min);
		}
	}
}
