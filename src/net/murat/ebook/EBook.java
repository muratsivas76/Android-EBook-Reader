package net.murat.ebook;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.Window;
import android.view.WindowManager;

// Java
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

// custom
import net.murat.sayfas.*;

public class EBook extends Activity {

  private final Handler uiHandler = new Handler(Looper.getMainLooper());

  @SuppressWarnings("deprecation")
  @Override
  public void onCreate (Bundle paramBundle) {
    super.onCreate (paramBundle);

    requestWindowFeature (Window.FEATURE_NO_TITLE);

    getWindow ().setFlags (
      WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN
    );

    installPages();

    ScenePanel panel=new ScenePanel (this);
    panel.init ();

    setContentView (panel);
  }//oncreate end

  private void installPages() {
    AInfos.addSayfa(new ASayfa_00000());

    Adding pa = new Adding();
    pa.addAll();

    AInfos.generate();
  }

  protected void onStop() {
    super.onStop();
  }

  protected void onResume() {
    super.onResume();
  }

  protected void onPause() {
    super.onPause();
  }

  protected void onDestroy() {
    super.onDestroy ();

    uiHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          android.os.Process.killProcess(android.os.Process.myPid());
          System.exit(0);
        }
    }, 250);
  }

}
