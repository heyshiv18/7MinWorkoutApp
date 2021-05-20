package com.example.a7minworkout

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_b_m_i.*
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }
    private var currentVisibleView:String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_m_i)

        setSupportActionBar(toolbar_bmi_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculator BMI"
        toolbar_bmi_activity.setNavigationOnClickListener {
            onBackPressed() }

        makeVisibleUsUnitsView()
//        CHANGING BETWEEN RADIO-BUTTONS (US View & METRIC View) ->
        rgUnits.setOnCheckedChangeListener { radioGroup: RadioGroup, checkedId:Int ->
            if(checkedId == R.id.rbMetricUnits)
                makeVisibleMetricUnitsView()
            else
                makeVisibleUsUnitsView()
        }
        btnCalculateUnits.setOnClickListener {
            if(currentVisibleView == METRIC_UNITS_VIEW){
                if(validateMetricUnits()){
                    val heightValue:Float = etMetricUnitHeight.text.toString().toFloat()/100
                    val weightValue:Float = etMetricUnitWeight.text.toString().toFloat()
                    val bmi = weightValue/(heightValue*heightValue)
                    displayBMIResult(bmi)
                }else{
                    Toast.makeText(this@BMIActivity, "Please Enter Valid Values", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(validateUsUnits()){
                    val usUnitHeightValueFeet:String = etUsUnitHeightFeet.text.toString()
                    val usUnitWeightValue:Float = etMetricUnitWeight.text.toString().toFloat()
                    val usUnitHeightValueInch:String = etUsUnitHeightInch.text.toString()
                    val heightValue:Float = usUnitHeightValueFeet.toFloat()+usUnitHeightValueInch.toFloat() * 12
                    val bmi = 703*(usUnitWeightValue/(heightValue*heightValue))
                    displayBMIResult(bmi)
                }else{
                    Toast.makeText(this@BMIActivity, "Please Enter Valid Values", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//FUNCTIONS FOR CHANGING BETWEEN RADIO-BUTTONS (US View & METRIC View) ->
    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        llMetricUnitsView.visibility = View.VISIBLE
        llUsUnitsView.visibility = View.GONE
        etMetricUnitHeight.text!!.clear()
        etMetricUnitWeight.text!!.clear()
    }

    private fun makeVisibleUsUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        llUsUnitsView.visibility = View.VISIBLE
        llMetricUnitsView.visibility = View.GONE
        etUsUnitHeightFeet.text!!.clear()
        etUsUnitHeightInch.text!!.clear()
        etUsUnitWeight.text!!.clear()
    }

    private fun validateMetricUnits():Boolean{
        var isValid = true
        if(etMetricUnitHeight.text.toString().isEmpty()) isValid = false
        else if(etMetricUnitWeight.text.toString().isEmpty()) isValid = false
        return isValid
    }

    private fun validateUsUnits():Boolean{
        var isValid = true
        if(etUsUnitHeightFeet.text.toString().isEmpty()) isValid = false
        else if(etUsUnitHeightInch.text.toString().isEmpty()) isValid = false
        else if(etUsUnitWeight.text.toString().isEmpty()) isValid = false
        return isValid
    }
//    ---END---

    private fun displayBMIResult(bmi:Float){
        val bmiLabel:String
        val bmiDescription:String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        tvYourBMI.visibility = View.VISIBLE
        tvBMIDescription.visibility = View.VISIBLE
        tvBMIType.visibility = View.VISIBLE
        tvBMIValue.visibility = View.VISIBLE
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        tvBMIValue.text = bmiValue
        tvBMIDescription.text = bmiDescription
        tvBMIType.text = bmiLabel
    }
}