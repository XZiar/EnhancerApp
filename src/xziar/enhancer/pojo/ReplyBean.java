package xziar.enhancer.pojo;

public class ReplyBean
{
	private int rid = -1;
	private int uid = -1;
	private int pid = -1;
	private long time_reply;
	private String describe = "";
	private String replyer = "";

	public ReplyBean()
	{
	}

	public int getRid()
	{
		return rid;
	}

	public void setRid(int rid)
	{
		this.rid = rid;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getPid()
	{
		return pid;
	}

	public void setPid(int pid)
	{
		this.pid = pid;
	}

	public long getTime_reply()
	{
		return time_reply;
	}

	public void setTime_reply(long time_reply)
	{
		this.time_reply = time_reply;
	}

	public String getDescribe()
	{
		return describe;
	}

	public void setDescribe(String describe)
	{
		this.describe = describe;
	}

	public String getReplyer()
	{
		return replyer;
	}

	public void setReplyer(String replyer)
	{
		this.replyer = replyer;
	}

}
