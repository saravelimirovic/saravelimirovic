package com.example.myapplication.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentRegisterSecondBinding
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterFirstBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterSecond.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterSecond : Fragment(R.layout.fragment_register_second) {
    private lateinit var binding: FragmentRegisterSecondBinding
    val arraySpinner = arrayOf<String>("Kupac","Dostavljac", "Prodavac")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterSecondBinding.inflate(inflater, container, false)

        val spinner = binding.root.findViewById<Spinner>(R.id.spinner_uloga)
        spinner?.adapter = ArrayAdapter(requireActivity().applicationContext,  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arraySpinner) as SpinnerAdapter
        spinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                print("Nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                //Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}