package com.stb.qr_scanner


import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stb.qr_scanner.databinding.ActivityMainBinding
import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import android.webkit.URLUtil
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    //activity_main.xml
    private lateinit var binding: ActivityMainBinding
    private var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //link buttons
        setOnCLickListener()
        setupScanner()
        //do scann stuff
        performAction()
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator?.setBeepEnabled(false)
    }

    private fun setOnCLickListener(){
        binding.buttonCopyLink.setOnClickListener{copyLinktoClipboard()}
        binding.buttonOpenWebsite.setOnClickListener{openURL()}
        binding.buttonOpenWebsite.setOnClickListener{newScan()}
    }

    /**
     * maybe unnecessary but maybe need to do something before new scanning
     */
    private fun newScan(){
        performAction()
    }

    /**
     * opens url that is in 'siteName' text field
     */
    private fun openURL(){
        val url = binding.siteName.text.toString()
        if (url.isNotEmpty()){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }else{
            Toast.makeText(this,"nothing to open", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * copies url to clipboard of phone
     */
    private fun copyLinktoClipboard(){
        val url = binding.siteName.text.toString()
        if (url.isNotEmpty()) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Link", url)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "link copied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "nothing to copy", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * starts the scanner
     */
    private fun performAction() {
        qrScanIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {
                    // Converting the data to json format
                    val obj = JSONObject(result.contents)

                    // Show values in UI.
                    binding.name.text = obj.getString("name")
                    binding.siteName.apply {
                        setAutoLinkMask(Linkify.WEB_URLS)
                        isClickable = true
                        isFocusable = true
                    }
                    binding.siteName.text = obj.getString("site_name")

                } catch (e: JSONException) {
                    //not in json format so just showing url
                    binding.name.text = "Scanned Data:"
                    binding.siteName.text = result.contents
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
