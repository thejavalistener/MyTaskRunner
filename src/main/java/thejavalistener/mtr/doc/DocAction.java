package thejavalistener.mtr.doc;

import java.util.List;

public interface DocAction
{
    public String getActionName();
    public String getActionDescription();
    public List<DocParam> getParams();
    public List<DocOperator> getExecuteIfOperators();
}