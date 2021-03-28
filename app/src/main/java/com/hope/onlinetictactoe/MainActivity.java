package com.hope.onlinetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// content view for this piece of code

        final TextView animateTextView = findViewById(R.id.homeText);
        // construct the value animator and define the range
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1000f);
        //repeats the animation 100 times
        valueAnimator.setRepeatCount(100);
        // increase the speed first and then decrease
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // animate over the course of 10000 milliseconds
        valueAnimator.setDuration(10000);
        // define how to update the view at each "step" of the animation
        valueAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            animateTextView.setRotationX(progress);

        });
        valueAnimator.start();

        Button PlayButton = findViewById(R.id.buPlay);
        PlayButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MainActivity_game.class)); // start the game activity when play is pressed
        });

        Button AboutButton = findViewById(R.id.buAbout);
        AboutButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MainActivity_about.class));// goes to the about page when about is pressed
        });
    }
}

