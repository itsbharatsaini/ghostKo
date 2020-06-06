package com.example.ghostpy

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ghostpy.R
import com.example.ghostpy.Utils.Encrypter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    companion object{
        private val OPEN_REQUEST_CODE = 1037
        private val SAVE_REQUEST_CODE = 5473
        private var selected_FilePath ="";
        private var FileDir ="";
        private var FileName ="";
        private var file_password = ""
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        
        val select_file: Button = findViewById(R.id.select_file)
        select_file.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            //intent.type = "*/*"
            startActivityForResult(intent,OPEN_REQUEST_CODE)
        }

        
        val Submit_button: Button = findViewById(R.id.submit_button)
        Submit_button.setOnClickListener{
            if (selected_FilePath!="") {
                val password = findViewById<EditText>(R.id.password)
                val c_password = findViewById<EditText>(R.id.c_password)

                file_password = password.text.toString().trim()
                val file_c_password = c_password.text.toString().trim()
                if (file_password.isEmpty()){
                    Toast.makeText(applicationContext, "Please Enter Password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else if(file_c_password.isEmpty()){
                    Toast.makeText(applicationContext, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else if (file_password.length != 8){
                    Toast.makeText(applicationContext, "Password must be 8 characters long Only", Toast.LENGTH_SHORT).show()
                }else if(file_password != file_c_password){

                    Toast.makeText(applicationContext, "Password not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else{
                    val id: Int = radio_group.checkedRadioButtonId
                    if (id!=-1){
                        if (id ==2131165187) {
                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),SAVE_REQUEST_CODE)
                                }else{
                                    Enc_Save_File()
                                }
                            }else{
                                Enc_Save_File()
                            }
                        }else{
                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),SAVE_REQUEST_CODE)
                                }else{
                                    Dec_Save_File()
                                }
                            }else{
                                Dec_Save_File()
                            }
                        }
                    } else{
                        Toast.makeText(applicationContext,"You must choose option",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(applicationContext,"Please selected file",
                    Toast.LENGTH_SHORT).show()
            }
        }


        val Clear_Button: Button = findViewById(R.id.reset)
        Clear_Button.setOnClickListener {
            clear_all()

        }
    }


    private fun clear_all(){
        selected_FilePath ="";
        file_view.setText("")
        password.setText("")
        c_password.setText("")
        radio_group.clearCheck()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!,filePathColumn,null,null,null)
            cursor?.moveToFirst()
            val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0])!!
            selected_FilePath = cursor.getString(columnIndex)
            cursor.close()
            val file_intent = File(selected_FilePath)
            FileDir = file_intent.parent+"/"
            FileName = file_intent.name
            file_view.setText("Selected file :" + "${selected_FilePath}")

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==SAVE_REQUEST_CODE){
            if (grantResults.isEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Permission Granted", Toast.LENGTH_SHORT).show()
            }else{
                if (grantResults[0] ==PackageManager.PERMISSION_DENIED)
                    Toast.makeText(applicationContext,"Permission Denied", Toast.LENGTH_SHORT).show()
        }
    } }

    private fun Enc_Save_File(){
        val externalStorageState = Environment.getExternalStorageState()
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)){

            val inputStream = File(selected_FilePath)

            val outputFileEnc = FileOutputStream(File(FileDir,"Encrypt_" + "${FileName}"))
            try {
                val Password = file_password+file_password
                Encrypter.encryptToFile(Password, Password, FileInputStream(inputStream),outputFileEnc)
                clear_all()
                inputStream.delete()
                Toast.makeText(applicationContext, "File Encrypted", Toast.LENGTH_SHORT).show()

            }catch (e:Exception ){
                clear_all()
                Toast.makeText(applicationContext, "File Not Encrypted", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }else{
            Toast.makeText(applicationContext,"External Storage not Mounted",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun Dec_Save_File(){
        val inputStream = File(selected_FilePath)
        val outputStream = File(selected_FilePath.replace("Encrypt_",""))

        try {
            val Password = file_password+file_password
            Encrypter.decryptToFile(Password, Password,FileInputStream(inputStream),FileOutputStream(outputStream))
            clear_all()
            inputStream.delete()
            Toast.makeText(applicationContext, "File Decrypted", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            clear_all()
            Toast.makeText(applicationContext, "File Not Decrypted", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

}