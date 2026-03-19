package thejavalistener.mtr.doc;

import java.util.List;

public class NamespaceDocOperation
{
	public String name;
	public String description;
	public List<DocParam> params;
	public NamespaceDocOperation(String name,String description,List<DocParam> params)
	{
		super();
		this.name=name;
		this.description=description;
		this.params = params;
	}
}
