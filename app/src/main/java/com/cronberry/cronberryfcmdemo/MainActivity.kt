package com.cronberry.cronberryfcmdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.cronberry.fcmpushnotification.CronberryPref
import com.cronberry.fcmpushnotification.Utility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var myPref: CronberryPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myPref = CronberryPref(this)
        if (myPref.userEmailPref != "") {
            openSecondActivity()
        }
        button.setOnClickListener {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            val emailId = editText2
            if (emailId.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Enter email address", Toast.LENGTH_SHORT)
                    .show();
                return@setOnClickListener
            } else {
                if (!emailId.text.toString().trim().matches(Regex(emailPattern))) {
                    Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT)
                        .show();
                    return@setOnClickListener

                }
            }

            progressBar.visibility = View.VISIBLE
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult?> {
                    override fun onComplete(@NonNull task: Task<InstanceIdResult?>) {
                        if (!task.isSuccessful) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "No token found", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("arnish", "no token found")
                            return
                        }

                        val refreshedToken = task.result!!.token
                        Log.d("arnish", "NEw Token: ")
                        Log.d("arnish", "Token: $refreshedToken")
                        val retrofitObj = Utility.getRetrofitObj(this@MainActivity)
                        val hashMap = HashMap<String, Any>()
                        hashMap["projectKey"] = "VW50aXRsZSBQcm9qZWN0MTU5MDc1OTQ2NDgzNA=="
                        hashMap["audienceId"] = System.currentTimeMillis().toString()
                        hashMap["android_fcm_token"] = refreshedToken
                        val paramList = ArrayList<HashMap<String, Any>>()
                        val dataMap = HashMap<String, Any>()
                        dataMap["paramKey"] = "demo_email"
                        dataMap["paramValue"] = editText2.text.toString().trim()
                        paramList.add(dataMap)
                        hashMap["paramList"] = paramList
                        retrofitObj!!.registerAudience(hashMap)
                            .enqueue(object : Callback<LinkedHashMap<Any, Any>> {
                                override fun onFailure(
                                    call: Call<LinkedHashMap<Any, Any>>,
                                    t: Throwable
                                ) {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onResponse(
                                    call: Call<LinkedHashMap<Any, Any>>,
                                    response: Response<LinkedHashMap<Any, Any>>
                                ) {
                                    progressBar.visibility = View.GONE
                                    try {
                                        val body = response.body()!!
                                        Log.d("cronberry", body.toString())
                                        if (body["status"].toString() == "false") {
                                            Toast.makeText(
                                                this@MainActivity,
                                                body["error_msgs"].toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            myPref.userEmailPref = emailId.text.toString().trim()
                                            openSecondActivity()
                                        }
                                    } catch (ex: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            })
                        Log.d("arnish", " token found")
                        Log.d("arnish", refreshedToken)
                    }
                })
        }
    }

    private fun openSecondActivity() {
        val intent = Intent(this, InfomationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
