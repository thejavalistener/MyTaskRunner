package thejavalistener.mtr.doc;

public class DocParam
{
	public String name;
	public String description;
	public String type;
	public String defaultValue;
	
	public DocParam(String name,String description,String type)
	{
		this(name,description,type,null);
	}
	
	public DocParam(String name,String description,String type,String defaultValue)
	{
		super();
		this.name=name;
		this.description=description;
		this.type=type;
		this.defaultValue = defaultValue;
	}
}