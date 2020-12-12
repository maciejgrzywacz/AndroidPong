package com.example.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;


public class GameView extends View {

    // GAME VALUES
    private final int PADDLE_HEIGHT_SCREEN_PERCENT = 30;
    private final int PADDLE_WIDTH = 50;
    private final int PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET = 150;
    private final int BALL_SIZE = 50;
    private final int BALL_START_VELOCITY = 1000;
    private final int BALL_ACCELERATION = 5000;

    private final int TEXT_SCREEN_OFFSET = 100;
    private final int TEXT_SIZE = 50;


    public Paddle paddleLeft, paddleRight;
    public Ball ball;

    private Paint mPaint;
    private int mWidth, mHeight;
    private long timeOfLastDraw;
    private int points[] = {0,0};

    public GameView(Context context) {
        super(context);

        final GameView thisView = this;
        ViewTreeObserver viewTreeObserver = thisView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            //add a listener that will fire only once when layout is set, to get width and height and setup game based on this values
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    thisView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mWidth = thisView.getWidth();
                    mHeight = thisView.getHeight();

                    thisView.setupGame();
                }
            });
        }

        mPaint = new Paint();
    }

    public void setupGame() {
        int paddleHeight = mHeight * PADDLE_HEIGHT_SCREEN_PERCENT / 100;

        paddleLeft = new Paddle(
                Color.WHITE,
                PADDLE_WIDTH,
                paddleHeight);
        paddleLeft.setXPos(PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET);
        paddleLeft.setYPos(mHeight / 2);

        paddleRight = new Paddle(
                Color.RED,
                PADDLE_WIDTH,
                paddleHeight);
        paddleRight.setXPos(mWidth - PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET);
        paddleRight.setYPos(mHeight / 2);

        ball = new Ball(
                Color.WHITE,
                BALL_SIZE,
                BALL_SIZE);

        resetGame();

        timeOfLastDraw = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBG(canvas);

        // update ball position
        long timeNow = System.currentTimeMillis();
        long dt = timeNow -  timeOfLastDraw;
        timeOfLastDraw = timeNow;
        ball.calculatePostion(dt);
        // check for object collisions
        calculateBallCollisions();

        // draw all game objects
        ball.draw(canvas, mPaint);
        paddleLeft.draw(canvas, mPaint);
        paddleRight.draw(canvas, mPaint);

        // invalidate view after every draw so this behaves basically as game loop
        this.invalidate();
    }

    private void calculateBallCollisions() {
        if (ball.direction[0] > 0) {
            // ball is moving right
            // check collision with right paddle
            if (ball.getXPos() + ball.getWidth() / 2 > mWidth - PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET - paddleRight.getWidth() / 2) {
                if (ball.getYPos() + ball.getHeight() / 2 > paddleRight.getYPos() - paddleRight.getHeight() / 2 &&
                        ball.getYPos() - ball.getHeight() / 2 < paddleRight.getYPos() + paddleRight.getHeight() / 2 &&
                        ball.getXPos() < mWidth - PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET) {
                    handleBallPaddleCollision(paddleRight);
                }
            }
            // check collision with right screen bound
            if (ball.getXPos() + ball.getWidth() / 2 > mWidth) {
                resetGame();
                points[0]++;
            }
        }
        else {
            // ball is moving left
            // check collision with left paddle
            if (ball.getXPos() - ball.getWidth() / 2 < PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET + paddleLeft.getWidth() / 2) {
                if (ball.getYPos() + ball.getHeight() / 2 > paddleLeft.getYPos() - paddleLeft.getHeight() / 2 &&
                        ball.getYPos() - ball.getHeight() / 2 < paddleLeft.getYPos() + paddleLeft.getHeight() / 2 &&
                        ball.getXPos() > PADDLE_TO_SCREEN_BORDER_HORIZONTAL_OFFSET) {
                    handleBallPaddleCollision(paddleLeft);
                }
            }

            // check collision with left screen bound
            if (ball.getXPos() - ball.getWidth() / 2 < 0) {
                resetGame();
                points[1]++;
            }
        }

        if (ball.direction[1] != 0) {
            // ball is moving up
            // check collision with upper screen bound
            if ((ball.getYPos() + ball.getHeight() / 2 > mHeight)) {
                // revert vertical ball direction
                double ballDirection[] = ball.getDirection();
                ballDirection[1] = -ballDirection[1];
                ball.setDirection(ballDirection);

                ball.setYPos(mHeight - ball.getHeight() / 2);
            }
            else if (ball.getYPos() - ball.getHeight() / 2 < 0) {
                // revert vertical ball direction
                double ballDirection[] = ball.getDirection();
                ballDirection[1] = -ballDirection[1];
                ball.setDirection(ballDirection);

                ball.setYPos(ball.getHeight() / 2);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // handle touches
        // loop on pointers to support multiple concurrent touch events
        for (int i = 0; i < event.getPointerCount(); i++) {
            int activePointer = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(activePointer);

            int x = (int) event.getX(pointerIndex);
            int y = (int) event.getY(pointerIndex);

            // if screen touched on left side - move left paddle, if right - right one
            if (x < mWidth / 2) {
                setGameObjectPositionInBounds(paddleLeft, paddleLeft.getXPos(), y);
            }
            else {
                setGameObjectPositionInBounds(paddleRight, paddleRight.getXPos(), y);
            }
        }

        // return true to handle concurrent touch events
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetGame() {
        ball.setXPos(mWidth / 2);
        ball.setYPos(mHeight / 2);

        double randomizedAngle = java.util.concurrent.ThreadLocalRandom.current().nextDouble(0.0, 2 * Math.PI + 1.0);
        ball.setDirection(new double[]{Math.cos(randomizedAngle), Math.sin(randomizedAngle)});
        ball.setVelocity(BALL_START_VELOCITY);
        ball.setAcceleration(BALL_ACCELERATION);

//        paddleLeft.setYPos(mHeight / 2);
//        paddleRight.setYPos(mHeight / 2);
    }

    private void handleBallPaddleCollision(Paddle paddle){
        double N[] = new double[2];
        N[1] = (ball.getYPos() - paddle.getYPos()) / (paddle.getHeight() / 2) * 0.7;

        double secondPartValue = Math.sqrt(1 - N[1]*N[1]);
        if (paddle == paddleRight) N[0] = -secondPartValue;
        else N[0] = secondPartValue;

        ball.setDirection(N);
    }

    private void drawBG(Canvas canvas) {
        // draw black background
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, mHeight, mWidth, 0, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(100);
        canvas.drawText(String.valueOf(points[0]), TEXT_SCREEN_OFFSET, TEXT_SCREEN_OFFSET, mPaint);
        canvas.drawText(String.valueOf(points[1]), mWidth - TEXT_SCREEN_OFFSET - TEXT_SIZE, TEXT_SCREEN_OFFSET, mPaint);

        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mPaint);
    }

    private void setGameObjectPositionInBounds(GameObject gm, double x, double y) {
        // change GameObject position with checking screen bounds
        int objectCenterY = gm.getHeight() / 2 ;

        if (y < objectCenterY) {
            gm.setYPos(objectCenterY);
        }
        else if (y > mHeight - objectCenterY) {
            gm.setYPos(mHeight - objectCenterY);
        }
        else {
            gm.setYPos(y);
        }
    }
}
