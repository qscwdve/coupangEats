package com.example.coupangeats.src.menuSelect.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.example.coupangeats.src.menuSelect.model.Option
import com.example.coupangeats.src.menuSelect.model.SelectMenu

class MenuDetailAdapter(val optionList: ArrayList<Option>,
                        val version:Int,
                        val numberOfChoices: Int,
                        val mPosition: Int,
                        val activity: MenuSelectActivity,
                        val parentAdapter: MenuDetailParentAdapter) : RecyclerView.Adapter<MenuDetailAdapter.MenuDetailViewHolder>() {
    // 버전이 1이면 필수 , 2이면 추가선택
    var count = 0
    var necessary = -1
    var selectedOption = Array(optionList.size){i -> false}   // 추가한 것들 저장
    class MenuDetailViewHolder(itemView: View, val menuDetailAdapter: MenuDetailAdapter) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_menu_detail_name)
        val necessaryParent = itemView.findViewById<LinearLayout>(R.id.item_menu_detail_necessary_check_parent)
        val necessaryImg = itemView.findViewById<ImageView>(R.id.item_menu_detail_necessary_check_img)
        val optionParent = itemView.findViewById<LinearLayout>(R.id.item_menu_detail_check_parent)
        val optionImg = itemView.findViewById<ImageView>(R.id.item_menu_detail_check_img)
        val price = itemView.findViewById<TextView>(R.id.item_menu_detail_price)

        fun bind(item: Option, position: Int){
            name.text = item.optionName
            val extraPrice = "(+${priceIntToString(item.extraPrive)}원)"
            price.text = extraPrice
            if(menuDetailAdapter.version == 1){
                // 필수
                optionParent.visibility = View.GONE
                necessaryParent.visibility = View.VISIBLE
                if(menuDetailAdapter.necessary == position){
                    necessaryParent.setBackgroundResource(R.drawable.menu_check_box_click)
                    necessaryImg.setImageResource(R.drawable.ic_nessary_option_round_white)
                } else {
                    necessaryParent.setBackgroundResource(R.drawable.menu_check_box)
                    necessaryImg.setImageResource(R.drawable.ic_nessary_option_round_gray)
                }
                itemView.setOnClickListener {
                    Log.d("checkSelect", "아이템 눌림")
                    // 아이템 선택시
                    if(menuDetailAdapter.count == 0){
                        menuDetailAdapter.count = 1
                        necessaryParent.setBackgroundResource(R.drawable.menu_check_box_click)
                        necessaryImg.setImageResource(R.drawable.ic_nessary_option_round_white)
                        menuDetailAdapter.necessary = position
                    } else {
                        // 필수면 1개인데 이미 선택된 상태이므로 다른 것을 선택하고 되돌려야 한다.
                        menuDetailAdapter.necessary = position
                        menuDetailAdapter.refresh()
                    }
                    menuDetailAdapter.sendActivityData()
                    menuDetailAdapter.changeNecessaryCheck()   // 필수선택을 했는지 확인하는 로직
                    menuDetailAdapter.parentAdapter.checkedNecessary(menuDetailAdapter.mPosition)
                }
            } else {
                // 추가 선택
                optionParent.visibility = View.VISIBLE
                necessaryParent.visibility = View.GONE
                optionImg.visibility = View.VISIBLE
                if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices) {
                    // 선택 불가능으로 나머지 돌려야 한다.
                    if(menuDetailAdapter.selectedOption[position]){
                        optionParent.setBackgroundResource(R.drawable.check_box_on)
                        optionImg.setImageResource(R.drawable.ic_check_white)
                    }
                    else{
                        optionParent.setBackgroundResource(R.drawable.check_box_block)
                        optionImg.visibility = View.INVISIBLE
                    }
                } else {
                    // 선택 아직 가능
                    if(menuDetailAdapter.selectedOption[position]) {
                        optionParent.setBackgroundResource(R.drawable.check_box_on)
                        optionImg.setImageResource(R.drawable.ic_check_white)}
                    else {
                        optionParent.setBackgroundResource(R.drawable.check_box_off)
                        optionImg.setImageResource(R.drawable.ic_check_gray)
                    }
                }
                itemView.setOnClickListener {
                    Log.d("checkSelect", "아이템 눌림")
                    // 아이템 선택시
                    if(menuDetailAdapter.count <= menuDetailAdapter.numberOfChoices){
                        if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices){
                            if(menuDetailAdapter.selectedOption[position]){
                                // 취소
                                optionParent.setBackgroundResource(R.drawable.check_box_off)
                                optionImg.setImageResource(R.drawable.ic_check_gray)
                                menuDetailAdapter.count--
                                menuDetailAdapter.selectedOption[position] = false
                                menuDetailAdapter.refresh()
                            }
                        } else {
                            if(!menuDetailAdapter.selectedOption[position] && menuDetailAdapter.count < menuDetailAdapter.numberOfChoices){
                                // 아직 선택 안했을 때
                                optionParent.setBackgroundResource(R.drawable.check_box_on)
                                optionImg.setImageResource(R.drawable.ic_check_white)
                                menuDetailAdapter.selectedOption[position] = true
                                menuDetailAdapter.count++
                                 if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices){
                                    // 선택 불가능으로 나머지 돌려야 한다.
                                    menuDetailAdapter.refresh()
                                }
                            } else {
                                // 선택된 것 취소
                                optionParent.setBackgroundResource(R.drawable.check_box_off)
                                optionImg.setImageResource(R.drawable.ic_check_gray)
                                menuDetailAdapter.count--
                                menuDetailAdapter.selectedOption[position] = false
                            }
                        }
                    }
                    menuDetailAdapter.sendActivityData()
              }
            }
        }
        fun priceIntToString(value: Int) : String {
            val target = value.toString()
            val size = target.length
            return if(size > 3){
                val last = target.substring(size - 3 until size)
                val first = target.substring(0..(size - 4))
                "$first,$last"
            } else target
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_detail, parent, false)
        return MenuDetailViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MenuDetailViewHolder, position: Int) {
        holder.bind(optionList[position], position)
    }

    override fun getItemCount(): Int = optionList.size

    fun refresh() {
        notifyDataSetChanged()
    }

    fun changeNecessaryCheck(){
        parentAdapter.necessaryCheckList[mPosition].check = true
    }

    fun sendActivityData(){
        var content = ""
        var totalPrice = 0
        if(necessary != -1){
            // 필수 옵션 1개
            val extraPrice = if(optionList[necessary].extraPrive != 0) "(+${priceIntToString(optionList[necessary].extraPrive)}원)" else ""
            totalPrice = optionList[necessary].extraPrive
            content = optionList[necessary].optionName + extraPrice
        } else {
            // 선택 옵션 여러개
            for(index in selectedOption.indices){
                if(selectedOption[index]){
                    // 선택된 것
                    val extraPrice = "(+${priceIntToString(optionList[index].extraPrive)}원)"
                    content += ", " + optionList[index].optionName + extraPrice
                    totalPrice += optionList[index].extraPrive
                }
            }
            if(content.isNotEmpty()){
                if(content[content.length - 1] == ',') content = content.substring(2 until (content.length - 1))
                else content = content.substring(2 until content.length)
            }
        }
        activity.saveMenuInfo(mPosition, SelectMenu(content, totalPrice))
        activity.changeTotalPrice(totalPrice)
    }

    fun priceIntToString(value: Int) : String {
        val target = value.toString()
        val size = target.length
        return if(size > 3){
            val last = target.substring(size - 3 until size)
            val first = target.substring(0..(size - 4))
            "$first,$last"
        } else target
    }

}