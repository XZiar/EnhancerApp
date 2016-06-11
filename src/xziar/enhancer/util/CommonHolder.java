package xziar.enhancer.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

public class CommonHolder<T> extends RecyclerView.ViewHolder
		implements OnClickListener
{
	public static interface OnItemClickListener<E>
	{
		public void OnClick(E data);
	}
	
	T obj;
	private OnItemClickListener<T> itemClick;

	public CommonHolder(View itemView)
	{
		super(itemView);
		itemView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if (itemClick != null)
			itemClick.OnClick(obj);
	}

	public void setData(T data)
	{
		obj = data;
	}

	public void setItemClick(OnItemClickListener<T> itemClick)
	{
		this.itemClick = itemClick;
	}
}
