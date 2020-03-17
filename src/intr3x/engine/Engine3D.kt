package intr3x.engine

import intr3x.math.Matrix
import intr3x.math.Mesh
import intr3x.math.Triangle
import intr3x.math.Vector
import java.awt.BasicStroke
import java.awt.Color

class Engine3D(h: Int, w: Int) : Engine(h, w) {
    override fun onCreate() {

    }

    override fun onStart() {

    }
    private var counter: Int = 0
    private var totalTime: Double = 0.0

    private val cube: Mesh = Mesh(listOf(
        Triangle(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), Vector(1.0, 1.0, 0.0)),
        Triangle(Vector(0.0, 0.0, 0.0), Vector(1.0, 1.0, 0.0), Vector(1.0, 0.0, 0.0)),

        Triangle(Vector(1.0, 0.0, 0.0), Vector(1.0, 1.0, 0.0), Vector(1.0, 1.0, 1.0)),
        Triangle(Vector(1.0, 0.0, 0.0), Vector(1.0, 1.0, 1.0), Vector(1.0, 0.0, 1.0)),

        Triangle(Vector(1.0, 0.0, 1.0), Vector(1.0, 1.0, 1.0), Vector(0.0, 1.0, 1.0)),
        Triangle(Vector(1.0, 0.0, 1.0), Vector(0.0, 1.0, 1.0), Vector(0.0, 0.0, 1.0)),

        Triangle(Vector(0.0, 0.0, 1.0), Vector(0.0, 1.0, 1.0), Vector(0.0, 1.0, 0.0)),
        Triangle(Vector(0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0), Vector(0.0, 0.0, 0.0)),

        Triangle(Vector(0.0, 1.0, 0.0), Vector(0.0, 1.0, 1.0), Vector(1.0, 1.0, 1.0)),
        Triangle(Vector(0.0, 1.0, 0.0), Vector(1.0, 1.0, 1.0), Vector(1.0, 1.0, 0.0)),

        Triangle(Vector(1.0, 0.0, 1.0), Vector(0.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0)),
        Triangle(Vector(1.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0))
    ))

    private val matrix: Matrix = Matrix.createProjectionMatrix(0.1, 1000.0, 90.0, height.toDouble() / width)
    private var rotationX: Matrix = Matrix.createXRotationMatrix(0.0)
    private var rotationZ: Matrix = Matrix.createZRotationMatrix(0.0)

    override fun onUpdate(timeElapsed: Double) {
        totalTime += timeElapsed
        //Thread.sleep(10)
        logger.info("Update called, FPS: ${1 / timeElapsed}")
        graphics.color = Color.BLACK
        graphics.fillRect(0, 0, width, height)

        graphics.color = Color.WHITE
        cube.triangles.forEach { drawTriangle(it) }
        //drawTriangle(cube.triangles[0])
        if (totalTime > 0) {
            rotationX = Matrix.createXRotationMatrix(totalTime)
            rotationZ = Matrix.createZRotationMatrix(totalTime * 0.5)
        }
        counter++
    }

    private fun drawTriangle(triangle: Triangle) {
        val rotated = triangle * rotationX * rotationZ

        val translated = Triangle(
            Vector(
                rotated.vector1.x - 0.5,
                rotated.vector1.y - 0.5,
                rotated.vector1.z + 2.0
            ),
            Vector(
                rotated.vector2.x - 0.5,
                rotated.vector2.y - 0.5,
                rotated.vector2.z + 2.0
            ),
            Vector(
                rotated.vector3.x - 0.5,
                rotated.vector3.y - 0.5,
                rotated.vector3.z + 2.0
            )
        )
        val projected = translated * matrix
        val scaled = Triangle(
            Vector(
                (projected.vector1.x + 1) * 0.5 * width,
                (projected.vector1.y + 1) * 0.5 * height,
                projected.vector1.z
            ),
            Vector(
                (projected.vector2.x + 1) * 0.5 * width,
                (projected.vector2.y + 1) * 0.5 * height,
                projected.vector2.z
            ),
            Vector(
                (projected.vector3.x + 1) * 0.5 * width,
                (projected.vector3.y + 1) * 0.5 * height,
                projected.vector3.z
            )
        )
        graphics.stroke = BasicStroke(1.0F)
        //graphics.drawLine(scaled.vector1.x.toInt(), scaled.vector1.y.toInt(), scaled.vector2.x.toInt(), scaled.vector2.y.toInt())
        //graphics.drawLine(scaled.vector2.x.toInt(), scaled.vector2.y.toInt(), scaled.vector3.x.toInt(), scaled.vector3.y.toInt())
        //graphics.drawLine(scaled.vector1.x.toInt(), scaled.vector1.y.toInt(), scaled.vector3.x.toInt(), scaled.vector3.y.toInt())
        graphics.drawPolygon(
            intArrayOf(scaled.vector1.x.toInt(), scaled.vector2.x.toInt(), scaled.vector3.x.toInt()),
            intArrayOf(scaled.vector1.y.toInt(), scaled.vector2.y.toInt(), scaled.vector3.y.toInt()),
            3
        )
        logger.debug(triangle)
    }
}
