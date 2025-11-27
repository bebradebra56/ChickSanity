package com.chickisa.sofskil.zxcvb.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import com.chickisa.sofskil.zxcvb.presentation.ui.load.ChickSanityLoadFragment
import org.koin.android.ext.android.inject

class ChickSanityV : Fragment(){

    private lateinit var chickSanityPhoto: Uri
    private var chickSanityFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val chickSanityTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        chickSanityFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        chickSanityFilePathFromChrome = null
    }

    private val chickSanityTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            chickSanityFilePathFromChrome?.onReceiveValue(arrayOf(chickSanityPhoto))
            chickSanityFilePathFromChrome = null
        } else {
            chickSanityFilePathFromChrome?.onReceiveValue(null)
            chickSanityFilePathFromChrome = null
        }
    }

    private val chickSanityDataStore by activityViewModels<ChickSanityDataStore>()


    private val chickSanityViFun by inject<ChickSanityViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (chickSanityDataStore.chickSanityView.canGoBack()) {
                        chickSanityDataStore.chickSanityView.goBack()
                        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "WebView can go back")
                    } else if (chickSanityDataStore.chickSanityViList.size > 1) {
                        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "WebView can`t go back")
                        chickSanityDataStore.chickSanityViList.removeAt(chickSanityDataStore.chickSanityViList.lastIndex)
                        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "WebView list size ${chickSanityDataStore.chickSanityViList.size}")
                        chickSanityDataStore.chickSanityView.destroy()
                        val previousWebView = chickSanityDataStore.chickSanityViList.last()
                        chickSanityAttachWebViewToContainer(previousWebView)
                        chickSanityDataStore.chickSanityView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (chickSanityDataStore.chickSanityIsFirstCreate) {
            chickSanityDataStore.chickSanityIsFirstCreate = false
            chickSanityDataStore.chickSanityContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return chickSanityDataStore.chickSanityContainerView
        } else {
            return chickSanityDataStore.chickSanityContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "onViewCreated")
        if (chickSanityDataStore.chickSanityViList.isEmpty()) {
            chickSanityDataStore.chickSanityView = ChickSanityVi(requireContext(), object :
                ChickSanityCallBack {
                override fun chickSanityHandleCreateWebWindowRequest(chickSanityVi: ChickSanityVi) {
                    chickSanityDataStore.chickSanityViList.add(chickSanityVi)
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "WebView list size = ${chickSanityDataStore.chickSanityViList.size}")
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "CreateWebWindowRequest")
                    chickSanityDataStore.chickSanityView = chickSanityVi
                    chickSanityVi.chickSanitySetFileChooserHandler { callback ->
                        chickSanityHandleFileChooser(callback)
                    }
                    chickSanityAttachWebViewToContainer(chickSanityVi)
                }

            }, chickSanityWindow = requireActivity().window).apply {
                chickSanitySetFileChooserHandler { callback ->
                    chickSanityHandleFileChooser(callback)
                }
            }
            chickSanityDataStore.chickSanityView.chickSanityFLoad(arguments?.getString(
                ChickSanityLoadFragment.CHICK_SANITY_D) ?: "")
//            ejvview.fLoad("www.google.com")
            chickSanityDataStore.chickSanityViList.add(chickSanityDataStore.chickSanityView)
            chickSanityAttachWebViewToContainer(chickSanityDataStore.chickSanityView)
        } else {
            chickSanityDataStore.chickSanityViList.forEach { webView ->
                webView.chickSanitySetFileChooserHandler { callback ->
                    chickSanityHandleFileChooser(callback)
                }
            }
            chickSanityDataStore.chickSanityView = chickSanityDataStore.chickSanityViList.last()

            chickSanityAttachWebViewToContainer(chickSanityDataStore.chickSanityView)
        }
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "WebView list size = ${chickSanityDataStore.chickSanityViList.size}")
    }

    private fun chickSanityHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        chickSanityFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Launching file picker")
                    chickSanityTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Launching camera")
                    chickSanityPhoto = chickSanityViFun.chickSanitySavePhoto()
                    chickSanityTakePhoto.launch(chickSanityPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                chickSanityFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun chickSanityAttachWebViewToContainer(w: ChickSanityVi) {
        chickSanityDataStore.chickSanityContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            chickSanityDataStore.chickSanityContainerView.removeAllViews()
            chickSanityDataStore.chickSanityContainerView.addView(w)
        }
    }


}