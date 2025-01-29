package com.example.snake

import android.graphics.Canvas
import android.graphics.Paint

// Clase abstracta Figura para representar diferentes formas
abstract class Figura(
    var color: Int,
    var strokeWidth: Float
) {
    abstract fun dibujar(canvas: Canvas, paint: Paint)
}

// Clase Cuadrado que hereda de Figura
class Cuadrado(
    color: Int,
    private val size: Float,
    var x: Float,
    var y: Float,
    strokeWidth: Float
) : Figura(color, strokeWidth) {

    override fun dibujar(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawRect(x, y, x + size, y + size, paint)
    }

    // Método para detectar colisión con otro cuadrado
    fun colisionaCon(otro: Cuadrado): Boolean {
        return x < otro.x + otro.size &&
                x + size > otro.x &&
                y < otro.y + otro.size &&
                y + size > otro.y
    }
}
