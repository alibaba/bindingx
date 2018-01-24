package com.alibaba.android.binding.plugin.weex.internal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.binding.plugin.weex.LogProxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * 获取 Device Orientation
 *
 * Created by rowandjj(chuyi)<br/>
 */

class OrientationDetector implements SensorEventListener {

    // These fields are lazily initialized by getHandler().
    private HandlerThread mThread;

    private Handler mHandler;

    // A reference to the application context in order to acquire the SensorService.
    private final Context mAppContext;


    // The geomagnetic vector expressed in the body frame.
    private float[] mMagneticFieldVector;

    // Holds a shortened version of the rotation vector for compatibility purposes.
    private float[] mTruncatedRotationVector;

    // Holds current rotation matrix for the device.
    private float[] mDeviceRotationMatrix;

    // Holds Euler angles corresponding to the rotation matrix.
    private double[] mRotationAngles;

    // Lazily initialized when registering for notifications.
    private SensorManagerProxy mSensorManagerProxy;

    // The only instance of that class and its associated lock.
    private static OrientationDetector sSingleton;
    private static final Object sSingletonLock = new Object();

    private static final Set<Integer> DEVICE_ORIENTATION_SENSORS_A = Utils.newHashSet(
            Sensor.TYPE_GAME_ROTATION_VECTOR);
    private static final Set<Integer> DEVICE_ORIENTATION_SENSORS_B = Utils.newHashSet(
            Sensor.TYPE_ROTATION_VECTOR);
    // Option C backup sensors are used when options A and B are not available.
    private static final Set<Integer> DEVICE_ORIENTATION_SENSORS_C = Utils.newHashSet(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD);


    private final Set<Integer> mActiveSensors = new HashSet<>();
    private final List<Set<Integer>> mOrientationSensorSets;
    private Set<Integer> mDeviceOrientationSensors;
    private boolean mDeviceOrientationIsActive;
    private boolean mDeviceOrientationIsActiveWithBackupSensors;
    private boolean mOrientationNotAvailable;

    private ArrayList<OnOrientationChangedListener> mListeners = new ArrayList<>();


    private OrientationDetector(@NonNull Context context) {
        // use Application Context!
        mAppContext = context.getApplicationContext();

        mOrientationSensorSets = Utils.newArrayList(DEVICE_ORIENTATION_SENSORS_A,
                DEVICE_ORIENTATION_SENSORS_B,
                DEVICE_ORIENTATION_SENSORS_C);
    }

    static OrientationDetector getInstance(Context appContext) {
        synchronized (sSingletonLock) {
            if (sSingleton == null) {
                sSingleton = new OrientationDetector(appContext);
            }
            return sSingleton;
        }
    }

