package thejavalistener.mtr.expr;

public interface NamespaceHandler
{
    public String getNamespace();

    /**
     * Recibe TODO lo que sigue al namespace.
     * Ej: ${time:today:format:yyyyMMdd} => payload = "today:format:yyyyMMdd"
     */
    public String resolve(String payload) throws Exception;
}