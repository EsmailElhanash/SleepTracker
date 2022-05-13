package com.example.sleeptracker.utils.androidutils

import android.content.Context
import android.text.TextUtils
import com.example.sleeptracker.R
import com.example.sleeptracker.databinding.ActivitySignUpBinding

object InputValidator {
//    private const val PASSWORD_REGEX : String = """^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@${'$'} %^&*-]).{8,}${'$'}"""

    private fun isNameValid(name:String) : Boolean {
        return name.isNotEmpty() && name.isNotBlank()
    }

    private fun isEmailValid(email:String) : Boolean {
        return if (TextUtils.isEmpty(email)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    private fun isPasswordValid(password:String) : Boolean {
        return password.length>=7
//        return Regex(PASSWORD_REGEX).matches(password)
    }

    private fun doesPasswordMatch(pw:String , pw2: String) : Boolean {
        return pw == pw2
    }

    private fun isAgeValid(age:String) : Boolean {
        return if (age.toIntOrNull() != null) age.toInt()>0
        else false
    }

    private fun isGenderSelected(gender:Int) : Boolean {
        return gender == 1 || gender == 2
    }

//    private fun areTermsAccepted(termsAccepted:Boolean) : Boolean {
//        return termsAccepted
//    }


    fun checkInputs(context: Context ,binding: ActivitySignUpBinding) : String {
        if(!isNameValid(binding.nameInput.editText?.text.toString())){
            return context.getString(R.string.invalid_entered_name)
        }

        if(!isEmailValid(binding.emailInput.editText?.text.toString())){
            return context.getString(R.string.invalid_entered_email)
        }

        if(!isPasswordValid(binding.passwordInput.editText?.text.toString())){
            return context.getString(R.string.invalid_entered_password)
        }

        if(!doesPasswordMatch(binding.passwordInput.editText?.text.toString(),binding.passwordConfirmInput.editText?.text.toString())){
            return context.getString(R.string.password_not_match)
        }

        if(!isAgeValid(binding.ageInput.editText?.text.toString())){
            return context.getString(R.string.invalid_age)
        }

        if(!isGenderSelected(binding.genderPicker.selectedItemPosition)){
            return context.getString(R.string.choose_gender)
        }



        if (binding.ethnicGroups.ethnicRadioGroup.checkedRadioButtonId == -1)
            return context.getString(R.string.please_choose_ethnic_group)
        return ""
    }

    fun checkPassword(context:Context , password:String , confirm:String):String{
        if(!isPasswordValid(password)){
            return context.getString(R.string.invalid_entered_password)
        }

        if(!doesPasswordMatch(password,confirm)){
            return context.getString(R.string.password_not_match)
        }
        return ""
    }
}