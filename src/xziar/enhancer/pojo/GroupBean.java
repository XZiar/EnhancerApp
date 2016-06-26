package xziar.enhancer.pojo;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupBean extends UserBean
{
	public static enum Role
	{
		leader, member;
	}
	
	public HashMap<String, Role> members = new HashMap<>();
	private int leaderID = -1;
	private ArrayList<String> stus = new ArrayList<>();
	
	public GroupBean(AccountBean account)
	{
		super(account);
	}

	public void addMember(int muid, int role)
	{
		members.put("" + muid, Role.values()[role]);
		if(Role.leader.ordinal() == role)
			setLeaderID(muid);
	}
	
	public HashMap<String, Role> getMembers()
	{
		return members;
	}

	public void setMembers(HashMap<String, Role> members)
	{
		this.members = members;
	}

	@Override
	public int getPeople()
	{
		return members.size();
	}

	public int getLeaderID()
	{
		return leaderID;
	}

	public void setLeaderID(int leaderID)
	{
		this.leaderID = leaderID;
	}

	public ArrayList<String> getStus()
	{
		return stus;
	}

	public void setStus(ArrayList<String> stus)
	{
		this.stus = stus;
	}
}
