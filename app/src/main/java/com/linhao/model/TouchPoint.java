package com.linhao.model;

/**
 * Created by haoshenglin on 2018/3/16.
 */
public class TouchPoint {

    float pointX;
    float pointY;

    public TouchPoint(float pointX, float pointY) {
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public float getPointX() {
        return pointX;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }
}
