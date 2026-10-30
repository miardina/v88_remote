package com.example.v88remote

import android.app.Activity
import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private var irManager: ConsumerIrManager? = null
    private lateinit var statusText: TextView
    private lateinit var powerButton: Button

    // Profil NEC provisoire à tester : adresse 0x01, commande 0x40.
    // Ce code n'est pas confirmé pour toutes les versions V88 4K.
    private const val IR_FREQUENCY = 38_000
    private const val NEC_ADDRESS = 0x01
    private const val NEC_POWER_COMMAND = 0x40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        irManager = getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        buildInterface()
        updateIrStatus()
    }

    private fun buildInterface() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 40, 32, 40)
        }

        val title = TextView(this).apply {
            text = "V88 Remote"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Télécommande infrarouge – version 1.0"
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        powerButton = Button(this).apply {
            text = "⏻  POWER"
            textSize = 22f
            isAllCaps = false
            setOnClickListener { sendPowerSignal() }
        }

        statusText = TextView(this).apply {
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        }

        container.addView(title)
        container.addView(subtitle)
        container.addView(
            powerButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                72
            )
        )
        container.addView(statusText)
        setContentView(container)
    }

    private fun updateIrStatus() {
        val manager = irManager
        if (manager == null) {
            statusText.text = "Service infrarouge indisponible sur ce téléphone."
            powerButton.isEnabled = false
        } else if (!manager.hasIrEmitter()) {
            statusText.text = "Aucun émetteur infrarouge détecté."
            powerButton.isEnabled = false
        } else {
            statusText.text = "Émetteur IR détecté. Oriente le haut du téléphone vers la V88."
            powerButton.isEnabled = true
        }
    }

    private fun sendPowerSignal() {
        val manager = irManager
        if (manager == null || !manager.hasIrEmitter()) {
            Toast.makeText(this, "Émetteur infrarouge indisponible", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val pattern = buildNecPattern(NEC_ADDRESS, NEC_POWER_COMMAND)
            manager.transmit(IR_FREQUENCY, pattern)
            statusText.text =
                "Signal envoyé (NEC 38 kHz, adresse 0x01, commande 0x40). " +
                "Vérifie si la V88 réagit."
        } catch (exception: Exception) {
            statusText.text = "Erreur lors de l'envoi : ${exception.message ?: "inconnue"}"
        }
    }

    /**
     * Construit une trame NEC standard : leader, 32 bits LSB-first et bit final.
     * Les durées sont exprimées en microsecondes.
     */
    private fun buildNecPattern(address: Int, command: Int): IntArray {
        val timings = ArrayList<Int>(68)
        timings.add(9_000)
        timings.add(4_500)

        val bytes = intArrayOf(
            address and 0xFF,
            address.inv() and 0xFF,
            command and 0xFF,
            command.inv() and 0xFF
        )

        for (value in bytes) {
            for (bit in 0 until 8) {
                timings.add(560)
                timings.add(if (((value shr bit) and 1) == 1) 1_690 else 560)
            }
        }

        timings.add(560)
        return timings.toIntArray()
    }
}
