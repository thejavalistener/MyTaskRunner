package thejavalistener.mtr.expr.ns;

public interface NamespaceOperation
{
	public abstract String getName();
	public abstract String resolve(String args[]) throws Exception;

}
