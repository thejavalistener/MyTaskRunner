package thejavalistener.mtr.json.expr;

public interface NamespaceOperation
{
	public abstract String getName();
	public abstract String resolve(String args[]) throws Exception;

}
