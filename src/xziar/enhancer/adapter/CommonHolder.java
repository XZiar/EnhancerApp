package xziar.enhancer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("itemView")
public abstract class CommonHolder<T> extends RecyclerView.ViewHolder
{
	public static interface OnItemClickListener<E>
	{
		public void OnClick(E data);
	}

	public CommonHolder(View itemView)
	{
		super(itemView);
		itemView.setTag(this);
	}

	public abstract void setData(T data, int idx, int type);
}
