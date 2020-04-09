package com.quanticheart.searchtoolbar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchToolbar.setMenuOnClickListener {
            when (it) {
                R.id.act1 -> {
                    Toast.makeText(this, "ACT 1", Toast.LENGTH_SHORT).show()
                }
                R.id.act2 -> {
                    Toast.makeText(this, "ACT 2", Toast.LENGTH_SHORT).show()
                }
                R.id.act3 -> {
                    Toast.makeText(this, "ACT 3", Toast.LENGTH_SHORT).show()
                }
            }
        }

        searchToolbar.setSearchTextListener {
            textSearch.text = it
        }
    }
}
