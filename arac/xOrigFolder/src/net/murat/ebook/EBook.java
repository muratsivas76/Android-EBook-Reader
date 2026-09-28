package net.murat.ebook;

import android.app.Activity;

import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;

public class EBook
extends Activity
{

  public void onCreate (Bundle paramBundle)
  {
    super.onCreate (paramBundle);

    requestWindowFeature (Window.FEATURE_NO_TITLE);

    getWindow ().setFlags (
      WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN
    );

    ScenePanel panel=new ScenePanel (this);
    panel.init ();

    setContentView (panel);
  }//oncreate end

  protected void onStop()
  {
    super.onStop();
  }

  protected void onResume()
  {
    super.onResume();
  }

  protected void onPause()
  {
    super.onPause();
  }

  protected void onDestroy ()
  {
    super.onDestroy ();
    android.os.Process.killProcess (android.os.Process.myPid ());
    System.exit (0);
  }

}
