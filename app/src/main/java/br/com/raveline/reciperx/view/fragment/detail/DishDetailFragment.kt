package br.com.raveline.reciperx.view.fragment.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.DishDetailFragmentBinding
import br.com.raveline.reciperx.viewmodel.DishDetailViewModel

class DishDetailFragment : Fragment() {

    private lateinit var viewModel: DishDetailViewModel
    private lateinit var dBinding:DishDetailFragmentBinding
    private val args: DishDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dBinding = DishDetailFragmentBinding.inflate(inflater,container,false)

        return dBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            args.let {
                Toast.makeText(context, it.dish.title, Toast.LENGTH_SHORT).show()
            }
        }
    }


}