package hollarena.bernard.com.hollarena;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class SplashScreen extends Activity {
    FirebaseAuth mAuth;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Typewritter writer = (Typewritter)findViewById(R.id.typewiter);
        final Button registerBt = (Button) findViewById(R.id.registerBt);
         mAuth = FirebaseAuth.getInstance();
        //Add a character every 300ms
        final Thread timer = new Thread(){
            public void run(){
                try{
                    writer.setCharacterDelay(300);
                    writer.animateText("HOLLORENA");
                    sleep(5000);
//                    checkGPS();

                    if (mAuth.getCurrentUser() != null){
                        finish();
                        startActivity(new Intent(SplashScreen.this,LocationActivity.class));
                    } else {
                        finish();
                        startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    }

                } catch (Exception e){
                    e.printStackTrace();
                } finally {

                    writer.postOnAnimation(new Runnable() {
                        @Override
                        public void run() {
                           /* Log.e(TAG, "for button animation================");
                            registerBt.setVisibility(View.VISIBLE);
                            Animation myFadeInAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade);
                            myFadeInAnimation.setInterpolator(new BounceInterpolator());
                            registerBt.startAnimation(myFadeInAnimation);*/


                        }
                    });
                }
            }
        };
        timer.start();


    }
}
