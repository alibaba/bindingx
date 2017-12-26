package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.Nullable;

/**
 * Description:
 *
 *  四元数
 *
 * Created by rowandjj(chuyi)<br/>
 */

class Quaternion {

    double x,y,z;
    double w;

    Quaternion() {
    }

    Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * 欧拉角转四元数
     * */
    @Nullable
    Quaternion setFromEuler(Euler euler) {
        if(euler == null || !euler.isEuler) {
            return null;
        }

        double c1 = Math.cos(euler.x / 2);
        double c2 = Math.cos(euler.y / 2);
        double c3 = Math.cos(euler.z / 2);
        double s1 = Math.sin(euler.x / 2);
        double s2 = Math.sin(euler.y / 2);
        double s3 = Math.sin(euler.z / 2);

        String order = euler.order;

        if ("XYZ".equals(order)) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if ("YXZ".equals(order)) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        } else if ("ZXY".equals(order)) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if ("ZYX".equals(order)) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        } else if ("YZX".equals(order)) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if ("XZY".equals(order)) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        }

        return this;
    }

    /**
     * 轴角转四元数
     * */
    Quaternion setFromAxisAngle(Vector3 axis, double angle) {
        double halfAngle = angle / 2;
        double s = Math.sin(halfAngle);

        this.x = axis.x * s;
        this.y = axis.y * s;
        this.z = axis.z * s;
        this.w = Math.cos(halfAngle);
        return this;
    }


    Quaternion multiply(Quaternion q) {
        return this.multiplyQuaternions(this, q);
    }

    Quaternion multiplyQuaternions(Quaternion a,Quaternion b) {
        // from http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm
        double qax = a.x, qay = a.y, qaz = a.z, qaw = a.w;
        double qbx = b.x, qby = b.y, qbz = b.z, qbw = b.w;

        this.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
        this.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
        this.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
        this.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;
        return this;
    }

    @Override
    public String toString() {
        return "Quaternion{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}
