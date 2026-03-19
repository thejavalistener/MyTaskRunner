package thejavalistener.mtr.doc;

import java.util.List;

public interface DocAction
{
    public String getActionName();
    public List<DocParam> getParams();
    public List<DocOperator> getExecuteIfOperators();
    public List<String> getExamples();
}