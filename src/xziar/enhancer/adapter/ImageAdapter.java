package xziar.enhancer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import xziar.enhancer.R;
import xziar.enhancer.util.ImageUtil.ImgHolder;
import xziar.enhancer.util.ImageUtil.ImgViewHolder;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;

public class ImageAdapter extends CommonAdapter<ImgHolder, ImageAdapter.ImageHolder>
{
	static class ImageHolder extends CommonHolder<ImgHolder>
	{
		@BindView
		private ImageView pic;
		private ImgViewHolder holder;

		public ImageHolder(View itemView)
		{
			super(itemView);
			ViewInject.inject(this);
			holder = new ImgViewHolder(pic);
		}

		@Override
		public void setData(ImgHolder data, int idx, int type)
		{
			Log.v("CommonH", "setData:" + data);
			holder.getDrawable(data);
		}

	}

	public ImageAdapter(Context context)
	{
		super(context, ImageHolder.class);
		resID.add(R.layout.item_image);
	}

	@Override
	public void refresh(ArrayList<ImgHolder> datas)
	{
		super.refresh(datas);
	}

	@Override
	public int compare(ImgHolder lhs, ImgHolder rhs)
	{
		return 0;
	}
}
