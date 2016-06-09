package xziar.enhancer.pojo;

import java.util.regex.Pattern;

public class StudentBean extends UserBean
{
	private boolean gender;
	private String id_person;
	private String school;
	private String id_student;
	private int time_enter;
	
	public StudentBean()
	{
		
	}
	
	public StudentBean(AccountBean account)
	{
		super(account);
	}

	public Boolean getGender()
	{
		return gender;
	}

	public void setGender(Boolean gender)
	{
		this.gender = gender;
	}

	public void setSgender(String gender)
	{
		if ("true".equals(gender))
			this.gender = true;
		else if ("false".equals(gender))
			this.gender = false;
	}
	
	public String getId_person()
	{
		return id_person;
	}
	public void setId_person(String id_person)
	{
		this.id_person = id_person;
	}
	public String getSchool()
	{
		return school;
	}
	public void setSchool(String school)
	{
		this.school = school;
	}
	public String getId_student()
	{
		return id_student;
	}
	public void setId_student(String id_student)
	{
		this.id_student = id_student;
	}
	public int getTime_enter()
	{
		return time_enter;
	}
	public void setTime_enter(int time_enter)
	{
		this.time_enter = time_enter;
	}
	public void setStime_enter(String time)
	{
		if(time == null)
		{
			time_enter = 0;
			System.out.println("null");
			return;
		}
		if(Pattern.matches("^[1,2][0,9][0-9]{2}-(([0][1-9])|([1][0-2]))$", time) == false)
		{
			System.out.println("false");
			time_enter = 0;
			return;
		}
		Integer dy = Integer.parseInt(time.substring(0, 4));
		Integer dm = Integer.parseInt(time.substring(5, 7));
		time_enter = dy * 100 + dm;
		System.out.println(time_enter);
	}
}
