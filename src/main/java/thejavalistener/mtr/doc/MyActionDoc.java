package thejavalistener.mtr.doc;

import java.util.List;

public interface MyActionDoc
{
	public String getActionName();
	public String getActionDescription();
	public List<MyActionDocParam> getActionParams();
}
