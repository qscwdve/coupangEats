package com.example.coupangeats.src.reviewWrite.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.example.coupangeats.src.reviewWrite.model.PhotoData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class FirebaseControl(val activity: ReviewWriteActivity, val userIdx: String) {
    val storage = FirebaseStorage.getInstance()
    val simpleDateFormat = SimpleDateFormat("yyMMddhhmmss")

    fun addFireBaseImg(uri: Uri) {
        val storageRef = storage.reference

        val filename = makeFileName()
        val riversRef = storageRef.child("review_photo_img/${filename}.jpg")
        val uploadTask = riversRef.putFile(uri)

        // 새로운 이미지 저장
        uploadTask.addOnFailureListener {
            Log.d("firebase", "사진 넣기 실패")
        }.addOnSuccessListener {
            Log.d("firebase", "사진 넣기 성공")
        }
        val uriTask = uploadTask.continueWithTask { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val downloadUri = task.result
                Log.d("firebase", "다운받은 파일 uri : ${downloadUri}")
                activity.addImgToPhotoAdapter(downloadUri.toString())
            }
        }
    }

    fun makeFileName(): String {
        val now = System.currentTimeMillis()
        val data = Date(now)
        val getTime = simpleDateFormat.format(data)
        return "${now.toString()}${userIdx}${getTime.toString()}"
    }

    fun getFirebaseImg(fileUrl: String) {
        val storageRef: StorageReference = storage.getReference()
        storageRef.child(fileUrl).downloadUrl.addOnSuccessListener { uri -> //이미지 로드 성공시
            //activity.addImgToPhotoAdapter(photoData)
            //Log.d("firebase", "사진 가져오기 성공, ${uri.toString()}")
        }.addOnFailureListener {
            Log.d("firebase", "사진 가져오기 실패")
        }
    }
}