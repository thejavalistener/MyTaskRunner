package thejavalistener.mtr.expr;

public interface NamespaceHandler
{
    String getNamespace();

    /**
     * Recibe TODO lo que sigue al namespace.
     * Ej: ${time:today:format:yyyyMMdd} => payload = "today:format:yyyyMMdd"
     */
    String resolve(String payload);
}