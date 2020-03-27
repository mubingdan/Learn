package com.example.learndemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aspectjx.annotations.OnSingleClick
import com.example.router.Router
import com.example.router.annotation.Route
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = "/app/main", group = "app")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate...")
        hello.setOnClickListener { clickHello() }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart()...")
        loadData(3)
    }

    private fun loadData(id: Int): Int {
        Log.d("MainActivity", "loadData() id = $id")
        return id
    }

    @OnSingleClick(value = 1000)
    override fun onClick(v: View?) {
        Log.d("MainActivity", "onClick...")
    }

    @OnSingleClick
    fun clickHello() {
        Log.d("MainActivity", "clickHello...")
        Toast.makeText(this, "Hello, AspectJ", Toast.LENGTH_LONG).show()
        testAop()
        testAop2()
        Router.getInstance().build("/app/second").navigation()
    }

    fun testAopWithincode() {
        Log.d("MainActivity", "test aop withincode...")
    }

    fun testAop() {
        Log.d("MainActivity", "test aop() ...")
        testAopWithincode()
    }

    fun  testAop2() {
        Log.d("MainActivity", "test aop2() ...")
        testAopWithincode()
    }
}
