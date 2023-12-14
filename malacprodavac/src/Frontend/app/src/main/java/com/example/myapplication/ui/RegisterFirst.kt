package com.example.myapplication.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentRegisterFirstBinding
import com.example.myapplication.R

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFirst.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFirst : Fragment(R.layout.fragment_register_first) {
    private lateinit var binding: FragmentRegisterFirstBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterFirstBinding.inflate(inflater, container, false)

        binding.toggleButton.setOnClickListener{
            var inputType = binding.registrationPassword.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.registrationPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.registrationPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.registrationPassword.setSelection(binding.registrationPassword.text.length)
        }

        return binding.root
    }
}