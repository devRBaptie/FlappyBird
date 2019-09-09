package me.reece.flappybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class GameView extends View {

    int bird_x, bird_y, birdSpeed;
    Paint level, score, bluePaint, blackPaint, goldPaint;
    Bitmap [] background= new Bitmap[6];
    Bitmap [] Life = new Bitmap[3];
    Bitmap [] Bird = new Bitmap[2];
    boolean touched = false;
    public int setScore;
    int blue_x ,  gold_x;
    int blue_y = 250;
    int gold_y = 500;
    int dot_speed = 10;
    int canvasWidth, canvasHeight;
    int lives = 3;
    int lev = 1;
    boolean gold_flg = true;
    int blackDots [] [] = new int [100][2];
    int backColor = 0;
    int count = 1;
    int life_x, life_y;
    boolean life_y_flg = true;
    boolean life_x_flg = true;
    boolean showLife = false;

    public GameView(Context context) {
        super(context);

        //___________________________BIT MAPS_________________________________//

        background[0]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackgroundred);
        background[1]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackground);
        background[2]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackgroundgreen);
        background[3]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackgroundblue);
        background[4]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackgroundorange);
        background[5]= BitmapFactory.decodeResource(getResources(),R.drawable.treebackgroundpurple);

        Life[0] = BitmapFactory.decodeResource(getResources(), R.drawable.smallalivetransparent);
        Life[1] = BitmapFactory.decodeResource(getResources(), R.drawable.deadtransparent);
        Life[2] = BitmapFactory.decodeResource(getResources(), R.drawable.smalldead);

        Bird[0] = BitmapFactory.decodeResource(getResources(), R.drawable.wingsuptransparent);
        Bird[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wingsdowntransparent);

        bird_x = 100;
        bird_y = 50;
        birdSpeed = 20;
        setScore = 0;

        level = new Paint();
        level.setColor(Color.RED);
        level.setTypeface(Typeface.DEFAULT);
        level.setTextSize(64);
        level.setAntiAlias(true);
        level.setTextAlign(Paint.Align.CENTER);

        score = new Paint();
        score.setColor(Color.BLACK);
        score.setTypeface(Typeface.DEFAULT_BOLD);
        score.setTextSize(64);
        score.setAntiAlias(true);

        bluePaint =  new Paint();
        bluePaint.setColor(Color.BLUE);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        goldPaint = new Paint();
        goldPaint.setColor(Color.RED);

        FirebaseApp.initializeApp(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();


        //_____________________SET UP UI_________________________________//

        //-----BACKGROUND COLORS-----//
        canvas.drawBitmap(background[backColor],0,0,null);

        canvas.drawText("score " + setScore, 20, 60, score);
        canvas.drawText("Level "+ lev , canvasWidth/2, 60, level);

        int minBird_Y = Bird[0].getHeight();
        int maxBird_Y = canvasHeight - Bird[0].getHeight() *3;

        //_____________________SET UP DOT_______________________//

        blue_x += dot_speed;

        //-----GOLD DOT-----//
        gold_x += dot_speed + 10;

        lev = (int) setScore/100;
        if(lev==0){
            lev=1;
        }
        if(count==lev){
            count++;
            if(backColor!=5){
                backColor++;
            }else{
                backColor=0;
            }
        }

        //-----MOVE GOLD DOT UP/DOWN------//
        if(gold_flg){
            gold_y=gold_y+25;
        }else {
            gold_y=gold_y-25;
        }

        //-----SET BOUNDS FOR GOLD DOT-----//
        if(gold_y < minBird_Y) gold_flg=true;
        if(gold_y > maxBird_Y) gold_flg=false;


        if(gold_x > canvasWidth) {

            gold_y = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
            gold_x = 0;

            //----RANDOM UP/DOWN-----//
            int a = (int)Math.floor(Math.random() * 2);
            switch (a){
                case 0:
                    gold_flg=true;
                    break;
                case 1:
                    gold_flg=false;
                    break;
            }
        }


        //-----BLUE DOT-----//
        if(blue_x > canvasWidth || blue_x == 0) {

            blue_y = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
            blue_x = 0;
        }

        //_____________________DRAW DOTS_________________________//

        //______________________BLACK DOT 2D ARRAY______________________//
        for (int i=1; i<=lev ;i++) {
            //-----BLACK DOT-----//
            if(blackDots[i][0] > canvasWidth || blackDots[i][1] == 0) {

                blackDots[i][1] = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
                blackDots[i][0] = 0;
            }

            if(isHit(blackDots[i][0], blackDots[i][1])) { // IS DOT HIT
                blackDots[i][0] = 0;


                //Check lives
                if(lives != 0){
                    lives--;
                    Toast.makeText(getContext(), " BOOM ", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                blackDots[i][0] += dot_speed + 5;
                canvas.drawCircle(canvasWidth - blackDots[i][0], blackDots[i][1], 50, blackPaint);
            }
        }

        // ------BLUE DOT-----//
         if(isHit(blue_x, blue_y)) { // IS DOT HIT
             blue_x = 0;
             setScore = setScore + 25;
         }
         else{
             canvas.drawCircle(canvasWidth - blue_x, blue_y, 25, bluePaint);
         }

        // -----GOLD DOT-----//
        if(isHit(gold_x, gold_y)) { // IS DOT HIT

            gold_x = 0;
            setScore = setScore + 100;
        }
        else{
            canvas.drawCircle(canvasWidth - gold_x, gold_y, 10, goldPaint);
        }

        //_____________________________CHANGE UNICORNS_________________________________//
        switch(lives){
            case 0:
                canvas.drawBitmap(Life[1], canvasWidth - 100, 0, null);
                canvas.drawBitmap(Life[1], canvasWidth - 200, 0, null);
                canvas.drawBitmap(Life[1], canvasWidth - 300, 0, null);

                //------GAME OVER-----//
                Toast.makeText(getContext(), " GAME OVER ", Toast.LENGTH_SHORT).show();

                //------------FIREBASE-----------//

                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference();

                    Score score = new Score();
                    String s = String.valueOf(setScore);
                    score.setScore(setScore);

                    reference.child("Score").setValue(s);

                }catch (Exception e){

                }
                //-----------CHANGE SCREEN----------------------//
                final Intent i = new Intent(getContext(), GameOver.class);
                getContext().startActivity(i);


                //----RESET VARIABLES----//
                reset();
                break;
            case 1:
                canvas.drawBitmap(Life[1], canvasWidth - 100, 0, null);
                canvas.drawBitmap(Life[1], canvasWidth - 200, 0, null);
                canvas.drawBitmap(Life[0], canvasWidth - 300, 0, null);
                break;
            case 2:
                canvas.drawBitmap(Life[1], canvasWidth - 100, 0, null);
                canvas.drawBitmap(Life[0], canvasWidth - 200, 0, null);
                canvas.drawBitmap(Life[0], canvasWidth - 300, 0, null);
                break;
            case 3:
                canvas.drawBitmap(Life[0], canvasWidth - 100, 0, null);
                canvas.drawBitmap(Life[0], canvasWidth - 200, 0, null);
                canvas.drawBitmap(Life[0], canvasWidth - 300, 0, null);
                break;
            default:
                break;
        }


        /* TODO: Extra life*/
        if(lives!=3){
            int i = (int) Math.floor(Math.random()*10);

            if(i == 1){
                showLife = true;
            }
        }
        if(showLife) {
            if (isHit(life_x, life_y)) {
                lives++;
                showLife = false;
            } else {
                canvas.drawBitmap(Life[0], life_x, life_y, null);
            }
        }


        if(life_y_flg){
            life_y=life_y+10;
        }else {
            life_y=life_y-10;
        }

        if(life_y < minBird_Y) life_y_flg=true;
        if(life_y > maxBird_Y) life_y_flg=false;

        if(life_x_flg){
            life_x=life_x+10;
        }else {
            life_x=life_x-10;
        }

        if(life_x < 0) life_x_flg=true;
        if(life_x > canvasWidth) life_x_flg=false;


        //_____________________KEEP BIRD IN BOUNDS_______________________//

        bird_y += birdSpeed;

        if(bird_y < minBird_Y) bird_y = minBird_Y;
        if(bird_y > maxBird_Y) bird_y = maxBird_Y;

        if(life_y < minBird_Y) life_y = minBird_Y;
        if(life_y > maxBird_Y) life_y = maxBird_Y;
        if(life_x < 0) life_x = 0;
        if(life_x > canvasHeight) life_x = canvasHeight;

        // SPEED UP BIRD WHILE FALLING
        birdSpeed += 2;

        //____________________SEND THE BIRD UP ON TOUCH_____________________//

        if(touched){
            canvas.drawBitmap(Bird[0], bird_x, bird_y,null);
            touched = false;
        }else{
            canvas.drawBitmap(Bird[1], bird_x, bird_y,null);
        }
    }


    public boolean isHit(int x, int y){// _____________________DOT COLLISION_____________________//

        if((bird_x <  canvasWidth - x && canvasWidth - x < (bird_x + Bird[0].getWidth())) && (bird_y < y && y < (bird_y + Bird[0].getHeight()))){

            return true;
        }else{

            return false;
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:  // FINGER ON SCREEN

                touched = true;
                birdSpeed = -20;
                break;
        }
        return true;
    }

    public void reset(){
        lives=3;
        setScore = 0;
        lev = 1;
        backColor=0;
        count = 1;
        blue_x=0;
        gold_x=0;

        /*
        blue_x = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
        gold_y = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
        blackDots[1][0] = (int) Math.floor(Math.random() * (maxBird_Y - minBird_Y) + minBird_Y);
         */
        blackDots[0][1] = 0;
    }



}


/* TODO:
 * FIREBASE
 * ADD ADVERT
 * STOP GAME
 * GAME RETRY
 */
