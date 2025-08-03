package com.walton.productionlinedisplay.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.walton.productionlinedisplay.R
import com.walton.productionlinedisplay.adapter.ListAdapter
import com.walton.productionlinedisplay.databinding.FragmentMainBinding
import com.walton.productionlinedisplay.model.ResponseModel
import com.walton.productionlinedisplay.utils.AlertDialogStatus
import com.walton.productionlinedisplay.utils.ConnectivityStatus
import com.walton.productionlinedisplay.utils.Constant.TAG
import com.walton.productionlinedisplay.utils.NetworkResult
import com.walton.productionlinedisplay.viewmodel.ApiResponseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.HashMap

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var isFirstTime: Boolean = true
    private var noDataFound: Boolean = true
    private lateinit var runnableData: Runnable
    private var isInternetAvailable: Boolean = false
    private var isRunning: Boolean = false
    private lateinit var runnable: Runnable
    private val binding get() = _binding!!
    private var _binding: FragmentMainBinding? = null
    private lateinit var connectivityStatus: ConnectivityStatus

    private var qc_passed: Int? = null
    private var wip_stock: Int? = null
    private var wbst_received: Int? = null
    private var finalQcApproval: Int? = null

    private var productionLine: Int? = null
    private var shiftIndex: Int? = null
    private var paramMap: HashMap<String, String> = HashMap()
    private var finalQcApprovalParamMap: HashMap<String, String> = HashMap()
    private val apiResponseViewModel: ApiResponseViewModel by viewModels()

    private fun getParams(): HashMap<String, String> {
        paramMap["prod_line"] = "tv_prodline_${productionLine.toString()}"
        paramMap["shift"] = "shift${shiftIndex.toString()}"
        paramMap["date"] = apiResponseViewModel.getCurrentDate(false)
        Log.d(TAG, "getParams: ${apiResponseViewModel.getCurrentDate(false)}")
        return paramMap
    }

    private fun getFinalQcApprovalParams(): HashMap<String, String> {
        finalQcApprovalParamMap["report_id"] = "427"
        finalQcApprovalParamMap["token"] = "TV-TOKEN-953274"
        return finalQcApprovalParamMap
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.txtDate.text = apiResponseViewModel.getCurrentDate(true)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectivityStatus = ConnectivityStatus(binding.root.context)

        // Production line spinner handler
        spinnerHandler()

        bindObserver()
    }

    private fun spinnerHandler() {

        binding.lnBody?.visibility = View.INVISIBLE

        binding.txtLoading.text = resources.getText(R.string.select_prod_shift)
        binding.txtLoading.visibility = View.VISIBLE

        val itemsProduction = resources.getStringArray(R.array.tv_production_line)
        val adapterProduction =
            context?.let { ArrayAdapter(it, R.layout.spinner_item, itemsProduction) }
        adapterProduction?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerView.adapter = adapterProduction
        binding.spinnerView.nextFocusDownId = binding.spinnerView.id

        val itemsShift = resources.getStringArray(R.array.shift)
        val adapterShift =
            context?.let { ArrayAdapter(it, R.layout.spinner_item, itemsShift) }
        adapterShift?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerViewShift.adapter = adapterShift
        binding.spinnerViewShift.nextFocusDownId = binding.spinnerViewShift.id


        binding.spinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG, "onItemSelected: $p2")
                productionLine = p2
                if (productionLine != 0 && shiftIndex != 0) {
                    Log.d(TAG, "onItemSelected: Prod: $productionLine ------ $shiftIndex")
                    // TODO
                    // check internet connection then call api
                    isFirstTime = true
                    runHandlerForData()
//                    apiResponseViewModel.getResponse(getParams())
                } else if (shiftIndex == 0 && productionLine == 0) {
                    binding.txtLoading.text = resources.getText(R.string.select_prod_shift)
                } else if (shiftIndex == 0 || shiftIndex == null) {
                    binding.txtLoading.text = resources.getText(R.string.select_shift)
                } else if (productionLine == 0) {
                    binding.lnBody?.visibility = View.GONE
                    binding.txtLoading.visibility = View.VISIBLE
                    binding.txtLoading.text = resources.getText(R.string.select_production_line)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: Prod")
            }
        }

        binding.spinnerViewShift.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Log.d(TAG, "onItemSelected: $p2")
                    shiftIndex = p2
                    if (productionLine != 0 && shiftIndex != 0) {
                        Log.d(TAG, "onItemSelected: Shift: $productionLine ------ $shiftIndex")
                        isFirstTime = true
                        runHandlerForData()
//                        apiResponseViewModel.getResponse(getParams())
                    } else if (shiftIndex == 0 && productionLine == 0) {
                        binding.txtLoading.text = resources.getText(R.string.select_prod_shift)
                    } else if (productionLine == 0 || productionLine == null) {
                        binding.txtLoading.text = resources.getText(R.string.select_production_line)
                    } else if (shiftIndex == 0) {
                        binding.lnBody?.visibility = View.GONE
                        binding.txtLoading.visibility = View.VISIBLE
                        binding.txtLoading.text = resources.getText(R.string.select_shift)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d(TAG, "onNothingSelected: Shift")
                }

            }
    }

    var finalQCApprovalDataParseDone: Boolean = false
    var wipStockDataParseDone: Boolean = false
    private fun bindObserver() {
        /**
         * Internet connection observer
         * */
        connectivityStatus.observe(viewLifecycleOwner) {
            when (it) {
                is AlertDialogStatus.Connected -> {
                    Log.d(TAG, "bindObserver: Connected")
                    isInternetAvailable = true
                    isInternetAvailable(true)
                }

                is AlertDialogStatus.ConnectionLost -> {
                    Log.d(TAG, "bindObserver: Network lost")
                    isInternetAvailable(false)
                    isInternetAvailable = false
                }

                is AlertDialogStatus.ConnectionNotAvailable -> {
                    Log.d(TAG, "bindObserver: Not connected")
                    isInternetAvailable = false
                    isInternetAvailable(false)
                }
            }
        }


        /**
         * Api response observer
         * **/
        apiResponseViewModel.apiResponseFinalQcApproval.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    Log.d(TAG, "bindObserver: ${it.message}")
                    finalQCApprovalDataParseDone = false
                }

                is NetworkResult.Loading -> {
                    finalQCApprovalDataParseDone = false
                }

                is NetworkResult.Success -> {
                    if (it.data!!.data != null) {
                        binding.txtFinalQcApproval!!.text = it.data.data.barcode_count
                        finalQCApprovalDataParseDone = true
                        finalQcApproval = it.data.data.barcode_count.toInt()
                        checkWBSTReceivedData();
                    }
                }
            }
        }
        apiResponseViewModel.apiResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.txtLoading.visibility = View.VISIBLE
                    binding.txtLoading.text = it.message
                    binding.lnBody?.visibility = View.INVISIBLE
                    wipStockDataParseDone = false
                    Log.d(TAG, "bindObserver: ${it.message}")
                    handlerData.removeCallbacksAndMessages(null)
                }

                is NetworkResult.Loading -> {
                    Log.d(TAG, "bindObserver: $isFirstTime")
                    if (isFirstTime) {
                        isFirstTime = false
                        wipStockDataParseDone = false
                        binding.txtLoading.visibility = View.VISIBLE
                        binding.txtLoading.text = resources.getText(R.string.please_wait)
                        binding.lnBody?.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Success -> {
                    Log.d(TAG, "bindObserver: ${it.data}")
                    if (it.data?.status == "failed") {
                        noDataFound = true
                        binding.txtLoading.text = resources.getText(R.string.no_data_found)
                        binding.txtLoading.visibility = View.VISIBLE
                        binding.lnBody?.visibility = View.INVISIBLE
                        handlerData.removeCallbacksAndMessages(null)
                    } else {
                        noDataFound = false
                        wipStockDataParseDone = true
                        checkWBSTReceivedData()
                        setDataUI(it)
                    }
                }
            }
        }
    }

    private fun checkWBSTReceivedData() {
        if (finalQCApprovalDataParseDone && wipStockDataParseDone) {
            wbst_received = getWbistReceivedData(finalQcApproval, wip_stock)
            binding.txtwbstRcv?.text = wbst_received.toString()
        }
    }

    private fun setDataUI(it: NetworkResult.Success<ResponseModel>) {
        if (!noDataFound) {

            binding.txtLastUpdated?.text = getLastUpdatedTime()

            binding.txtLoading.visibility = View.INVISIBLE
            binding.lnBody?.visibility = View.VISIBLE

            qc_passed = it.data?.pass
            wip_stock = it.data?.not_received
//            wbst_received = getWbistReceivedData(qc_passed, wip_stock)

            binding.txtqcPass?.text = qc_passed.toString()
//            binding.txtwbstRcv?.text = wbst_received.toString()
            binding.txtStock?.text = wip_stock.toString()

            if (it.data?.barcode != null && it.data.barcode.isNotEmpty()) {
                binding.barGridView?.adapter = it.data.barcode.let { it1 -> ListAdapter(it1) }
                binding.barGridView?.let { it1 -> autoScrollGridViewLoop(it1) }
                binding.txtNoBarcode?.visibility = View.INVISIBLE
            } else {
                binding.txtNoBarcode?.visibility = View.VISIBLE
            }
        }

    }

    private fun getLastUpdatedTime(): CharSequence {
        return " ${binding.txtClock.text}"
    }

    private fun getWbistReceivedData(qcPassed: Int?, wipStock: Int?): Int? {
        return wipStock?.let { qcPassed?.minus(it) }
    }

    private fun isInternetAvailable(b: Boolean) {
        if (!b) {
            if (!isRunning) {
                runHandlerforNoInternet(true)
            }
        } else {
            Log.d(TAG, "isInternetAvailable: Step 1")
            if (isRunning) {
                Log.d(TAG, "isInternetAvailable: Step 2")
                runHandlerforNoInternet(false)
            }
        }
    }

    val handler = Handler()
    val handlerData = Handler()
    var checkIsVisible = false

    private fun runHandlerforNoInternet(b: Boolean) {
        Log.d(TAG, "isInternetAvailable: Step 3")
        runnable = object : Runnable {
            override fun run() {
                if (checkIsVisible) {
                    binding.imgNoInternet?.visibility = View.INVISIBLE
                    checkIsVisible = false
                } else {
                    checkIsVisible = true
                    binding.imgNoInternet?.visibility = View.VISIBLE
                }
                handler.postDelayed(this, 500)
            }
        }

        if (b) {
            startHandler()
        } else {
            Log.d(TAG, "isInternetAvailable: Step 4")
            cancelHandler()
        }
    }

    private fun runHandlerForData() {
        Log.d(TAG, "isInternetAvailable: Step 3")
        runnableData = object : Runnable {
            override fun run() {
                if (isInternetAvailable) {
                    Log.d(TAG, "run: No Data found $noDataFound")
                    apiResponseViewModel.getResponse(getParams())
                    apiResponseViewModel.getFinalQcApproval(getFinalQcApprovalParams())
                    if (!noDataFound) {

                    }
                }
                handlerData.postDelayed(this, 300000)
            }
        }

        if (isInternetAvailable) {
            handlerData.post(runnableData)
        } else {
            binding.txtLoading.text = resources.getText(R.string.no_internet_connection_message)
        }
    }

    fun startHandler() {
        isRunning = true
        handler.post(runnable) // run the runnable in 1 second
    }

    fun cancelHandler() {
        Log.d(TAG, "isInternetAvailable: Step 5")
        handler.removeCallbacksAndMessages(null) // cancel the runnable

        isRunning = false
        Log.d(TAG, "isInternetAvailable: Final Step")
        if (binding.imgNoInternet?.isVisible == true) {
            binding.imgNoInternet!!.visibility = View.INVISIBLE
        }
    }

    private fun autoScrollGridViewLoop(gridView: GridView) {
        val handler = Handler(Looper.getMainLooper())
        val step = 2
        val delay = 20L

        val runnable = object : Runnable {
            override fun run() {
                // Try scrolling
                gridView.smoothScrollBy(step, 0)

                // Check if we've reached the bottom
                val lastVisible = gridView.lastVisiblePosition
                val totalItems = gridView.count

                // Delay reset to top until the last item is off-screen
                if (lastVisible >= totalItems - 1 && !gridView.canScrollVertically(1)) {
                    handler.postDelayed({
                        gridView.setSelection(0)
                    }, 500) // Wait for smooth experience
                }

                handler.postDelayed(this, delay)
            }
        }

        handler.post(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handlerData.removeCallbacksAndMessages(null)
        handler.removeCallbacksAndMessages(null)
        isFirstTime = true
    }
}