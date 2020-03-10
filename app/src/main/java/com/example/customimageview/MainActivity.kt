package com.example.customimageview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AnimationUtils.loadAnimation(this, R.anim.custom_anim).also { hyperspaceJumpAnimation ->
            findViewById<ImageView>(R.id.imageViewTest1).startAnimation(hyperspaceJumpAnimation)
        }
    }
}