    /**
     * 切记不需要监听的时候调用removeOrientationChangedListener
     */
    void addOrientationChangedListener(@NonNull OnOrientationChangedListener listener) {
        if (this.mListeners != null && !mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    boolean removeOrientationChangedListener(@Nullable OnOrientationChangedListener listener) {
        if (this.mListeners != null) {
            if (listener == null) {
                this.mListeners.clear();
                return true;
            } else {
                return this.mListeners.remove(listener);
            }
        }
        return false;
    }

    // For orientation we use a 3-way fallback approach where up to 3 different sets of sensors
    // are attempted if necessary. The sensors to be used for orientation are determined in the
    // following order:
    //   A: GAME_ROTATION_VECTOR (relative)
    //   B: ROTATION_VECTOR (absolute)
    //   C: combination of ACCELEROMETER and MAGNETIC_FIELD (absolute)
    // Some of the sensors may not be available depending on the device and Android version, so
    // the 3-way fallback ensures selection of the best possible option.
    // Examples:
    //   * Nexus 9, Android 5.0.2 --> option A
    //   * Nexus 10, Android 5.1 --> option B
    //   * Moto G, Android 4.4.4  --> option C
    private boolean registerOrientationSensorsWithFallback(int rateInMicroseconds) {
        if (mOrientationNotAvailable) return false;
        if (mDeviceOrientationSensors != null) {
            String type = getOrientationSensorTypeUsed();
            LogProxy.d("[OrientationDetector] register sensor:"+type);
            return registerSensors(mDeviceOrientationSensors, rateInMicroseconds, true);
        }
        ensureRotationStructuresAllocated();

        for (Set<Integer> sensors : mOrientationSensorSets) {
            mDeviceOrientationSensors = sensors;
            if (registerSensors(mDeviceOrientationSensors, rateInMicroseconds, true)) {
                String type = getOrientationSensorTypeUsed();
                LogProxy.d("[OrientationDetector] register sensor:"+type);
                return true;
            }
        }

        mOrientationNotAvailable = true;
        mDeviceOrientationSensors = null;
        mDeviceRotationMatrix = null;
        mRotationAngles = null;
        return false;
    }

    private String getOrientationSensorTypeUsed() {
        if (mOrientationNotAvailable) {
            return "NOT_AVAILABLE";
        }
        if (mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_A) {
            return "GAME_ROTATION_VECTOR";
        }
        if (mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_B) {
            return "ROTATION_VECTOR";
        }
        if (mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_C) {
            return "ACCELEROMETER_MAGNETIC";
        }
        return "NOT_AVAILABLE";
    }

    /**
     * Start listening for sensor events. If this object is already listening
     * for events, the old callback is unregistered first.
     *
     * @param rateInMicroseconds Requested callback rate in microseconds. The actual rate may be
     *                           higher. Unwanted events should be ignored.
     * @return True on success.
     */
    public boolean start(int rateInMicroseconds) {
        LogProxy.d("[OrientationDetector] sensor started");
        boolean success = registerOrientationSensorsWithFallback(rateInMicroseconds);
        if (success) {
            setEventTypeActive(true);
        }
        return success;
    }

    /**
     * Stop listening to sensors for a given event type. Ensures that sensors are not disabled
     * if they are still in use by a different event type.
     */
    void stop() {
        LogProxy.d("[OrientationDetector] sensor stopped");
        Set<Integer> sensorsToDeactivate = new HashSet<>(mActiveSensors);
        unregisterSensors(sensorsToDeactivate);
        setEventTypeActive(false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] values = event.values;

        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                if (mDeviceOrientationIsActiveWithBackupSensors) {
                    getOrientationFromGeomagneticVectors(values, mMagneticFieldVector);
                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                if (mDeviceOrientationIsActive
                        && mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_B) {
                    // only compute if not already computed for absolute orientation above.
                    convertRotationVectorToAngles(values, mRotationAngles);
                    gotOrientation(mRotationAngles[0], mRotationAngles[1], mRotationAngles[2]);
                }
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                if (mDeviceOrientationIsActive) {
                    convertRotationVectorToAngles(values, mRotationAngles);
                    gotOrientation(mRotationAngles[0], mRotationAngles[1], mRotationAngles[2]);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (mDeviceOrientationIsActiveWithBackupSensors) {
                    if (mMagneticFieldVector == null) {
                        mMagneticFieldVector = new float[3];
                    }
                    System.arraycopy(values, 0, mMagneticFieldVector, 0,
                            mMagneticFieldVector.length);
                }
                break;
            default:
                // Unexpected
                LogProxy.e("unexpected sensor type:" + type);
        }

    }

    /**
     * Returns orientation angles from a rotation matrix, such that the angles are according
     * to spec http://dev.w3.org/geo/api/spec-source-orientation.html.
     * <p>
     * It is assumed the rotation matrix transforms a 3D column vector from device coordinate system
     * to the world's coordinate system, as e.g. computed by {@see SensorManager.getRotationMatrix}.
     * <p>
     * In particular we compute the decomposition of a given rotation matrix R such that <br>
     * R = Rz(alpha) * Rx(beta) * Ry(gamma), <br>
     * where Rz, Rx and Ry are rotation matrices around Z, X and Y axes in the world coordinate
     * reference frame respectively. The reference frame consists of three orthogonal axes X, Y, Z
     * where X points East, Y points north and Z points upwards perpendicular to the ground plane.
     * The computed angles alpha, beta and gamma are in radians and clockwise-positive when viewed
     * along the positive direction of the corresponding axis. Except for the special case when the
     * beta angle is +-pi/2 these angles uniquely define the orientation of a mobile device in 3D
     * space. The alpha-beta-gamma representation resembles the yaw-pitch-roll convention used in
     * vehicle dynamics, however it does not exactly match it. One of the differences is that the
     * 'pitch' angle beta is allowed to be within [-pi, pi). A mobile device with pitch angle
     * greater than pi/2 could correspond to a user lying down and looking upward at the screen.
     *
     * <p>
     * Upon return the array values is filled with the result,
     * <ul>
     * <li>values[0]: rotation around the Z axis, alpha in [0, 2*pi)</li>
     * <li>values[1]: rotation around the X axis, beta in [-pi, pi)</li>
     * <li>values[2]: rotation around the Y axis, gamma in [-pi/2, pi/2)</li>
     * </ul>
     * <p>
     *
     * @param matrixR a 3x3 rotation matrix {@see SensorManager.getRotationMatrix}.
     * @param values  an array of 3 doubles to hold the result.
     * @return the array values passed as argument.
     */
    private static double[] computeDeviceOrientationFromRotationMatrix(
            float[] matrixR, double[] values) {
        /*
         * 3x3 (length=9) case:
         *   /  R[ 0]   R[ 1]   R[ 2]  \
         *   |  R[ 3]   R[ 4]   R[ 5]  |
         *   \  R[ 6]   R[ 7]   R[ 8]  /
         *
         */
        if (matrixR.length != 9) return values;

        if (matrixR[8] > 0) {  // cos(beta) > 0
            values[0] = Math.atan2(-matrixR[1], matrixR[4]);
            values[1] = Math.asin(matrixR[7]);                 // beta (-pi/2, pi/2)
            values[2] = Math.atan2(-matrixR[6], matrixR[8]);   // gamma (-pi/2, pi/2)
        } else if (matrixR[8] < 0) {  // cos(beta) < 0
            values[0] = Math.atan2(matrixR[1], -matrixR[4]);
            values[1] = -Math.asin(matrixR[7]);
            values[1] += (values[1] >= 0) ? -Math.PI : Math.PI; // beta [-pi,-pi/2) U (pi/2,pi)
            values[2] = Math.atan2(matrixR[6], -matrixR[8]);    // gamma (-pi/2, pi/2)
        } else { // R[8] == 0
            if (matrixR[6] > 0) {  // cos(gamma) == 0, cos(beta) > 0
                values[0] = Math.atan2(-matrixR[1], matrixR[4]);
                values[1] = Math.asin(matrixR[7]);       // beta [-pi/2, pi/2]
                values[2] = -Math.PI / 2;                // gamma = -pi/2
            } else if (matrixR[6] < 0) { // cos(gamma) == 0, cos(beta) < 0
                values[0] = Math.atan2(matrixR[1], -matrixR[4]);
                values[1] = -Math.asin(matrixR[7]);
                values[1] += (values[1] >= 0) ? -Math.PI : Math.PI; // beta [-pi,-pi/2) U (pi/2,pi)
                values[2] = -Math.PI / 2;                           // gamma = -pi/2
            } else { // R[6] == 0, cos(beta) == 0
                // gimbal lock discontinuity
                values[0] = Math.atan2(matrixR[3], matrixR[0]);
                values[1] = (matrixR[7] > 0) ? Math.PI / 2 : -Math.PI / 2;  // beta = +-pi/2
                values[2] = 0;                                              // gamma = 0
            }
        }

        // alpha is in [-pi, pi], make sure it is in [0, 2*pi).
        if (values[0] < 0) {
            values[0] += 2 * Math.PI; // alpha [0, 2*pi)
        }

        return values;
    }

    /*
     * Converts a given rotation vector to its Euler angles representation. The angles
     * are in degrees.
     */
    private void convertRotationVectorToAngles(float[] rotationVector, double[] angles) {
        if (rotationVector.length > 4) {
            // On some Samsung devices SensorManager.getRotationMatrixFromVector
            // appears to throw an exception if rotation vector has length > 4.
            // For the purposes of this class the first 4 values of the
            // rotation vector are sufficient (see crbug.com/335298 for details).
            System.arraycopy(rotationVector, 0, mTruncatedRotationVector, 0, 4);
            SensorManager.getRotationMatrixFromVector(mDeviceRotationMatrix,
                    mTruncatedRotationVector);
        } else {
            SensorManager.getRotationMatrixFromVector(mDeviceRotationMatrix, rotationVector);
        }
        computeDeviceOrientationFromRotationMatrix(mDeviceRotationMatrix, angles);
        for (int i = 0; i < 3; i++) {
            angles[i] = Math.toDegrees(angles[i]);
        }
    }

    private void getOrientationFromGeomagneticVectors(float[] acceleration, float[] magnetic) {
        if (acceleration == null || magnetic == null) {
            return;
        }
        if (!SensorManager.getRotationMatrix(mDeviceRotationMatrix, null, acceleration, magnetic)) {
            return;
        }
        computeDeviceOrientationFromRotationMatrix(mDeviceRotationMatrix, mRotationAngles);

        gotOrientation(Math.toDegrees(mRotationAngles[0]),
                Math.toDegrees(mRotationAngles[1]),
                Math.toDegrees(mRotationAngles[2]));
    }

    /**
     * fetch SensorManager
     */
    private SensorManagerProxy getSensorManagerProxy() {
        if (mSensorManagerProxy != null) {
            return mSensorManagerProxy;
        }

        SensorManager sensorManager =
                (SensorManager) mAppContext.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            mSensorManagerProxy = new SensorManagerProxyImpl(sensorManager);
        }
        return mSensorManagerProxy;
    }


    private void setEventTypeActive(boolean active) {
        mDeviceOrientationIsActive = active;
        mDeviceOrientationIsActiveWithBackupSensors = active
                && (mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_C);
    }

    private void ensureRotationStructuresAllocated() {
        if (mDeviceRotationMatrix == null) {
            mDeviceRotationMatrix = new float[9];
        }
        if (mRotationAngles == null) {
            mRotationAngles = new double[3];
        }
        if (mTruncatedRotationVector == null) {
            mTruncatedRotationVector = new float[4];
        }
    }

    /**
     * @param sensorTypes         List of sensors to activate.
     * @param rateInMicroseconds  Intended delay (in microseconds) between sensor readings.
     * @param failOnMissingSensor If true the method returns true only if all sensors could be
     *                            activated. When false the method return true if at least one
     *                            sensor in sensorTypes could be activated.
     */
    private boolean registerSensors(Set<Integer> sensorTypes, int rateInMicroseconds,
                                    boolean failOnMissingSensor) {
        Set<Integer> sensorsToActivate = new HashSet<>(sensorTypes);
        sensorsToActivate.removeAll(mActiveSensors);
        if (sensorsToActivate.isEmpty()) return true;

        boolean success = false;
        for (Integer sensorType : sensorsToActivate) {
            boolean result = registerForSensorType(sensorType, rateInMicroseconds);
            if (!result && failOnMissingSensor) {
                // restore the previous state upon failure
                unregisterSensors(sensorsToActivate);
                return false;
            }
            if (result) {
                mActiveSensors.add(sensorType);
                success = true;
            }
        }
        return success;
    }

    private void unregisterSensors(Iterable<Integer> sensorTypes) {
        for (Integer sensorType : sensorTypes) {
            if (mActiveSensors.contains(sensorType)) {
                getSensorManagerProxy().unregisterListener(this, sensorType);
                mActiveSensors.remove(sensorType);
            }
        }
    }

    private boolean registerForSensorType(int type, int rateInMicroseconds) {
        SensorManagerProxy sensorManager = getSensorManagerProxy();
        if (sensorManager == null) {
            return false;
        }
        return sensorManager.registerListener(this, type, rateInMicroseconds, getHandler());
    }

    private void gotOrientation(double alpha, double beta, double gamma) {
        if (mListeners != null) {
            try {
                for (OnOrientationChangedListener listener : mListeners) {
                    listener.onOrientationChanged(alpha, beta, gamma);
                }
            } catch (Throwable e) {
                //ignore
                LogProxy.e("[OrientationDetector] ", e);
            }
        }
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mThread = new HandlerThread("DeviceOrientation");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }
        return mHandler;
    }


    //////-------------------Interfaces----------------------------

    interface OnOrientationChangedListener {
        void onOrientationChanged(double alpha, double beta, double gamma);
    }





}
