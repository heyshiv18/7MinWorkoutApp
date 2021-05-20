package com.example.a7minworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_excercise.*
import kotlinx.android.synthetic.main.dialogue_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer:CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer:CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseList:ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var tts:TextToSpeech? = null
    private var player:MediaPlayer? = null
    private var exerciseAdapter:ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setSupportActionBar(toolbar_exercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_exercise_activity.setNavigationOnClickListener{
            customDialogueForBackButton()
        }
        tts = TextToSpeech(this, this)
        exerciseList = Constants.defaultExerciseList()

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

//    RECYCLER-VIEW FUNCTION ->
    private fun setupExerciseStatusRecyclerView(){
        rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)
        rvExerciseStatus.adapter = exerciseAdapter
    }
//    REST ->
    private fun setRestProgressBar(){
        progressBar.progress = restProgress
        restTimer = object:CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 3-restProgress
                tvTimer.text = (3-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }
    private fun setupRestView(){
        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress = 0
        }
        tvUpcomingExerciseName.text = exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()
        player = MediaPlayer.create(applicationContext, R.raw.press_start)
        player!!.isLooping = false
        player!!.start()
    }

//    EXERCISE ->
    private fun setupExerciseView(){
        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()

    speakOut(exerciseList!![currentExercisePosition].getName())

    setExerciseProgressBar()
    }
    private fun setExerciseProgressBar(){
        progressBarExercise.progress = exerciseProgress
        exerciseTimer = object:CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress = 5-exerciseProgress
                tvExerciseTimer.text = (5-exerciseProgress).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false) // exercise is completed so selection is set to false
                exerciseList!![currentExercisePosition].setIsCompleted(true) // updating in the list that this exercise is completed
                exerciseAdapter!!.notifyDataSetChanged()
                if(currentExercisePosition < 11){
                    setupRestView()
                }else {
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

//    FOR BOTH REST & EXERCISE ->
    public override fun onDestroy(){
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress = 0
        }
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        if (tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

//    Text To Speech ->
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result =tts!!.setLanguage(Locale.US)
        }
    }

    private fun speakOut(text:String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

//    CUSTOM BACK DIALOGUE BOX ->
    private fun customDialogueForBackButton() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialogue_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener{
            finish()
            customDialog.dismiss()
        }
    customDialog.tvNo.setOnClickListener {customDialog.dismiss()}
    customDialog.show()
    }
}