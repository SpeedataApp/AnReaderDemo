package com.example.AnReaderDemo;

import com.example.ReaderDemo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class VoicePlayer
{
	//private SoundPool soundPool = null;
	private int soundID = 0;
	private AudioManager am = null;
	private static VoicePlayer mInst = null;
	private float audioCurrentVolume = 0;
	private Context m_ctx = null;
	//private boolean bLoadFinish = false;

	static public VoicePlayer GetInst(Context ctx)
	{
		if (mInst == null)
		{
			mInst = new VoicePlayer(ctx);
		}
		return mInst;
	}

	private VoicePlayer(Context ctx)
	{
		//soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		//soundID = soundPool.load(ctx, R.raw.msg, 1);
		//am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		//audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		m_ctx = ctx;
		
		//soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
		//{
			
		//	public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
		//	{
				// TODO Auto-generated method stub
		//		bLoadFinish = true;
		//	}
		//});
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		//if (soundPool != null)
		//{
		//	soundPool.release();
		//}
		super.finalize();
	}

	public void Play()
	{	
		SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		soundID = soundPool.load(m_ctx, R.raw.msg, 1);
		am = (AudioManager) m_ctx.getSystemService(Context.AUDIO_SERVICE);
		audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
		{
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
			{
				soundPool
				.play(soundID, audioCurrentVolume, audioCurrentVolume, 1, 0, 1);
				try
				{
					Thread.sleep(80);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				soundPool.unload(soundID);
				soundPool.release();
			}
		});
	}
}
