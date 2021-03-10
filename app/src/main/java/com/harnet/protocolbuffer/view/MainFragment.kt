package com.harnet.protocolbuffer.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.harnet.protocolbuffer.viewModel.MainViewModel
import com.harnet.protocolbuffer.R

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        saveChanges()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.mFirstName.observe(viewLifecycleOwner, { person ->
            view?.findViewById<TextView>(R.id.saved_text)?.text = person.firstName
        })
    }

    private fun saveChanges() {
        view?.findViewById<Button>(R.id.btn_save)?.setOnClickListener {
            val firstName = view?.findViewById<EditText>(R.id.user_input)?.text
            if (firstName != null) {
                if (firstName.isNotEmpty()) {
                    viewModel.updateFirstName(firstName.toString())
                }
            }
        }
    }

}