package com.mabe.productions.pr_ipulsus_running

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_legal_info.*

class LegalInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_info)

        img_back_arrow.setOnClickListener {
            this.finish()
        }
        val verdana = Typeface.createFromAsset(assets,
                "fonts/futura_light.ttf")
        privacy_policy_text.setTypeface(verdana)
        toolbar_title_registration.setTypeface(verdana)
    }
}