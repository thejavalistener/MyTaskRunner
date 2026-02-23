package thejavalistener.mtr.expr.ns;

public interface NamespaceOperation
{
	public String getName();
	public String resolve(String args[]) throws Exception;
}
