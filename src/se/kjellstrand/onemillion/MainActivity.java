
package se.kjellstrand.onemillion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        LinearLayout layout = (LinearLayout) findViewById(R.id.fragment_container);

        Fragment fragment = new InputFragment();
        getSupportFragmentManager().beginTransaction().add(layout.getId(), fragment , "someTag1").commit();

    }
}
