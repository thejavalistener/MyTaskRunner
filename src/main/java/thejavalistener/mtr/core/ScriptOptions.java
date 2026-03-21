package thejavalistener.mtr.core;

public class ScriptOptions
{
	private boolean showVarValues = false;
	private boolean simulationMode = false;
	private int closeDelaySeconds = 10;
	public boolean isShowVarValues()
	{
		return showVarValues;
	}
	public void setShowVarValues(boolean showVarValues)
	{
		this.showVarValues=showVarValues;
	}
	public boolean isSimulationMode()
	{
		return simulationMode;
	}
	public void setSimulationMode(boolean simulationMode)
	{
		this.simulationMode=simulationMode;
	}
	public int getCloseDelaySeconds()
	{
		return closeDelaySeconds;
	}
	public void setCloseDelaySeconds(int closeDelaySeconds)
	{
		this.closeDelaySeconds=closeDelaySeconds;
	}
}