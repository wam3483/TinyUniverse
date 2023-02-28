package com.pixelatedmind.game.tinyuniverse.generation.music.filter

class BiQuadFilter() {
    // coefficients
    private var a0: Double = 0.0
    private var a1: Double = 0.0
    private var a2: Double = 0.0
    private var a3: Double = 0.0
    private var a4: Double = 0.0

    // state
    private var x1: Float
    private var x2: Float
    private var y1: Float
    private var y2: Float

    /// <summary>
    /// Passes a single sample through the filter
    /// </summary>
    /// <param name="inSample">Input sample</param>
    /// <returns>Output sample</returns>
    fun Transform(inSample : Float) : Float
    {
        // compute result
        var result = a0 * inSample + a1 * x1 + a2 * x2 - a3 * y1 - a4 * y2

        // shift x1 to x2, sample to x1
        x2 = x1
        x1 = inSample

        // shift y1 to y2, result to y1
        y2 = y1
        y1 = result.toFloat()

        return y1
    }

    fun SetCoefficients(aa0:Double , aa1:Double, aa2:Double, b0:Double, b1:Double, b2:Double)
    {
        // precompute the coefficients
        a0 = b0/aa0
        a1 = b1/aa0
        a2 = b2/aa0
        a3 = aa1/aa0
        a4 = aa2/aa0
    }

    /// <summary>
    /// Set this up as a low pass filter
    /// </summary>
    /// <param name="sampleRate">Sample Rate</param>
    /// <param name="cutoffFrequency">Cut-off Frequency</param>
    /// <param name="q">Bandwidth</param>
    fun SetLowPassFilter(sampleRate:Float, cutoffFrequency:Float, q:Float)
    {
        // H(s) = 1 / (s^2 + s/Q + 1)
        var w0 = 2 * Math.PI * cutoffFrequency / sampleRate
        var cosw0 = Math.cos(w0)
        var alpha = Math.sin(w0) / (2 * q)

        var b0 = (1 - cosw0) / 2
        var b1 = 1 - cosw0
        var b2 = (1 - cosw0) / 2
        var aa0 = 1 + alpha
        var aa1 = -2 * cosw0
        var aa2 = 1 - alpha
        SetCoefficients(aa0,aa1,aa2,b0,b1,b2)
    }

    /// <summary>
    /// Set this up as a peaking EQ
    /// </summary>
    /// <param name="sampleRate">Sample Rate</param>
    /// <param name="centreFrequency">Centre Frequency</param>
    /// <param name="q">Bandwidth (Q)</param>
    /// <param name="dbGain">Gain in decibels</param>
    fun SetPeakingEq(sampleRate:Float, centreFrequency:Float, q:Float, dbGain:Float)
    {
        // H(s) = (s^2 + s*(A/Q) + 1) / (s^2 + s/(A*Q) + 1)
        var w0 = 2 * Math.PI * centreFrequency / sampleRate
        var cosw0 = Math.cos(w0)
        var sinw0 = Math.sin(w0)
        var alpha = sinw0 / (2 * q)
        var a = Math.pow(10.0, dbGain / 40.0)     // TODO: should we square root this value?

        var b0 = 1 + alpha * a
        var b1 = -2 * cosw0
        var b2 = 1 - alpha * a
        var aa0 = 1 + alpha / a
        var aa1 = -2 * cosw0
        var aa2 = 1 - alpha / a
        SetCoefficients(aa0, aa1, aa2, b0, b1, b2)
    }

