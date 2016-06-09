package xziar.enhancer.pojo;

public class AccountBean
{
	public static enum Role
	{
		admin, student, company, group;
	}
	public static enum Status
	{
		unchecked, pass, banned;
	}

	protected Role role;
	protected Status status;
	protected int uid = -1;
	protected String un = "";
	protected String pwd = "";

	public AccountBean()
	{
		
	}
	
	public AccountBean(AccountBean account)
	{
		setAccountRole(account.getAccountRole());
		setAccountStatus(account.getAccountStatus());
		setUid(account.getUid());
		setUn(account.getUn());
		setPwd(account.getPwd());
	}
	
	public AccountBean(int uid, Role role)
	{
		setUid(uid);
		setAccountRole(role);
	}

	public Role getAccountRole()
	{
		return role;
	}

	public void setAccountRole(Role role)
	{
		this.role = role;
	}
	
	public int getRole()
	{
		return role.ordinal();
	}

	public void setRole(int role)
	{
		this.role = Role.values()[role];
	}
	
	public Status getAccountStatus()
	{
		return status;
	}

	public void setAccountStatus(Status status)
	{
		this.status = status;
	}
	
	public int getStatus()
	{
		return status.ordinal();
	}

	public void setStatus(int status)
	{
		this.status = Status.values()[status];
	}
	
	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public String getUn()
	{
		return un;
	}

	public void setUn(String un)
	{
		this.un = un;
	}

	public String getPwd()
	{
		return pwd;
	}

	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}
	
}
