package xziar.enhancer.widget;

import android.app.ProgressDialog;
import android.content.Context;
import xziar.enhancer.R;

public class WaitDialog extends ProgressDialog
{

	public WaitDialog(Context context, String msg)
	{
		super(context, R.style.Theme_AppCompat_Light_Dialog);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setIndeterminate(true);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		setMessage(msg);
	}

}
