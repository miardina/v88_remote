
package com.example.v88remote

import android.app.Activity
import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    companion object {
        private const val IR_FREQUENCY = 38_000
        private const val NEC_ADDRESS = 0x01
        private const val NEC_POWER_COMMAND = 0x40
    }

    private var irManager: ConsumerIrManager? = null
    private lateinit var statusText: TextView
    private lateinit var powerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        irManager = getSystemService(
            Context.CONSUMER_IR_SERVICE
        ) as? ConsumerIrManager

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
            text = "Télécommande infrarouge - Version 1.0"
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        powerButton = Button(this).apply {
            text = "⏻ POWER"
            textSize = 22f
            isAllCaps = false
            setOnClickListener {
                sendPowerSignal()
            }
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
            statusText.text =
                "Service infrarouge indisponible."
            powerButton.isEnabled = false

        } else if (!manager.hasIrEmitter()) {
            statusText.text =
                "Aucun émetteur infrarouge détecté."
            powerButton.isEnabled = false

        } else {
            statusText.text =
                "Émetteur IR détecté. Oriente le haut du téléphone vers la V88."
            powerButton.isEnabled = true
        }
    }

    private fun sendPowerSignal() {
        val manager = irManager

        if (manager == null || !manager.hasIrEmitter()) {
            Toast.makeText(
                this,
                "Émetteur infrarouge indisponible",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val pattern = buildNecPattern(
                NEC_ADDRESS,
                NEC_POWER_COMMAND
            )

            manager.transmit(IR_FREQUENCY, pattern)

            statusText.text =
                "Signal envoyé. Vérifie si la V88 réagit."

        } catch (exception: Exception) {
            statusText.text =
                "Erreur : ${exception.message ?: "inconnue"}"
        }
    }

    private fun buildNecPattern(
        address: Int,
        command: Int
    ): IntArray {

        val timings = ArrayList<Int>(68)

        // En-tête NEC
        timings.add(9000)
        timings.add(4500)

        val bytes = intArrayOf(
            address and 0xFF,
            address.inv() and 0xFF,
            command and 0xFF,
            command.inv() and 0xFF
        )

        // Transmission des 32 bits, LSB first
        for (value in bytes) {
            for (bit in 0 until 8) {
                timings.add(560)

                timings.add(
                    if (((value shr bit) and 1) == 1) {
                        1690
                    } else {
                        560
                    }
                )
            }
        }

        // Bit final
        timings.add(560)

        return timings.toIntArray()
    }
}
