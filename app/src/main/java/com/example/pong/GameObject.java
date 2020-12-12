package com.example.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GameObject {

    protected double xPos, yPos;
    protected int mWidth, mHeight;
    protected int mColor;

    GameObject(int color, int width, int height) {
        mColor = color;
        mWidth = width;
        mHeight = height;
    }

    void setColor(int color) {
        mColor = color;
    }

    void setHeight(int height) {
        mHeight = height;
    }
    int getHeight() { return mHeight; }

    void setWidth(int width) {
        mWidth = width;
    }
    int getWidth() { return  mWidth; }

    void setXPos(double x) {
        xPos = x;
    }
    public double getXPos() { return  xPos; }

    void setYPos(double y) { yPos = y; }
    public double getYPos() { return  yPos; }


    void draw(Canvas canvas, Paint paint) {
        paint.setColor(mColor);
        canvas.drawRect(
                (int) xPos - mWidth / 2,
                (int) yPos + mHeight / 2,
                (int) xPos + mWidth / 2,
                (int) yPos - mHeight / 2,
                paint
        );
    }
}
