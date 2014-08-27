
package se.kjellstrand.onemillion;

import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.plus.PlusShare;

import android.support.v4.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ResultFragment extends Fragment {

    private static final String APP_PLAY_URL = "https://play.google.com/store/apps/details?id=se.kjellstrand.onemillion";

    public static final String RESULT_TEXT = "RESULT_TEXT";

    private String resultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.result, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            resultText = bundle.getString(RESULT_TEXT);
            ((TextView) getActivity().findViewById(R.id.result)).setText(resultText);
        }

        setupShare();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    private void setupShare() {
        final android.support.v4.app.Fragment frag = this;
        getActivity().findViewById(R.id.share_fb).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // start Facebook Login
                Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {

                    // callback when session changes state
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {
                        FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(getActivity())
                                .setLink(APP_PLAY_URL).setDescription(resultText).setFragment(frag);
                        // share the app
                        if (builder.canPresent()) {
                            builder.build().present();
                        }
                    }
                });
            }
        });

        getActivity().findViewById(R.id.share_tw).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                    tweetIntent.setType("*/*");
                    tweetIntent.putExtra(Intent.EXTRA_TEXT, getShareMessage() + "\n" + APP_PLAY_URL);
                    PackageManager pm = getActivity().getPackageManager();
                    List<ResolveInfo> lract = pm.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean resolved = false;
                    for (ResolveInfo ri : lract) {
                        if (ri.activityInfo.name.contains("twitter")) {
                            tweetIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                            resolved = true;
                            break;
                        }
                    }
                    startActivity(resolved ? tweetIntent : Intent.createChooser(tweetIntent, "Choose one"));
                } catch (final ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        getActivity().findViewById(R.id.share_gp).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Launch the Google+ share dialog with attribution to your app.
                Intent shareIntent = new PlusShare.Builder(getActivity()).setType("text/plain").setText(getShareMessage())
                        .setContentUrl(Uri.parse(APP_PLAY_URL)).getIntent();

                startActivity(shareIntent);
            }
        });
    }

    private String getShareMessage() {
        String text = ((TextView) getActivity().findViewById(R.id.result)).getText().toString();
        return text;
    }

}