    /// <summary>
    /// Set this as a high pass filter
    /// </summary>
    fun SetHighPassFilter(sampleRate:Float, cutoffFrequency:Float, q:Float)
    {
        // H(s) = s^2 / (s^2 + s/Q + 1)
        var w0 = 2 * Math.PI * cutoffFrequency / sampleRate
        var cosw0 = Math.cos(w0)
        var alpha = Math.sin(w0) / (2 * q)

        var b0 = (1 + cosw0) / 2
        var b1 = -(1 + cosw0)
        var b2 = (1 + cosw0) / 2
        var aa0 = 1 + alpha
        var aa1 = -2 * cosw0
        var aa2 = 1 - alpha
        SetCoefficients(aa0, aa1, aa2, b0, b1, b2)
    }
    companion object {
        /// <summary>
        /// Create a low pass filter
        /// </summary>
        fun LowPassFilter(sampleRate:Float, cutoffFrequency:Float, q:Float) : BiQuadFilter
        {
            var filter = BiQuadFilter()
            filter.SetLowPassFilter(sampleRate, cutoffFrequency, q)
            return filter
        }

        /// <summary>
        /// Create a High pass filter
        /// </summary>
        fun  HighPassFilter(sampleRate:Float, cutoffFrequency:Float, q:Float):BiQuadFilter
        {
            var filter = BiQuadFilter ()
            filter.SetHighPassFilter(sampleRate, cutoffFrequency, q)
            return filter
        }

        /// <summary>
        /// Create a bandpass filter with constant skirt gain
        /// </summary>
        fun BandPassFilterConstantSkirtGain(sampleRate:Float, centreFrequency:Float, q:Float):BiQuadFilter
        {
            // H(s) = s / (s^2 + s/Q + 1)  (constant skirt gain, peak gain = Q)
            var w0 = 2 * Math.PI * centreFrequency / sampleRate
            var cosw0 = Math.cos(w0)
            var sinw0 = Math.sin(w0)
            var alpha = sinw0 / (2 * q)

            var b0 = sinw0 / 2 // =   Q*alpha
            var b1 = 0.0
            var b2 = -sinw0 / 2 // =  -Q*alpha
            var a0 = 1 + alpha
            var a1 = -2 * cosw0
            var a2 = 1 - alpha
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }

        /// <summary>
        /// Create a bandpass filter with constant peak gain
        /// </summary>
        fun BandPassFilterConstantPeakGain(sampleRate:Float, centreFrequency:Float, q:Float):BiQuadFilter
        {
            // H(s) = (s/Q) / (s^2 + s/Q + 1)      (constant 0 dB peak gain)
            var w0 = 2 * Math.PI * centreFrequency / sampleRate
            var cosw0 = Math.cos(w0)
            var sinw0 = Math.sin(w0)
            var alpha = sinw0 / (2 * q)

            var b0 = alpha
            var b1 = 0.0
            var b2 = -alpha
            var a0 = 1 + alpha
            var a1 = -2 * cosw0
            var a2 = 1 - alpha
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }

        /// <summary>
        /// Creates a notch filter
        /// </summary>
        fun NotchFilter(sampleRate:Float, centreFrequency:Float, q:Float):BiQuadFilter
        {
            // H(s) = (s^2 + 1) / (s^2 + s/Q + 1)
            var w0 = 2 * Math.PI * centreFrequency / sampleRate
            var cosw0 = Math.cos(w0)
            var sinw0 = Math.sin(w0)
            var alpha = sinw0 / (2 * q)

            var b0 = 1.0
            var b1 = -2 * cosw0
            var b2 = 1.0
            var a0 = 1 + alpha
            var a1 = -2 * cosw0
            var a2 = 1 - alpha
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }

        /// <summary>
        /// Creaes an all pass filter
        /// </summary>
        fun AllPassFilter(sampleRate:Float, centreFrequency:Float, q:Float):BiQuadFilter
        {
            //H(s) = (s^2 - s/Q + 1) / (s^2 + s/Q + 1)
            val w0 = 2 * Math.PI * centreFrequency / sampleRate
            val cosw0 = Math.cos(w0)
            val sinw0 = Math.sin(w0)
            val alpha = sinw0 / (2 * q)

            val b0 = 1 - alpha
            val b1 = -2 * cosw0
            val b2 = 1 + alpha
            val a0 = 1 + alpha
            val a1 = -2 * cosw0
            val a2 = 1 - alpha
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }

        /// <summary>
        /// Create a Peaking EQ
        /// </summary>
        fun PeakingEQ(sampleRate:Float, centreFrequency:Float, q:Float, dbGain:Float):BiQuadFilter
        {
            var filter = BiQuadFilter()
            filter.SetPeakingEq(sampleRate, centreFrequency, q, dbGain)
            return filter
        }

        /// <summary>
        /// H(s) = A * (s^2 + (sqrt(A)/Q)*s + A)/(A*s^2 + (sqrt(A)/Q)*s + 1)
        /// </summary>
        /// <param name="sampleRate"></param>
        /// <param name="cutoffFrequency"></param>
        /// <param name="shelfSlope">a "shelf slope" parameter (for shelving EQ only).
        /// When S = 1, the shelf slope is as steep as it can be and remain monotonically
        /// increasing or decreasing gain with frequency.  The shelf slope, in dB/octave,
        /// remains proportional to S for all other values for a fixed f0/Fs and dBgain.</param>
        /// <param name="dbGain">Gain in decibels</param>
        fun LowShelf(sampleRate:Float, cutoffFrequency:Float, shelfSlope:Float, dbGain:Float):BiQuadFilter
        {
            var w0 = 2 * Math.PI * cutoffFrequency / sampleRate
            var cosw0 = Math.cos(w0)
            var sinw0 = Math.sin(w0)
            var a = Math.pow(10.0, dbGain / 40.0)    // TODO: should we square root this value?
            var alpha = sinw0 / 2 * Math.sqrt((a + 1 / a) * (1 / shelfSlope - 1) + 2)
            var temp = 2 * Math.sqrt(a) * alpha

            var b0 = a * ((a + 1) - (a - 1) * cosw0 + temp)
            var b1 = 2 * a * ((a - 1) - (a + 1) * cosw0)
            var b2 = a * ((a + 1) - (a - 1) * cosw0 - temp)
            var a0 = (a + 1) + (a - 1) * cosw0 + temp
            var a1 = -2 * ((a - 1) + (a + 1) * cosw0)
            var a2 = (a + 1) + (a - 1) * cosw0 - temp
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }

        /// <summary>
        /// H(s) = A * (A*s^2 + (sqrt(A)/Q)*s + 1)/(s^2 + (sqrt(A)/Q)*s + A)
        /// </summary>
        /// <param name="sampleRate"></param>
        /// <param name="cutoffFrequency"></param>
        /// <param name="shelfSlope"></param>
        /// <param name="dbGain"></param>
        /// <returns></returns>
        fun HighShelf(sampleRate:Float, cutoffFrequency:Float, shelfSlope:Float, dbGain:Float) : BiQuadFilter
        {
            var w0 = 2 * Math.PI * cutoffFrequency / sampleRate
            var cosw0 = Math.cos(w0)
            var sinw0 = Math.sin(w0)
            var a = Math.pow(10.0, dbGain / 40.0)     // TODO: should we square root this value?
            var alpha = sinw0 / 2 * Math.sqrt((a + 1 / a) * (1 / shelfSlope - 1) + 2)
            var temp = 2 * Math.sqrt(a) * alpha

            var b0 = a * ((a + 1) + (a - 1) * cosw0 + temp)
            var b1 = -2 * a * ((a - 1) + (a + 1) * cosw0)
            var b2 = a * ((a + 1) + (a - 1) * cosw0 - temp)
            var a0 = (a + 1) - (a - 1) * cosw0 + temp
            var a1 = 2 * ((a - 1) - (a + 1) * cosw0)
            var a2 = (a + 1) - (a - 1) * cosw0 - temp
            return BiQuadFilter (a0, a1, a2, b0, b1, b2)
        }
    }

    init
    {
        // zero initial samples
        x1 = 0f
        x2 = 0f
        y1 = 0f
        y2 = 0f
    }

    private constructor(a0:Double, a1:Double, a2:Double, b0:Double, b1:Double, b2:Double) : this() {
        SetCoefficients(a0,a1,a2,b0,b1,b2);

        // zero initial samples
        x1 = 0f
        x2 = 0f
        y1 = 0f
        y2 = 0f
    }
}