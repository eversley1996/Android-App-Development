package vandy.mooc.pingpong.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vandy.mooc.pingpong.R;
import vandy.mooc.pingpong.activities.MainActivity;
import vandy.mooc.pingpong.utils.Utils;

/**
 * A broadcast receiver that handles "ping" intents.
 */
public class PingReceiver
        extends BroadcastReceiver {
    /**
     * Intent sent to the PingReceiver.
     */
    public final static String ACTION_VIEW_PING =
            "vandy.mooc.action.VIEW_PING";
    /**
     * String displayed in system notification.
     */
    private static String mTitle = "Ping";

    /**
     * Intent extra key strings.
     */
    private static String COUNT = "COUNT";
    private static String NOTIFICATION_ID = "NOTIFICATION_ID";

    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG =
            getClass().getSimpleName();
    /**
     * Number of times to play "ping/pong".
     */
    private final int mMaxCount;

    /**
     * Reference to the enclosing activity so we can call it back when
     * "ping/pong" is done.
     */
    private MainActivity mActivity;

    /**
     * Constructor sets the fields.
     */
    public PingReceiver(MainActivity activity, int maxCount) {
        mActivity = activity;
        mMaxCount = maxCount;
    }

    /**
     * Factory method that makes a "ping" intent with the given @a count as an
     * extra.
     */
    public static Intent makePingIntent(
            Context context,
            int count,
            int notificationId) {
        return new Intent(PingReceiver.ACTION_VIEW_PING)
                .putExtra(COUNT, count)
                .putExtra(NOTIFICATION_ID, notificationId)
                // Limit receivers to components in this app's package.
                .setPackage(context.getPackageName());
    }

    /**
     * Hook method called by the Android ActivityManagerService framework after
     * a broadcast has sent.
     *
     * @param context The caller's context.
     * @param ping    An intent containing ping data.
     */
    @Override
    public void onReceive(
            Context context,
            Intent ping) {
        // Get the count from the PongReceiver.
        Integer count = ping.getIntExtra(COUNT, 0);

        Log.d(TAG, "onReceive() called with count of "
                + count);

        // Get the application notification id for updating the status bar
        // notification entry.
        int notificationId = ping.getIntExtra(NOTIFICATION_ID, 1);

        // If we're done then pop a toast and tell MainActivity we've
        // stop playing.
        if (count > mMaxCount) {
            // Update the status bar notification.
            Utils.updateStatusBar(context, mTitle, R.drawable.ping,
                                  notificationId);

            // Inform the activity we've stopped playing.
            mActivity.stopPlaying();
        }
        // If we're not done then pop a toast and "go async" by
        // creating a thread and broadcasting a "pong" intent.
        else {
            // Update the status bar notification.
            Utils.updateStatusBar(context,
                                  mTitle + " " + count,
                                  R.drawable.ping,
                                  notificationId);
            // Broadcast a "pong" intent with given count.
            context.sendBroadcast(
                    PongReceiver.makePongIntent(context,
                                                count,
                                                notificationId));
        }
    }
}


