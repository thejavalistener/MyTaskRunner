package thejavalistener.mtr.core;

public interface ProgressListener
{
	void onProgress(long done, long total); // total puede ser -1 si no se
											// conoce
}
