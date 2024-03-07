package com.example.qrcodescanner

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var imageBitmap: Bitmap

    val option = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureBtn.setOnClickListener {
//            Toast.makeText(this,"Capture Btn Clicked",Toast.LENGTH_SHORT).show()
            takeImage()
        }

        binding.scanBtn.setOnClickListener {
//            Toast.makeText(this,"Scan Btn Clicked",Toast.LENGTH_SHORT).show()
            processImage()
        }
    }

    private fun processImage() {
        if(imageBitmap!=null){
            val image = InputImage.fromBitmap(imageBitmap,0)
            val scanner = BarcodeScanning.getClient(option)
            scanner.process(image).addOnSuccessListener {barcodes->
                if(barcodes.isEmpty()){
                    Toast.makeText(this,"Nothing to Scan",Toast.LENGTH_SHORT).show()
                }
                for(barcode in barcodes){
                    val type = barcode.valueType
                    when(type){
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi?.ssid
                            val pass = barcode.wifi?.password
                            val wifiType = barcode.wifi?.encryptionType

                            binding.qrText.text = ssid+"\n"+pass+"\n"+wifiType
                        }

                        Barcode.TYPE_URL -> {
                            val title = barcode.url?.title
                            val link = barcode.url?.url

                            binding.qrText.text = title+"\n"+link
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("Error",it.message.toString())
            }
        }
        else{
            Toast.makeText(this,"Please capture Image to Scan",Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(intent,123)
        }
        catch (e:Exception){
            Log.e("Error",e.message.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123 && resultCode== RESULT_OK){
            val bundle:Bundle? = data?.extras
            imageBitmap = bundle!!.get("data") as Bitmap
            binding.qrImage.setImageBitmap(imageBitmap)
        }
    }
}