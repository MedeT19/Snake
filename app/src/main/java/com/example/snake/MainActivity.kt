package com.example.snake

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.snake.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var image: ImageView

    // Variables para el canvas
    private var bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
    private var canvas = Canvas(bitmap)
    private var paint = Paint()

    // Variables del juego
    private var segmentSize = 20f
    private var snakeSegments = mutableListOf(Cuadrado(Color.BLACK, segmentSize, 200f, 200f, 5f))
    private var direction = Pair(1f, 0f) // Dirección inicial: derecha
    private var manzana = Cuadrado(Color.RED, segmentSize, Random.nextFloat() * (400 - segmentSize), Random.nextFloat() * (400 - segmentSize), 5f)
    private var gameRunning = true
    private var score = 0 // Variable para el puntaje


    // Handler para movimiento automático
    private val handler = Handler(Looper.getMainLooper())
    private val gameLoop = object : Runnable {
        override fun run() {
            if (gameRunning) {
                moverAutomatico()
                handler.postDelayed(this, 300) // Ejecuta cada 300 ms
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        image = findViewById(R.id.ivLienzo)

        // Configuración inicial del canvas
        canvas.drawColor(Color.GRAY)
        paint.isAntiAlias = true
        image.setImageBitmap(bitmap)

        // Inicializa el puntaje
        actualizarScore()

        // Dibuja el estado inicial
        actualizarCanvas()

        // Inicia el movimiento automático
        handler.post(gameLoop)
    }

    fun cambiarDireccion(view: View) {
        when (view.id) {
            R.id.btnArriba -> if (direction.second != 1f) direction = Pair(0f, -1f) // No puede ir hacia abajo
            R.id.btnAbajo -> if (direction.second != -1f) direction = Pair(0f, 1f) // No puede ir hacia arriba
            R.id.btnIzquierda -> if (direction.first != 1f) direction = Pair(-1f, 0f) // No puede ir hacia la derecha
            R.id.btnDerecha -> if (direction.first != -1f) direction = Pair(1f, 0f) // No puede ir hacia la izquierda
        }
    }

    @RequiresApi(35)
    private fun moverAutomatico() {
        val cabeza = snakeSegments.first()
        val newHeadX = cabeza.x + direction.first * segmentSize
        val newHeadY = cabeza.y + direction.second * segmentSize

        // Verifica colisión con bordes
        if (newHeadX < 0 || newHeadY < 0 || newHeadX >= 400 || newHeadY >= 400) {
            gameOver()
            return
        }

        // Verifica colisión con el cuerpo
        for (segment in snakeSegments) {
            if (segment.x == newHeadX && segment.y == newHeadY) {
                gameOver()
                return
            }
        }

        // Verifica colisión con la manzana
        val newHead = Cuadrado(Color.BLACK, segmentSize, newHeadX, newHeadY, 5f)
        if (newHead.colisionaCon(manzana)) {
            snakeSegments.add(0, newHead) // Agrega nuevo segmento
            manzana.x = Random.nextFloat() * (400 - segmentSize)
            manzana.y = Random.nextFloat() * (400 - segmentSize)
            // Incrementa el puntaje y actualiza la interfaz
            score++
            actualizarScore()

        } else {
            // Mueve la viborita desplazando los segmentos
            snakeSegments.add(0, newHead)
            snakeSegments.removeLast()
        }

        // Redibuja el canvas
        actualizarCanvas()
    }

    private fun actualizarScore() {
        binding.textViewScore.text = getString(R.string.score_label, score)
    }

    private fun gameOver() {
        gameRunning = false
        binding.textViewGameOver.visibility = View.VISIBLE
    }

    fun reiniciar(view: View) {
        // Reinicia las variables del juego
        snakeSegments.clear()
        snakeSegments.add(Cuadrado(Color.BLACK, segmentSize, 200f, 200f, 5f))
        direction = Pair(1f, 0f)
        manzana.x = Random.nextFloat() * (400 - segmentSize)
        manzana.y = Random.nextFloat() * (400 - segmentSize)
        gameRunning = true
        score = 0 // Reinicia el puntaje
        actualizarScore()
        binding.textViewGameOver.visibility = View.GONE

        // Limpia y redibuja el canvas
        actualizarCanvas()

        // Reinicia el loop del juego
        handler.post(gameLoop)
    }

    private fun actualizarCanvas() {
        canvas.drawColor(Color.GRAY)
        for (segment in snakeSegments) {
            segment.dibujar(canvas, paint)
        }
        manzana.dibujar(canvas, paint)
        image.setImageBitmap(bitmap)
    }
}

