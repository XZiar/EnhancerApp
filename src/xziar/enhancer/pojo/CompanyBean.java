package xziar.enhancer.pojo;

public class CompanyBean extends UserBean
{
	private String name_legal;
	private String id_legal;
	private String pic_id;
	private String pic_coltd;
	private ImgBean img_id;
	private ImgBean img_coltd;
	private String cel;
	private String tel;
	private String addr;

	public CompanyBean()
	{
	}

	public CompanyBean(AccountBean account)
	{
		super(account);
	}

	public String getName_legal()
	{
		return name_legal;
	}

	public void setName_legal(String name_legal)
	{
		this.name_legal = name_legal;
	}

	public String getId_legal()
	{
		return id_legal;
	}

	public void setId_legal(String id_legal)
	{
		this.id_legal = id_legal;
	}

	public String getCel()
	{
		return cel;
	}

	public void setCel(String cel)
	{
		this.cel = cel;
	}

	public String getTel()
	{
		return tel;
	}

	public void setTel(String tel)
	{
		this.tel = tel;
	}

	public String getAddr()
	{
		return addr;
	}

	public void setAddr(String addr)
	{
		this.addr = addr;
	}

	public String getPic_coltd()
	{
		return pic_coltd;
	}

	public ImgBean getImg_coltd()
	{
		return img_coltd;
	}

	public void setPic_coltd(String pic_coltd)
	{
		this.pic_coltd = pic_coltd;
	}

	public void setImg_coltd(ImgBean img)
	{
		img_coltd = img;
	}

	public String getPic_id()
	{
		return pic_id;
	}

	public ImgBean getImg_id()
	{
		return img_id;
	}

	public void setPic_id(String pic_id)
	{
		this.pic_id = pic_id;
	}

	public void setImg_id(ImgBean img)
	{
		img_id = img;
	}
}
