package thejavalistener.mtr.doc;

import java.util.List;

public interface DocNamespace
{
    public String getNamespace();
    public String getDescription();
    public List<NamespaceDocOperation> getOperations();
}