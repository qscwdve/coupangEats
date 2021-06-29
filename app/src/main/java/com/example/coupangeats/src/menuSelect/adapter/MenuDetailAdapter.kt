package com.example.coupangeats.src.menuSelect.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    var count = 0;
    var necessary = -1
    var selectedOption = Array(optionList.size){i -> false}   // 추가한 것들 저장
    class MenuDetailViewHolder(itemView: View, val menuDetailAdapter: MenuDetailAdapter) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_menu_detail_name)
        val click = itemView.findViewById<ImageView>(R.id.item_menu_detail_click)
        val price = itemView.findViewById<TextView>(R.id.item_menu_detail_price)

        fun bind(item: Option, position: Int){
            name.text = item.optionName
            val extraPrice = "(+${priceIntToString(item.extraPrive)}원)"
            price.text = extraPrice
            if(menuDetailAdapter.version == 1){
                // 필수
                if(menuDetailAdapter.necessary == position)
                    click.setImageResource(R.drawable.ic_necessary_option_click)
                 else
                     click.setImageResource(R.drawable.ic_necessary_option)
                itemView.setOnClickListener {
                    Log.d("checkSelect", "아이템 눌림")
                    // 아이템 선택시
                    if(menuDetailAdapter.count == 0){
                        menuDetailAdapter.count = 1
                        click.setImageResource(R.drawable.ic_necessary_option_click)
                        menuDetailAdapter.necessary = position
                    } else {
                        // 필수면 1개인데 이미 선택된 상태이므로 다른 것을 선택하고 되돌려야 한다.
                        menuDetailAdapter.necessary = position
                        menuDetailAdapter.refresh()
                    }
                    menuDetailAdapter.sendActivityData()
                    menuDetailAdapter.changeNecessaryCheck()   // 필수선택을 했는지 확인하는 로직
                }
            } else {
                // 추가 선택
                if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices) {
                    // 선택 불가능으로 나머지 돌려야 한다.
                    if(menuDetailAdapter.selectedOption[position]) click.setImageResource(R.drawable.ic_add_option_click)
                    else click.setImageResource(R.drawable.ic_add_option_false)
                } else {
                    // 선택 아직 가능
                    if(menuDetailAdapter.selectedOption[position]) click.setImageResource(R.drawable.ic_add_option_click)
                    else click.setImageResource(R.drawable.ic_add_option)
                }
                itemView.setOnClickListener {
                    Log.d("checkSelect", "아이템 눌림")
                    // 아이템 선택시
                    if(menuDetailAdapter.count <= menuDetailAdapter.numberOfChoices){
                        if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices){
                            if(menuDetailAdapter.selectedOption[position]){
                                // 취소
                                click.setImageResource(R.drawable.ic_add_option)
                                menuDetailAdapter.count--
                                menuDetailAdapter.selectedOption[position] = false
                                menuDetailAdapter.refresh()
                            }
                        } else {
                            if(!menuDetailAdapter.selectedOption[position] && menuDetailAdapter.count < menuDetailAdapter.numberOfChoices){
                                // 아직 선택 안했을 때
                                click.setImageResource(R.drawable.ic_add_option_click)
                                menuDetailAdapter.selectedOption[position] = true
                                menuDetailAdapter.count++
                                if(menuDetailAdapter.count == menuDetailAdapter.numberOfChoices){
                                    // 선택 불가능으로 나머지 돌려야 한다.
                                    menuDetailAdapter.refresh()
                                }
                            } else {
                                // 선택된 것 취소
                                click.setImageResource(R.drawable.ic_add_option)
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