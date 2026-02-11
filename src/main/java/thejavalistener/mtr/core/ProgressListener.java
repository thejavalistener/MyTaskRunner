package thejavalistener.mtr.core;

public interface ProgressListener
{
	public void onStart();
	public void onProgress(int percent);
	public void onFinish();			
}
