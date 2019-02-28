package edu.ucsd.cse110.team4personalbest.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import edu.ucsd.cse110.team4personalbest.FriendChat;

public class FirebaseMessageToChatMessageAdapter implements ChatMessaging {
    String TAG = FirebaseMessageToChatMessageAdapter.class.getSimpleName();

    @Override
    public void subscribe(FriendChat activity) {
                FirebaseMessaging.getInstance().subscribeToTopic(activity.DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
