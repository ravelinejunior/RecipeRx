package br.com.raveline.reciperx.view.fragment.random

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.RandomFragmentBinding
import br.com.raveline.reciperx.viewmodel.NotificationViewModel

class RandomFragment : Fragment() {

    private var randomBinding:RandomFragmentBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        randomBinding = RandomFragmentBinding.inflate(inflater,container,false)
        return randomBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            if(activity is MainActivity){
                (activity as MainActivity).showBottomNavigationView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        randomBinding = null
    }

}