package com.hope.onlinetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity_about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_about);//set content view for this piece of code

        final ImageView animateImageView = findViewById(R.id.imgHope);
        // construct the value animator and define the range
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 500f);
        //repeats the animation 100 times
        valueAnimator.setRepeatCount(100);
        // increase the speed first and then decrease
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // animate over the course of 10000 milliseconds
        valueAnimator.setDuration(10000);
        // define how to update the view at each "step" of the animation
        valueAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            animateImageView.setRotationY(progress);

        });
        valueAnimator.start();

        Button HomeButton = findViewById(R.id.buHome);//assign button to buHome
        HomeButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity_about.this, MainActivity.class));// when home button pressed return to main activity
        });
    }
}