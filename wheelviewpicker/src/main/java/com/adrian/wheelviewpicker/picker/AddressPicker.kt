package com.adrian.wheelviewpicker.picker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import com.adrian.simplemvp.views.BaseDialog

import com.adrian.wheelviewpicker.R
import com.adrian.wheelviewpicker.utils.AssetsUtils
import com.adrian.wheelviewpicker.view.OnItemSelectedListener
import com.adrian.wheelviewpicker.view.WheelView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

class AddressPicker(context: Context) : BaseDialog(context), View.OnClickListener {
    override fun getLayoutResId(): Int {
        return R.layout.layout_address_picker
    }

    override fun initViews() {
        initView()
    }

    /**
     * 所有省
     */
    private var mProvinceDatas: ArrayList<String>? = null
    /**
     * key - 省 value - 市
     */
    private val mCitisDatasMap = HashMap<String, ArrayList<String>>()
    /**
     * key - 市 values - 区
     */
    private val mDistrictDatasMap = HashMap<String, ArrayList<String>>()
    private var mViewProvince: WheelView? = null
    private var mViewCity: WheelView? = null
    private var mViewDistrict: WheelView? = null
    private var mTvConfirm: TextView? = null
    private var mTvCancel: TextView? = null
    private var onAddressListener: OnAddressListener? = null

    internal var cities: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val view = View.inflate(context, R.layout.layout_address_picker, null)
        initCityData()

//        initView()
        initData()
        setListener()
//        this.setContentView(view)

        this.setCanceledOnTouchOutside(true)

        //从底部弹出
        val window = this.window
        window!!.setGravity(Gravity.BOTTOM)  //此处可以设置dialog显示的位置
        //        window.setWindowAnimations(R.style.windowAnimationStyle);  //添加动画

        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = params
    }


    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.tv_confirm) {
            if (onAddressListener != null) {

                val mCurrentProvinceName = mProvinceDatas!![mViewProvince!!.selectedItem]

                var cityPos = mViewCity!!.selectedItem
                cityPos = if (cityPos >= mCitisDatasMap[mCurrentProvinceName]!!.size - 1) mCitisDatasMap[mCurrentProvinceName]!!.size - 1 else cityPos
                val mCurrentCityName = mCitisDatasMap[mCurrentProvinceName]!![cityPos]

                var districtPos = mViewDistrict!!.selectedItem
                districtPos = if (districtPos >= mDistrictDatasMap[mCurrentCityName]!!.size - 1) mDistrictDatasMap[mCurrentCityName]!!.size - 1 else districtPos
                val mCurrentDistrictName = mDistrictDatasMap[mCurrentCityName]!![districtPos]

                onAddressListener!!.onAddressSelected(mCurrentProvinceName, mCurrentCityName, mCurrentDistrictName)
            }
        }
        cancel()
    }

    /**
     * 回调接口
     */
    interface OnAddressListener {
        fun onAddressSelected(province: String, city: String, district: String)
    }

    fun setAddressListener(onAddressListener: OnAddressListener) {
        this.onAddressListener = onAddressListener
    }

    /**
     * 初始化布局
     */
    private fun initView() {
        mViewProvince = findViewById(R.id.wv_province)
        mViewCity = findViewById(R.id.wv_city)
        mViewDistrict = findViewById(R.id.wv_district)
        mTvConfirm = findViewById(R.id.tv_confirm)
        mTvCancel = findViewById(R.id.tv_cancel)

        /**
         * 设置可见条目数量
         * 注：因为WheelView是圆形，最上面和最下面刚好在圆顶和圆底，
         * 所以最上面和最下面两个看不到，因此可见数量要比设置的少2个
         */
        mViewProvince!!.visibleItemCount = 9
        mViewCity!!.visibleItemCount = 9
        mViewDistrict!!.visibleItemCount = 9
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        //省
        mViewProvince!!.setItems(mProvinceDatas!!)
        mViewProvince!!.setCurrentItem(0)
        //市
        val province: String = mProvinceDatas!![0]
        mViewCity!!.setItems(mCitisDatasMap[province]!!)
        mViewCity!!.setCurrentItem(0)
        //区
        mViewDistrict!!.setItems(mDistrictDatasMap[mCitisDatasMap[mProvinceDatas!![0]]!![0]]!!)
        mViewDistrict!!.setCurrentItem(0)
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        //省-------------------------------------------
        mViewProvince!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                updateCities(index)
            }
        }
        //市-------------------------------------------
        mViewCity!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                updateAreas(index)
            }
        }
        mTvConfirm!!.setOnClickListener(this)
        mTvCancel!!.setOnClickListener(this)
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private fun updateCities(index: Int) {
        val province = mProvinceDatas!![index]
        cities = mCitisDatasMap[province]
        mViewCity!!.setItems(cities!!)
        mViewCity!!.setCurrentItem(0)

        updateAreas(0)
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private fun updateAreas(index: Int) {

        val city = cities!![index]
        val areas = mDistrictDatasMap[city]

        mViewDistrict!!.setItems(areas!!)
        mViewDistrict!!.setCurrentItem(0)
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     * 解析json，完成后释放Json对象的内存
     */
    private fun initCityData() {
        try {
            val json = AssetsUtils.readText(context, "city.json")
            if (TextUtils.isEmpty(json)) return
            val mJsonObj = JSONObject(json)
            val jsonArray = mJsonObj.getJSONArray("citylist")
//            mProvinceDatas = arrayOfNulls<String>(jsonArray!!.length())
            mProvinceDatas = arrayListOf()
            for (i in 0 until jsonArray.length()) {
                val jsonP = jsonArray.getJSONObject(i)// 每个省的json对象
                val province = jsonP.getString("areaName")// 省名字
                mProvinceDatas!!.add(province)
                var jsonCs: JSONArray? = null
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("cities")
                } catch (e1: Exception) {
                    continue
                }

                val mCitiesDatas = arrayListOf<String>()
                for (j in 0 until jsonCs.length()) {
                    val jsonCity = jsonCs.getJSONObject(j)
                    val city = jsonCity.getString("areaName")// 市名字
                    mCitiesDatas.add(city)
                    var jsonAreas: JSONArray? = null
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("counties")
                    } catch (e: Exception) {
                        continue
                    }

                    val mAreasDatas = arrayListOf<String>()// 当前市的所有区
                    for (k in 0 until jsonAreas.length()) {
                        val area = jsonAreas.getJSONObject(k).getString("areaName")// 区域的名称
                        mAreasDatas.add(area)
                    }
                    mDistrictDatasMap[city] = mAreasDatas
                }
                mCitisDatasMap[province] = mCitiesDatas
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

}
