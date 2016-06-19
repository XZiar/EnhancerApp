package xziar.enhancer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class FragManager
{
	private HashMap<Integer, Fragment> fragMap = new HashMap<>();
	private HashMap<Integer, ArrayList<Object>> datMap = new HashMap<>();

	private int objId;
	private FragmentManager fragMan;
	private Fragment curFrag;
	private FragmentTransaction curTrans;

	public FragManager(Activity activity, int objId)
	{
		fragMan = activity.getFragmentManager();
		this.objId = objId;
	}

	private FragmentTransaction getTrans()
	{
		return curTrans == null ? curTrans = fragMan.beginTransaction() : curTrans;
	}

	public FragManager add(Fragment... frags)
	{
		FragmentTransaction trans = getTrans();
		for (Fragment frag : frags)
			trans.add(objId, frag);
		return this;
	}

	public FragManager add(Fragment frag, int id, Object... args)
	{
		FragmentTransaction trans = getTrans();
		trans.add(objId, frag);
		fragMap.put(id, frag);
		ArrayList<Object> dats = new ArrayList<>(Arrays.asList(args));
		datMap.put(id, dats);
		return this;
	}

	public FragManager hideAll()
	{
		FragmentTransaction trans = getTrans();
		for (Fragment frag : fragMap.values())
			trans.hide(frag);
		return this;
	}

	public FragManager show(Fragment frag)
	{
		FragmentTransaction trans = getTrans();
		if (fragMap.containsValue(frag))
			trans.show(curFrag = frag);
		return this;
	}

	public FragManager show(int fragId)
	{
		Fragment frag = fragMap.get(fragId);
		if (frag != null)
			return show(frag);
		return this;
	}

	public FragManager change(Fragment frag)
	{
		FragmentTransaction trans = getTrans();
		if (frag != curFrag && fragMap.containsValue(frag))
		{
			if (curFrag != null)
				trans.hide(curFrag);
			trans.show(curFrag = frag);
		}
		return this;
	}

	public FragManager change(int fragId)
	{
		Fragment frag = fragMap.get(fragId);
		if (frag != null)
			return change(frag);
		return this;
	}

	public Object getData(int fragId)
	{
		return getData(fragId, 0);
	}

	public Object getData(int fragId, int pos)
	{
		ArrayList<Object> dats = datMap.get(fragId);
		if (dats == null || dats.size() <= pos)
			return null;
		return dats.get(pos);
	}

	public Object getData(Fragment frag)
	{
		return getData(frag, 0);
	}

	public Object getData(Fragment frag, int pos)
	{
		for (Entry<Integer, Fragment> e : fragMap.entrySet())
		{
			if (e.getValue() == frag)
			{
				return getData(e.getKey(), pos);
			}
		}
		return null;
	}

	public void doit()
	{
		if (curTrans != null)
		{
			curTrans.commit();
			curTrans = null;
		}
	}

	public Fragment getCurFrag()
	{
		return curFrag;
	}
}
