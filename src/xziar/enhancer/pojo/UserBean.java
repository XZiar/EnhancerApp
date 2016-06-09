package xziar.enhancer.pojo;

public class UserBean extends AccountBean
{
	protected String name = "";
	protected String describe = "";
	protected int score = 0;
	protected int task_finish = 0, task_ongoing = 0;

	public UserBean()
	{

	}

	public UserBean(AccountBean account)
	{
		super(account);
	}
	
	public UserBean(String un, String pwd)
	{
		this.un = un;
		this.pwd = pwd;
	}

	public UserBean(int uid, int people, String name, String describe,
			int score, int task_finish, int task_ongoing)
	{
		setUid(uid);
		setName(name);
		setDescribe(describe);
		setScore(score);
		setTask_finish(task_finish);
		setTask_ongoing(task_ongoing);
	}

	public void copy(UserBean user)
	{
		this.uid = user.uid;
		setName(user.name);
		setDescribe(user.describe);
		setScore(user.score);
		setTask_finish(user.task_finish);
		setTask_ongoing(user.task_ongoing);
	}


	public int getPeople()
	{
		return 1;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	

	public String getDescribe()
	{
		return describe;
	}

	public void setDescribe(String describe)
	{
		this.describe = describe;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getTask_finish()
	{
		return task_finish;
	}

	public void setTask_finish(int task_finish)
	{
		this.task_finish = task_finish;
	}

	public int getTask_ongoing()
	{
		return task_ongoing;
	}

	public void setTask_ongoing(int task_ongoing)
	{
		this.task_ongoing = task_ongoing;
	}
}
