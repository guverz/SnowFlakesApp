package com.example.snowflakesapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
//import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

// TODO: сделать снежинки различных оттенков, для чего добавить поле "цвет"
data class Snowflake(var x: Float, var y: Float, val velocity: Float, val radius: Float, val color: Int)
lateinit var snow: Array<Snowflake>
val paint = Paint()
var h = 1000; var w = 1000 // значения по умолчанию, будут заменены в onLayout()


open class Snowflakes(ctx: Context) : View(ctx) {
    lateinit var moveTask: MoveTask
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // перерисовывает канву
        canvas.drawColor(Color.rgb(10, 18, 41))

        for (s in snow) {
            paint.color = s.color
            canvas.drawCircle(s.x, s.y, s.radius, paint)
        }

    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top; w = right - left
        // TODO заполнить массив снежинками со случайными координатами, радиусами и скоростями
        val r = Random(0)
        r.nextFloat() // генерирует число от 0 до 1

//        val mainWHue = Random.nextInt(0, 256)
        val mainWHue = Random(1)
//        val secWHue = Random(1)
//        secWHue.nextFloat()
        mainWHue.nextInt(0, 256)
        snow = Array(10) {
            Snowflake(
                x = r.nextFloat() * w,
                y = r.nextFloat() * h,
                velocity = 15 + 10 * r.nextFloat(),
                radius = 30 + 20 * r.nextFloat(),
                Color.rgb(mainWHue.nextInt(0, 256), mainWHue.nextInt(0, 256), mainWHue.nextInt(0, 256))
//                Color.WHITE
            ) // TODO: сделать снежинки разного оттенка
        }
//        Log.d("mytag", "snow: " + snow.contentToString())
    }

    fun moveSnowflakes() {
        // TODO: сделать падение с замедлением, к нижней границе экрана
        // скорость должна снижаться до нуля
        for (s in snow) {
            val constX :Float = s.x
            s.x += Random.nextInt(30, 60)
            s.x -= Random.nextInt(30, 60)

            if ((s.y).toInt() >= (h * 0.7).toInt()) { //Реализовано замедление на 10 единиц при прохождении снежинкой 0.7 высоты экрана
                s.y += s.velocity - 10
            } else if ((s.y).toInt() >= (h * 0.4).toInt()) { //Реализовано замедление на 5 единиц при прохождении снежинкой 0.4 высоты экрана
                s.y += s.velocity - 5
            } else {
                s.y += s.velocity
            }

            if (s.y > h) {
                s.y -= h // если снежинка покинула экран, вычитаем высоту
            }

            if (s.x > constX + 90) { // Проверка чтобы не уходили направо глубоко
                while (s.x > constX + 90) {
                    s.x -= Random.nextInt(30, 60)
                }
            } else if (s.x < constX - 90) { // Аналогичная проверка только уже на левую сторону
                while (s.x < constX - 90) {
                    s.x += Random.nextInt(30, 60)
                }
            }


        }
        invalidate() // перерисовать
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        moveSnowflakes()
        invalidate() // перерисовать View
        moveTask = MoveTask(this)
        moveTask.execute(100)
        return false // защита от множественных срабатываний

    }
    class MoveTask(val s: Snowflakes) : AsyncTask<Int, Int, Int>() {
        // https://developer.alexanderklimov.ru/android/theory/asynctask.php
        override fun doInBackground(vararg params: Int?): Int {
            val delay = params[0] ?: 200 // если задерка не задана
            while (true) {
                Thread.sleep(delay.toLong())
                s.moveSnowflakes()
            }
            return 0
        }
    }
}