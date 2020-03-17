package intr3x.math

import kotlin.math.*

data class Vector(val x: Double, val y: Double, val z: Double) {
    operator fun  plus(other: Vector): Vector = Vector(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector): Vector = Vector(x - other.x, y - other.y, z - other.z)
    operator fun unaryMinus(): Vector = Vector(-x, -y, -z)
    operator fun times(matrix: Matrix): Vector {
        var newX: Double = x * matrix[0][0] + y * matrix[1][0] + z * matrix[2][0] + matrix[3][0]
        var newY: Double = x * matrix[0][1] + y * matrix[1][1] + z * matrix[2][1] + matrix[3][1]
        var newZ: Double = x * matrix[0][2] + y * matrix[1][2] + z * matrix[2][2] + matrix[3][2]
        val newW: Double = x * matrix[0][3] + y * matrix[1][3] + z * matrix[2][3] + matrix[3][3]

        if (newW != 0.0) {
            newX /= newW
            newY /= newW
            newZ /= newW
        }
        return Vector(newX, newY, newZ)
    }

    infix fun crossProduct(other: Vector): Vector = Vector(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    infix fun dotProduct(other: Vector): Double = x * other.x + y * other.y + z * other.z

    val length: Double
        get() = sqrt(x*x + y*y + z*z)

    infix fun normalizeToLength(length: Double): Vector {
        val factor = length / this.length
        return Vector(
            x * factor,
            y * factor,
            z * factor
        )
    }
}

data class Triangle(val vector1: Vector, val vector2: Vector, val vector3: Vector) {
    operator fun times(matrix: Matrix): Triangle {
        return Triangle(vector1 * matrix, vector2 * matrix, vector3 * matrix)
    }
    fun transform(func: (Vector) -> Vector): Triangle {
        return Triangle(func(vector1), func(vector2), func(vector3))
    }
}

data class Mesh(val triangles: List<Triangle>)

class Matrix private constructor(val value: List<List<Double>>) {
    companion object {
        fun createProjectionMatrix(zNear: Double, zFar: Double, fov: Double, aspectRatio: Double): Matrix {
            val fovRad: Double = 1.0 / tan(fov / 2 / 180 * PI)
            val q: Double = zFar / (zFar - zNear)
            val value = listOf(
                listOf(aspectRatio * fovRad,    0.0,        0.0, 0.0),
                listOf(                 0.0, fovRad,        0.0, 0.0),
                listOf(                 0.0,    0.0,          q, 1.0),
                listOf(                 0.0,    0.0, -zNear * q, 0.0)
            )
            return Matrix(value)
        }
        fun createXRotationMatrix(radians: Double): Matrix {
            val value = listOf(
                listOf(1.0,           0.0,          0.0, 0.0),
                listOf(0.0,  cos(radians), sin(radians), 0.0),
                listOf(0.0, -sin(radians), cos(radians), 0.0),
                listOf(0.0,           0.0,          0.0, 1.0)
            )
            return Matrix(value)
        }
        fun createZRotationMatrix(radians: Double): Matrix {
            val value = listOf(
                listOf( cos(radians), sin(radians), 0.0, 0.0),
                listOf(-sin(radians), cos(radians), 0.0, 0.0),
                listOf(          0.0,          0.0, 1.0, 0.0),
                listOf(          0.0,          0.0, 0.0, 1.0)
            )
            return Matrix(value)
        }
        fun createYRotationMatrix(radians: Double): Matrix {
            val value = listOf(
                listOf(cos(radians),          0.0, -sin(radians), 0.0),
                listOf(         0.0,          1.0,           0.0, 0.0),
                listOf(sin(radians),          0.0,  cos(radians), 0.0),
                listOf(         0.0,          0.0,           0.0, 1.0)
            )
            return Matrix(value)
        }
    }

    operator fun get(index: Int): List<Double> {
        return value[index]
    }
}


