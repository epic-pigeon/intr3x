package intr3x.engine

import java.awt.Color

class Engine3D(h: Int, w: Int) : Engine(h, w) {
    override fun onCreate() {

    }

    override fun onStart() {

    }

    private var oddUpdate: Boolean = false
    private var counter: Int = 0

    override fun onUpdate(timeElapsed: Double) {
        counter++
        Thread.sleep(10)
        logger.info("Update called, FPS: ${1 / timeElapsed}")
        graphics.color = //if (oddUpdate) Color.BLUE else Color.RED
                Color.GREEN
        graphics.fillRect(0, 0, 1000, 600)
        graphics.drawLine(1000, 600, 1000 + counter * 2, 600 + counter * 2)
        oddUpdate = !oddUpdate
    }
}
