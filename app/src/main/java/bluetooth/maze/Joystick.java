package bluetooth.maze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View implements View.OnTouchListener {

    private float x;
    private float y;
    private int radius;
    public JoystickListener joystickListener;
    public int buffer = 60;
    public String colorJoystick = "#FF35FDBE";
    public String colorJoystickBG = "#FF3D3A3A";
    public boolean isClickable = true;
    private boolean isDragged;

    public void setGrey(){
        colorJoystick = "#8f8f8f";
    }

    public void setDefaultColor(){
        colorJoystick = "#FF35FDBE";
    }

    public void setCustom(String color){
        colorJoystick = color;
    }

    private boolean coordinatesReturnDefault = true;

    public Joystick(Context context) {
        super(context);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) joystickListener = (JoystickListener) context;
    }

    public Joystick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) joystickListener = (JoystickListener) context;
    }

    public Joystick(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) joystickListener = (JoystickListener) context;
    }

    public Joystick(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) joystickListener = (JoystickListener) context;
    }

    private boolean isInCircle(int x, int y, int radius){
        return x * x + y * y < radius * radius;
    }

    public boolean onTouch(View v, MotionEvent e){
        if(isClickable) {
            if (v.equals(this)) {
                if (e.getAction() != MotionEvent.ACTION_UP) {
                    if (isInCircle((int) e.getX() - getWidth() / 2, (int) e.getY() - getHeight() / 2, radius)) {
                        x = (int) e.getX();
                        y = (int) e.getY();
                        joystickListener.onJoystickMoved((int)(x-getWidth()/2)*buffer/radius,(int)(-(y-getHeight()/2)*buffer/radius));
                        invalidate();
                        isDragged = true;
                    } else {
                        if(isDragged){
                        float displacement = (float) Math.sqrt(Math.pow(e.getX() - getWidth() / 2, 2) + Math.pow(e.getY() - getHeight() / 2, 2));
                        x = (int) (getWidth() / 2 + (e.getX() - getWidth() / 2) * radius / displacement);
                        y = (int) (getHeight() / 2 + (e.getY() - getHeight() / 2) * radius / displacement);
                        joystickListener.onJoystickMoved((int)(x-getWidth()/2)*buffer/radius,(int)(-(y-getHeight()/2)*buffer/radius));
                        invalidate();}
                    }
                }
                else {
                    isDragged = false;
                    coordinatesReturnDefault = true;
                    joystickListener.onJoystickMoved(0, 0);
                    invalidate();
                }
            }
        }
        return true;
    }

    public void setPosition(float x, float y){
        //Log.d("CustomDebug",String.valueOf(x+getWidth()/2 +" "+ -y+getHeight()/2))

        x = (x*(radius/60))+getWidth()/2;
        y = (-y*(radius/60))+getHeight()/2;

        if(isInCircle((int)x/60,(int)y/60,radius)){
        this.x = x;
        this.y = y;
        invalidate();}

    }



    public interface JoystickListener{
        void onJoystickMoved(int x, int y);
    }

    @Override
    public void onDraw(Canvas canvas){

        if(coordinatesReturnDefault){
            x = getWidth()/2;
            y = getHeight()/2;
            coordinatesReturnDefault = false;
        }

        radius = getHeight()/3;

        super.onDraw(canvas);
        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setColor(Color.parseColor(colorJoystickBG));
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, border);

        Paint joystick = new Paint(Paint.ANTI_ALIAS_FLAG);
        joystick.setColor(Color.parseColor(colorJoystick));
        canvas.drawCircle(x,y,getHeight()/7, joystick);
    }
}
