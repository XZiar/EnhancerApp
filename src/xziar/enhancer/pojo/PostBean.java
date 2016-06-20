package xziar.enhancer.pojo;

public class PostBean
{
	private int pid = -1;
	private int uid = -1;
	private int replycount;
	private int type;
	private String title = "";
	private long time_post;
	private String describe = "";
	private String poster = "";

	public PostBean()
	{
	}

	public int getPid()
	{
		return pid;
	}

	public void setPid(int pid)
	{
		this.pid = pid;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public long getTime_post()
	{
		return time_post;
	}

	public void setTime_post(long time_post)
	{
		this.time_post = time_post;
	}

	public String getDescribe()
	{
		return describe;
	}

	public void setDescribe(String describe)
	{
		this.describe = describe;
	}

	public String getPoster()
	{
		return poster;
	}

	public void setPoster(String poster)
	{
		this.poster = poster;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getReplycount()
	{
		return replycount;
	}

	public void setReplycount(int replycount)
	{
		this.replycount = replycount;
	}
}
