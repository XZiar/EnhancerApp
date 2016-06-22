package xziar.enhancer.pojo;

public class TaskBean
{
	public static enum Status
	{
		oncheck("审核中"), onapply("申请中"), onliscene("申请截止"), ongoing("进行中"), onfinish("已完结"), closed(
				"已关闭");
		private String txt;

		private Status(String txt)
		{
			this.txt = txt;
		}

		public String toTxt()
		{
			return txt;
		}
	}

	private int tid = -1;
	private int uid = -1;
	private int aid = -1;
	private String launcher;
	private String doer;
	private long time_start;
	private int time_last, time_modify;
	private int applycount;
	private Status status;
	private int payment;
	private String title;
	private String describe;
	private int sscored = -1;
	private int cscored = -1;
	private int limit_people, limit_score;

	public TaskBean()
	{
	}

	public TaskBean(int tid)
	{
		setTid(tid);
	}

	public int getTid()
	{
		return tid;
	}

	public void setTid(int tid)
	{
		this.tid = tid;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public long getTime_start()
	{
		return time_start;
	}

	public void setTime_start(long time_start)
	{
		this.time_start = time_start;
	}

	public int getTime_last()
	{
		return time_last;
	}

	public void setTime_last(int time_last)
	{
		this.time_last = time_last;
	}

	public int getTime_modify()
	{
		return time_modify;
	}

	public void setTime_modify(int time_modify)
	{
		this.time_modify = time_modify;
	}

	public int getApplycount()
	{
		return applycount;
	}

	public void setApplycount(int applycount)
	{
		this.applycount = applycount;
	}

	public Status getTaskStatus()
	{
		return status;
	}

	public void setTaskStatus(Status status)
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

	public int getPayment()
	{
		return payment;
	}

	public void setPayment(int payment)
	{
		this.payment = payment;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescribe()
	{
		return describe;
	}

	public void setDescribe(String describe)
	{
		this.describe = describe;
	}

	public int getLimit_people()
	{
		return limit_people;
	}

	public void setLimit_people(int limit_people)
	{
		this.limit_people = limit_people;
	}

	public int getLimit_score()
	{
		return limit_score;
	}

	public void setLimit_score(int limit_score)
	{
		this.limit_score = limit_score;
	}

	public String getLauncher()
	{
		return launcher;
	}

	public void setLauncher(String launcher)
	{
		this.launcher = launcher;
	}

	public int getAid()
	{
		return aid;
	}

	public void setAid(int aid)
	{
		this.aid = aid;
	}

	public String getDoer()
	{
		return doer;
	}

	public void setDoer(String doer)
	{
		this.doer = doer;
	}

	public int getSscored()
	{
		return sscored;
	}

	public void setSscored(int sscored)
	{
		this.sscored = sscored;
	}

	public int getCscored()
	{
		return cscored;
	}

	public void setCscored(int cscored)
	{
		this.cscored = cscored;
	}

}
