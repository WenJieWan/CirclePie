package com.wan.circlepie;

/**
 * Created by 万文杰 on 2017/2/18.
 */

public class CirclePieValue {
    private String text;
    private float number;
    private int color;
    private float angle;
    private float precent;

    public float getAngle() {
        return angle;
    }

    public void setRadian(float angle) {
        this.angle = angle;
    }

    public float getPrecent() {
        return precent;
    }

    public void setPrecent(float precent) {
        this.precent = precent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public CirclePieValue(String text, float number, int color) {
        this.color = color;
        this.text = text;
        this.number = number;
    }
}
