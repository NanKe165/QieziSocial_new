package com.eggplant.qiezisocial.ui.login.fragment

import android.app.DatePickerDialog
import android.widget.DatePicker
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import kotlinx.android.synthetic.main.ft_select_birth.*
import java.util.*


/**
 * Created by Administrator on 2020/4/26.
 */

class SelectBirthFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.ft_select_birth
    }

    override fun initView() {
        initDatePicker("2010-1-1")
    }

    override fun initEvent() {
        ft_select_birth_sure.setOnClickListener {
            (activity as LoginActivity).setMyBirth(sYear,sMonth,sDay)
        }
    }

    override fun initData() {

    }

    var sDay: Int = 1
    var sMonth: Int = 1
    var sYear: Int = 2010
    private fun initDatePicker(curDate: String) {
        val calendar = Calendar.getInstance()
        var year: Int
        var month: Int
        var day: Int
        try {
            year = Integer.parseInt(curDate.substring(0, curDate.indexOf("-")))
            month = Integer.parseInt(curDate.substring(curDate.indexOf("-") + 1, curDate.lastIndexOf("-"))) - 1
            day = Integer.parseInt(curDate.substring(curDate.lastIndexOf("-") + 1, curDate.length))
        } catch (e: Exception) {
            e.printStackTrace()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }
        ft_select_birth_datepicker.init(year, month, day, object : DatePickerDialog.OnDateSetListener, DatePicker.OnDateChangedListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                sDay = dayOfMonth
                sMonth=month
                sYear=year
            }

            override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                sDay = dayOfMonth
                sMonth=monthOfYear+1
                sYear=year
            }
        })
    }
}
