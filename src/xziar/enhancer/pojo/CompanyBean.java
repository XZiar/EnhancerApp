package xziar.enhancer.pojo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CompanyBean extends UserBean
{
	private String name_legal;
	private String id_legal;
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
	
	public ImgBean getImg_id()
	{
		return img_id;
	}
	public void setImg_id(ImgBean img_id)
	{
		this.img_id = img_id;
	}
	public InputStream getPic_id()
	{
		return new ByteArrayInputStream(img_id.getData());
	}
	public void setPic_id(byte[] data)
	{
		img_id = new ImgBean(data);
	}
	
	public ImgBean getImg_coltd()
	{
		return img_coltd;
	}
	public void setImg_coltd(ImgBean img_coltd)
	{
		this.img_coltd = img_coltd;
	}
	public InputStream getPic_coltd()
	{
		return new ByteArrayInputStream(img_coltd.getData());
	}
	public void setPic_coltd(byte[] data)
	{
		img_coltd = new ImgBean(data);
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
}
