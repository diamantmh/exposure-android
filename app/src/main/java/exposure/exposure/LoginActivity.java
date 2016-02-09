/*
This Activity exists for the purpose of initializing the Facebook SDK
and initializing the Fragment that the Facebook login button will
appear on. During its creation (via onCreate()), it creates the
LoginFragment and switches to its view.
 */

package exposure.exposure;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.facebook.FacebookSdk;

public class LoginActivity extends FragmentActivity {
    private static final String STATE_SELECTED_FRAGMENT_INDEX = "selected_fragment_index";
    public static final String FRAGMENT_TAG = "fragment_tag";
    private FragmentManager mFragmentManager;

    /*
    Initialize the FacebookSDK and bring up the fragment with the
    Facebook login button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, new LoginFragment(), FRAGMENT_TAG);
        transaction.commit();
    }
}