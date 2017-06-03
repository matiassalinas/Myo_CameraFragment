package io.github.matiassalinas.myocamerafragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragmentApi;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;

/**
 * Created by matias on 03-06-17.
 */

public class Myo {
    Button myoBtn;
    Hub hub;
    Context context;
    CameraFragmentApi cameraFragment;

    private DeviceListener mListener = new AbstractDeviceListener() {
        // onConnect() cuando el Myo se conecta.
        @Override
        public void onConnect(com.thalmic.myo.Myo myo, long timestamp) {
            myoBtn.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.colorPrimary));
        }
        // onDisconnect() cuando el Myo se desconecta
        @Override
        public void onDisconnect(com.thalmic.myo.Myo myo, long timestamp) {
            myoBtn.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.colorAccent));
        }
        // onArmSync() cuando el Myo se sincroniza. Se mostrara en que brazo se tiene el dispositivo
        @Override
        public void onArmSync(com.thalmic.myo.Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            //mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }
        // onArmUnsync() cuando el Myo se desincroniza
        @Override
        public void onArmUnsync(com.thalmic.myo.Myo myo, long timestamp) {
            //mTextView.setText(R.string.hello_world);
        }
        // onUnlock() cuando el Myo se desbloquea
        @Override
        public void onUnlock(com.thalmic.myo.Myo myo, long timestamp) {
            //mLockStateView.setText(R.string.unlocked);
        }
        // onLock() cuando el Myo se bloquea
        @Override
        public void onLock(com.thalmic.myo.Myo myo, long timestamp) {
            //mLockStateView.setText(R.string.locked);
        }
        // onPose() cuando se detecta una accion
        @Override
        public void onPose(com.thalmic.myo.Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    //mTextView.setText(getString(R.string.hello_world));
                    break;
                case REST:
                case DOUBLE_TAP:
                    //int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            //restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            //restTextId = R.string.arm_right;
                            break;
                    }
                    //mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    //mTextView.setText(getString(R.string.pose_fist));
                    if (cameraFragment != null) {
                        cameraFragment.switchActionPhotoVideo();
                    }
                    break;
                case WAVE_IN:
                    cameraFragment.switchActionPhotoVideo();
                    //mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    cameraFragment.switchActionPhotoVideo();
                    //mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    Log.d("FINGER","FINGER");
                    cameraFragment.switchActionPhotoVideo();
                    //mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(com.thalmic.myo.Myo.UnlockType.HOLD);
                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(com.thalmic.myo.Myo.UnlockType.TIMED);
            }
        }
    };

    public Myo(Button myoBtn,CameraFragmentApi cameraFragment, Context context) {
        this.myoBtn = myoBtn;
        this.context = context;
        this.cameraFragment = cameraFragment;
        this.hub = Hub.getInstance();
        if (!hub.init(context)) {
            Toast.makeText(context, "No se pudo inicializar el Hub. No es posible utilizar MYO", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }
        // DeviceListener callbacks.
        hub.addListener(mListener);
    }

    public boolean hubReady(){
        return this.hub.init(this.context);
    }
}
