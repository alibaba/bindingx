package com.alibaba.android.bindingx.core.internal;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
class Vector3 {

    double x,y,z;

    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vector3 applyQuaternion(Quaternion q) {
        double x = this.x, y = this.y, z = this.z;
        double qx = q.x, qy = q.y, qz = q.z, qw = q.w;

        // calculate quat * vector

        double ix = qw * x + qy * z - qz * y;
        double iy = qw * y + qz * x - qx * z;
        double iz = qw * z + qx * y - qy * x;
        double iw = -qx * x - qy * y - qz * z;

        // calculate result * inverse quat

        this.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
        this.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
        this.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;

        return this;
    }


}
