package xziar.enhancer.pojo;

import java.util.HashMap;

public class GroupBean extends UserBean
{
	public static enum Role
	{
		leader, member;
	}
	
	private HashMap<Integer,Role> members = new HashMap<>();
	private int leaderID = -1;
	
	public GroupBean(AccountBean account)
	{
		super(account);
	}

	public void addMember(int muid, int role)
	{
		members.put(muid, Role.values()[role]);
		if(Role.leader.ordinal() == role)
			setLeaderID(muid);
	}
	
	public HashMap<Integer, Role> getMembers()
	{
		return members;
	}

	public void setMembers(HashMap<Integer, Role> members)
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
}
