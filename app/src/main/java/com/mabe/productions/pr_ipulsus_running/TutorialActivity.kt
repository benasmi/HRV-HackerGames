package com.mabe.productions.pr_ipulsus_running

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial)

        val images = ArrayList<SlideModel>()
        images.add(SlideModel(R.raw.slide_1))
        images.add(SlideModel(R.raw.slide_2))
        images.add(SlideModel(R.raw.slide_3))
        images.add(SlideModel(R.raw.slide_4))
        images.add(SlideModel(R.raw.slide_5))
        images.add(SlideModel(R.raw.slide_6))
        images.add(SlideModel(R.raw.slide_7))
        images.add(SlideModel(R.raw.slide_8))
        images.add(SlideModel(R.raw.slide_9))
        images.add(SlideModel(R.raw.slide_10))
        images.add(SlideModel(R.raw.slide_11))

        image_slider.setImageList(images, true)
        image_slider.setOnPageChangeListener(){
            if(it + 1 == images.size){
                finishButton.visibility = View.VISIBLE
            }else{
                finishButton.visibility = View.GONE
            }
        }
        finishButton.setOnClickListener {
            this.finish()
            startActivity(Intent(this, MainScreenActivity::class.java))
        }
    }

    //Accessing private member of ImageSlider called viewPager via reflection to set on page changed listener.
    //A neat workaround
    fun ImageSlider.setOnPageChangeListener(listener: (Int) -> Unit){
        val field = this.javaClass.declaredFields
                .toList().filter { it.name == "viewPager" }.first()
        field.isAccessible = true
        val value = field.get(this)
        val viewPager = value as ViewPager
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                listener(position)
            }

        })

    }
}