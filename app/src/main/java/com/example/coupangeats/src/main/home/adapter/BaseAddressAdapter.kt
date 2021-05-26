package com.example.coupangeats.src.main.home.adapter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.home.HomeFragment

class BaseAddressAdapter(val fragmentInfo : HomeFragment) : RecyclerView.Adapter<BaseAddressAdapter.BaseAddressViewHolder>() {
    private val itemList = getBasicAddressArrayList()
    class BaseAddressViewHolder(itemView : View, val fragmentInfo: HomeFragment) : RecyclerView.ViewHolder(itemView) {
        val locationName = itemView.findViewById<TextView>(R.id.item_home_no_address_text)

        fun bind(item: String){
            locationName.text = item
            itemView.setOnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    fragmentInfo.baseAddressResult(item)
                }, 100)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_no_address, parent,false)
        return BaseAddressViewHolder(view, fragmentInfo)
    }

    override fun onBindViewHolder(holder: BaseAddressViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun getBasicAddressArrayList() : ArrayList<String> {
        return arrayListOf(
            "서울시 강남구",
            "서울시 송파구",
            "서울시 서초구",
            "서울시 관악구",
            "경기도 용인시",
            "서울시 동작구",
            "서울시 강동구",
            "서울시 마포구",
            "서울시 광진구",
            "서울시 용산구",
            "서울시 성동구",
            "서울시 강서구",
            "서울시 양천구",
            "서울시 영동포구",
            "서울시 구로구",
            "서울시 금천구",
            "서울시 서대문구",
            "서울시 은평구",
            "서울시 중구",
            "서울시 중랑구",
            "서울시 강북구",
            "서울시 성북구",
            "서울시 도봉구",
            "서울시 노원구",
            "경기도 성남시",
            "경기도 부천시",
            "인천시 부평구",
            "인천시 계양구",
            "인천시 남동구",
            "경기도 수원시",
            "인천시 미추홀구",
            "인천시 연수구",
            "인천시 동구",
            "인천시 서구",
            "인천시 중구",
            "경기도 김포시",
            "경기도 의정부시",
            "경기도 구리시",
            "경기도 고양시",
            "경기도 안양시",
            "경기도 군포시",
            "경기도 남양주시",
            "경기도 하남시",
            "경기도 과천시",
            "경기도 안산시",
            "경기도 시흥시",
            "경기도 파주시",
            "경기도 포천시",
            "경기도 광주시",
            "경기도 광명시",
            "경기도 안성시",
            "걍기도 여주시",
            "걍기도 동두천시",
            "세종시",
            "경기도 화성시",
            "경기도 오산시",
            "경기도 이천시",
            "경기도 평택시",
            "경기도 의왕시",
            "경기도 양주시",
            "부산시 수영구",
            "부산시 남구",
            "부산시 부산진구",
            "부산시 동구",
            "부산시 중구",
            "부산시 서구",
            "부산시 연제구",
            "부산시 동래구",
            "대전시 서구",
            "대전시 중구",
            "대전시 유성구",
            "부산시 금정구",
            "부산시 영도구",
            "부산시 북구",
            "부산시 사상구",
            "부산시 강서구",
            "부산시 사하구",
            "부산시 기장군",
            "울산시 중구",
            "울산시 남구",
            "울산시 동구",
            "울산시 북구",
            "울산시 울주군",
            "대구시 달서구",
            "대구시 서구",
            "대구시 남구",
            "대구시 중구",
            "대구시 동구",
            "광주시 동구",
            "광주시 북구",
            "광주시 남구",
            "광주시 서구",
            "대구시 북구",
            "대구시 수성구",
            "경남 김해시",
            "경남 양산시",
            "경남 통영시",
            "경남 창원시",
            "경남 진주시",
            "경남 거제시",
            "경남 사천시",
            "경남 밀양시",
            "경북 경주시",
            "경북 안동시",
            "경북 상주시",
            "경북 영천시",
            "충북 청주시",
            "충북 제천시",
            "충북 충주시",
            "충북 천안시",
            "충남 서산시",
            "충남 당진시",
            "충남 아산시",
            "충남 공주시",
            "충남 보령시",
            "전북 전주시",
            "전북 군산시",
            "전북 정읍시",
            "전북 익산시",
            "전남 여주시",
            "전남 순천시",
            "전남 목포시",
            "전남 무안군",
            "전남 광양시",
            "강원 원주시",
            "강원 춘천시",
            "강원 강릉시",
            "강원 동해시",
            "강원 속초시",
            "제주 제주시",
            "제주 서귀포시"
        )
    }
}