package ir.test.example.smartgeiger.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ir.test.example.smartgeiger.R;


public class MyPreferences
{
	public static final int NULL_INT = -1;
	public static final long NULL_LONG = -1L;
	public static final double NULL_DOUBLE = -1.0;
	public static final String NULL_STRING = null;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	private Context mContext;

	
	public MyPreferences(Context context)
	{
		if(context != null) {
			mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			mEditor = mSharedPreferences.edit();
			mContext = context;
		}
	}

	public boolean isFirstTime()
	{
		String key = mContext.getString(R.string.prefs_key_firsttime);
		if (mSharedPreferences.getBoolean(key, true)) {
			mEditor.putBoolean(key, false);
			mEditor.commit();
			return true;
		}
		return false;
	}
	
	
	public void clearPreferences()
	{
		mEditor.clear();
		mEditor.commit();
	}

	public void restoreDefualts()
	{
		mEditor.putInt(mContext.getString(R.string.prefs_key_CPM), 100);
		mEditor.putInt(mContext.getString(R.string.prefs_key_SV), 100);
		mEditor.commit();

	}


	public int getCPM()
	{
		String key = mContext.getString(R.string.prefs_key_CPM);
		return mSharedPreferences.getInt(key, NULL_INT);
	}

	public void setCPM(int key)
	{
		mEditor.putInt(mContext.getString(R.string.prefs_key_CPM), key);
		mEditor.commit();
	}

	public int getSv()
	{
		String key = mContext.getString(R.string.prefs_key_SV);
		return mSharedPreferences.getInt(key, NULL_INT);
	}

	public void setSv(int key)
	{
		mEditor.putInt(mContext.getString(R.string.prefs_key_SV), key);
		mEditor.commit();
	}

}
