package io.github.matiassalinas.myocamerafragment;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            myo.unlock(com.thalmic.myo.Myo.UnlockType.HOLD);
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
            if(cameraFragment == null) return;
            switch (pose) {
                case UNKNOWN:
                    break;
                case REST:
                    break;
                case DOUBLE_TAP:
                    //Tomar foto o grabar
                    tomarFoto();
                    break;
                case FIST:
                    // Cambiar camara
                    cameraFragment.switchCameraTypeFrontBack();
                    break;
                case WAVE_IN:
                    //Flash
                    cameraFragment.toggleFlashMode();
                    break;
                case WAVE_OUT:
                    //Flash
                    cameraFragment.toggleFlashMode();
                    break;
                case FINGERS_SPREAD:
                    //Cambiar foto a video, o al reves.
                    cameraFragment.switchActionPhotoVideo();
                    break;
            }
            myo.unlock(com.thalmic.myo.Myo.UnlockType.HOLD);
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                myo.notifyUserAction();
            }
        }
    };

    private void tomarFoto(){
        /*
          Creo la carpeta MyoFiles, el nombre del archivo sera el timestamp, y lo guardo.
        */
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyoFiles");
        imagesFolder.mkdirs();
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                   @Override
                                                   public void onVideoRecorded(String filePath) {
                                                       Toast.makeText(context, "onVideoRecorded " + filePath, Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onPhotoTaken(byte[] bytes, String filePath) {
                                                       Toast.makeText(context, "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                                                   }
                                               },
                imagesFolder.getAbsolutePath(),
                name);
    }

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
