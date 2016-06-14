package xziar.enhancer.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import xziar.enhancer.R;
import xziar.enhancer.pojo.ImgBean;
import xziar.enhancer.util.ViewInject;
import xziar.enhancer.util.ViewInject.BindView;
import xziar.enhancer.util.ViewInject.ObjView;

@ObjView("view")
public class CpnRegFragment extends Fragment implements OnClickListener
{
	private View view;
	private ImgBean limg = new ImgBean(), cimg = new ImgBean();
	@BindView(R.id.realname)
	private EditText name;
	@BindView(R.id.name_legal)
	private EditText lname;
	@BindView(R.id.id_legal)
	private EditText lid;
	@BindView(R.id.addr)
	private EditText addr;
	@BindView(R.id.tel)
	private EditText tel;
	@BindView(R.id.cel)
	private EditText cel;
	@BindView(R.id.pic_legal)
	private ImageView lpic;
	@BindView(R.id.pic_coltd)
	private ImageView cpic;

	private static enum reqCode
	{
		picLegal, picColtd
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_cpn_reg, container, false);
		ViewInject.inject(this);
		lpic.setOnClickListener(this);
		cpic.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	public HashMap<String, Object> getData()
	{
		HashMap<String, Object> data = new HashMap<>();
		data.put("cpn.name", name.getText().toString());
		data.put("cpn.name_legal", lname.getText().toString());
		data.put("cpn.id_legal", lid.getText().toString());
		data.put("cpn.addr", addr.getText().toString());
		data.put("cpn.tel", tel.getText().toString());
		data.put("cpn.cel", cel.getText().toString());
		data.put("cpn.img_id.img", limg.getData());
		data.put("cpn.img_coltd.img", cimg.getData());
		return data;
	}

	@Override
	public void onClick(View v)
	{
		if (v == lpic || v == cpic)
		{
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent,
					(v == lpic ? reqCode.picLegal : reqCode.picColtd).ordinal());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		final ContentResolver cr = getActivity().getContentResolver();
		ImgBean objImg = null;
		ImageView objPic = null;
		switch (reqCode.values()[requestCode])
		{
		case picLegal:
			objImg = limg;
			objPic = lpic;
			break;
		case picColtd:
			objImg = cimg;
			objPic = cpic;
			break;
		}
		if (resultCode == Activity.RESULT_OK)
		{
			Uri uri = data.getData();
			Log.i("uri", uri.toString());
			try
			{
				InputStream ins = cr.openInputStream(uri);
				objImg.readImg(ins);
				ins.close();
				objPic.setImageBitmap(BitmapFactory.decodeByteArray(objImg.getData(), 0,
						objImg.getData().length));
			}
			catch (IOException e)
			{
				Log.e("CpnRegFragment", "error get image resource", e);
			}
		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			objPic.setImageResource(R.drawable.waitforimage);
			objImg = new ImgBean();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
