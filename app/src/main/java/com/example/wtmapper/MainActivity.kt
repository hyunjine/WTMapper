package com.example.wtmapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mapper_annotation.GenName
import com.example.mapper_annotation.Kson
import com.example.mapper_annotation.Mapper
import com.example.reflection.MapperObject

class MainActivity : AppCompatActivity() {
    val hello: Hello = Hello()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = User(
            name = "Test",
            email = "test@email.com"
        )

        Log.d("winter", user.toJson())
    }
}

@GenName
class Hello {

}

@Kson
data class User(
    val name: String,
    val email: String
)