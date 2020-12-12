package com.example.pong;

import android.opengl.Matrix;

public class Ball extends GameObject {

    double acceleration = 1000;
    double velocity = 1000;
    double[] direction = new double[2];     // normal vector of length 1

    Ball(int color, int width, int height) {
        super(color, width, height);
    }

    public void calculatePostion(double deltaT){
        double dtSeconds = deltaT / Math.pow(10, 3);
        velocity = velocity + acceleration * dtSeconds * dtSeconds;
        xPos = (xPos + velocity * direction[0] * dtSeconds);
        yPos = (yPos + velocity * direction[1] * dtSeconds);
    }

    public double[] getDirection() {
        return direction;
    }

    public void setDirection(double d[]) {
        direction = d;
    }

    public void setVelocity(double v) {
        velocity = v;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

}
