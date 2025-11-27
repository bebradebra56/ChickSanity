package com.chickisa.sofskil.zxcvb.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.chickisa.sofskil.MainActivity
import com.chickisa.sofskil.R
import com.chickisa.sofskil.databinding.FragmentLoadChickSanityBinding
import com.chickisa.sofskil.zxcvb.data.shar.ChickSanitySharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChickSanityLoadFragment : Fragment(R.layout.fragment_load_chick_sanity) {
    private lateinit var chickSanityLoadBinding: FragmentLoadChickSanityBinding

    private val chickSanityLoadViewModel by viewModel<ChickSanityLoadViewModel>()

    private val chickSanitySharedPreference by inject<ChickSanitySharedPreference>()

    private var chickSanityUrl = ""

    private val chickSanityRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            chickSanityNavigateToSuccess(chickSanityUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                chickSanitySharedPreference.chickSanityNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                chickSanityNavigateToSuccess(chickSanityUrl)
            } else {
                chickSanityNavigateToSuccess(chickSanityUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chickSanityLoadBinding = FragmentLoadChickSanityBinding.bind(view)

        chickSanityLoadBinding.chickSanityGrandButton.setOnClickListener {
            val chickSanityPermission = Manifest.permission.POST_NOTIFICATIONS
            chickSanityRequestNotificationPermission.launch(chickSanityPermission)
            chickSanitySharedPreference.chickSanityNotificationRequestedBefore = true
        }

        chickSanityLoadBinding.chickSanitySkipButton.setOnClickListener {
            chickSanitySharedPreference.chickSanityNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            chickSanityNavigateToSuccess(chickSanityUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chickSanityLoadViewModel.chickSanityHomeScreenState.collect {
                    when (it) {
                        is ChickSanityLoadViewModel.ChickSanityHomeScreenState.ChickSanityLoading -> {

                        }

                        is ChickSanityLoadViewModel.ChickSanityHomeScreenState.ChickSanityError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is ChickSanityLoadViewModel.ChickSanityHomeScreenState.ChickSanitySuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val chickSanityPermission = Manifest.permission.POST_NOTIFICATIONS
                                val chickSanityPermissionRequestedBefore = chickSanitySharedPreference.chickSanityNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), chickSanityPermission) == PackageManager.PERMISSION_GRANTED) {
                                    chickSanityNavigateToSuccess(it.data)
                                } else if (!chickSanityPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > chickSanitySharedPreference.chickSanityNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    chickSanityLoadBinding.chickSanityNotiGroup.visibility = View.VISIBLE
                                    chickSanityLoadBinding.chickSanityLoadingGroup.visibility = View.GONE
                                    chickSanityUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(chickSanityPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > chickSanitySharedPreference.chickSanityNotificationRequest) {
                                        chickSanityLoadBinding.chickSanityNotiGroup.visibility = View.VISIBLE
                                        chickSanityLoadBinding.chickSanityLoadingGroup.visibility = View.GONE
                                        chickSanityUrl = it.data
                                    } else {
                                        chickSanityNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    chickSanityNavigateToSuccess(it.data)
                                }
                            } else {
                                chickSanityNavigateToSuccess(it.data)
                            }
                        }

                        ChickSanityLoadViewModel.ChickSanityHomeScreenState.ChickSanityNotInternet -> {
                            chickSanityLoadBinding.chickSanityStateGroup.visibility = View.VISIBLE
                            chickSanityLoadBinding.chickSanityLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun chickSanityNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_chickSanityLoadFragment_to_chickSanityV,
            bundleOf(CHICK_SANITY_D to data)
        )
    }

    companion object {
        const val CHICK_SANITY_D = "chickSanityData"
    }
}