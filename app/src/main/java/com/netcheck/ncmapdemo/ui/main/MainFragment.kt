package com.netcheck.ncmapdemo.ui.main

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.netcheck.ncmapdemo.R

class MainFragment : Fragment() {
    private lateinit var logoAnim: AnimationDrawable
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.ivAnimateLogo)?.apply {
            setBackgroundResource(R.drawable.animation)
            logoAnim = background as AnimationDrawable
            logoAnim.start()
        }

        view.findViewById<ImageView>(R.id.ivPlanet).animate().alpha(1f).duration = 1500
        val anim = view.findViewById<ImageView>(R.id.ivBackground).animate().alpha(0.7f)
        anim.duration = 2000
        anim.withEndAction {
            view.findViewById<TextView>(R.id.tvNet).animate().alpha(1f).duration = 500
            val animLetter = view.findViewById<TextView>(R.id.tvHeck).animate().alpha(1f)
            animLetter.duration = 500
        }
    }

}