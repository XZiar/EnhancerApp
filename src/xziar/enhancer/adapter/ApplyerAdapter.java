package xziar.enhancer.adapter;

import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class ApplyerAdapter extends CommonAdapter<JSONObject, ApplyerAdapter.ApplyerHolder>
{
	static class ApplyerHolder extends CommonHolder<JSONObject>
	{
		@BindView
		private TextView name, people;

		public ApplyerHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
		}

		@Override
		public void setData(JSONObject data, int idx, int type)
		{
			name.setText(data.getString("name"));
			people.setText(data.getString("people"));
		}
	}

	public ApplyerAdapter(Context context)
	{
		super(context, ApplyerHolder.class);
		resID.add(R.layout.item_applyer);
	}
}
