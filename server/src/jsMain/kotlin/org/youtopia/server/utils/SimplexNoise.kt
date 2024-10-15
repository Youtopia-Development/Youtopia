package org.youtopia.server.utils

import kotlin.math.sqrt

// I forgot where I stole this from
object SimplexNoise {
    // Simplex noise in 2D, 3D and 4D
    private val grad3 = arrayOf(
        Grad(1.0, 1.0), Grad(-1.0, 1.0), Grad(1.0, -1.0), Grad(-1.0, -1.0),
        Grad(1.0, 0.0), Grad(-1.0, 0.0), Grad(1.0, 0.0), Grad(-1.0, 0.0),
        Grad(0.0, 1.0), Grad(0.0, -1.0), Grad(0.0, 1.0), Grad(0.0, -1.0),
    )

    private val p = (0..<256).map { it.toShort() }.shuffled().toShortArray()

    // To remove the need for index wrapping, double the permutation table length
    private val perm = ShortArray(512)
    private val permMod12 = ShortArray(512)

    init {
        for (i in 0..511) {
            perm[i] = p[i and 255]
            permMod12[i] = (perm[i] % 12).toShort()
        }
    }

    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    private val F2 = 0.5 * (sqrt(3.0) - 1.0)
    private val G2 = (3.0 - sqrt(3.0)) / 6.0

    private fun fastFloor(x: Double): Int {
        val xi = x.toInt()
        return if (x < xi) xi - 1 else xi
    }

    private fun dot(g: Grad, x: Double, y: Double): Double {
        return g.x * x + g.y * y
    }

    // 2D simplex noise
    fun noise(xin: Double, yin: Double): Double {
        val n0: Double
        val n1: Double
        val n2: Double // Noise contributions from the three corners
        // Skew the input space to determine which simplex cell we're in
        val s = (xin + yin) * F2 // Hairy factor for 2D
        val i = fastFloor(xin + s)
        val j = fastFloor(yin + s)
        val t = (i + j) * G2
        val x0a = i - t // Unskew the cell origin back to (x,y) space
        val y0a = j - t
        val x0 = xin - x0a // The x,y distances from the cell origin
        val y0 = yin - y0a
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        val i1: Int
        val j1: Int // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        else {
            i1 = 0
            j1 = 1
        } // upper triangle, YX order: (0,0)->(0,1)->(1,1)

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6
        val x1 = x0 - i1 + G2 // Offsets for middle corner in (x,y) unskewed coords
        val y1 = y0 - j1 + G2
        val x2 = x0 - 1.0 + 2.0 * G2 // Offsets for last corner in (x,y) unskewed coords
        val y2 = y0 - 1.0 + 2.0 * G2
        // Work out the hashed gradient indices of the three simplex corners
        val ii = i and 255
        val jj = j and 255
        val gi0 = permMod12[ii + perm[jj]].toInt()
        val gi1 = permMod12[ii + i1 + perm[jj + j1]].toInt()
        val gi2 = permMod12[ii + 1 + perm[jj + 1]].toInt()
        // Calculate the contribution from the three corners
        var t0 = 0.5 - x0 * x0 - y0 * y0
        if (t0 < 0) n0 = 0.0
        else {
            t0 *= t0
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0) // (x,y) of grad3 used for 2D gradient
        }
        var t1 = 0.5 - x1 * x1 - y1 * y1
        if (t1 < 0) n1 = 0.0
        else {
            t1 *= t1
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1)
        }
        var t2 = 0.5 - x2 * x2 - y2 * y2
        if (t2 < 0) n2 = 0.0
        else {
            t2 *= t2
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2)
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0 * (n0 + n1 + n2)
    }

    private data class Grad(
        val x: Double,
        val y: Double,
    )
}
