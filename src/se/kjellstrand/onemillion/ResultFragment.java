
package se.kjellstrand.onemillion;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ResultFragment extends Fragment {

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
        if (resultText != null && bundle != null) {
            resultText = bundle.getString(RESULT_TEXT);
            ((TextView) getActivity().findViewById(R.id.result)).setText(resultText);
        }

        setupShare();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        Log.d("TAG", "result " + resultText);
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
                                .setLink("https://developers.facebook.c)om/android").setDescription(resultText)
                                .setName("some name").setFragment(frag);
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
            }
        });
    }

    private String getShareMessage() {
        String text = ((TextView) getActivity().findViewById(R.id.result)).getText().toString();
        return text;
    }

}
