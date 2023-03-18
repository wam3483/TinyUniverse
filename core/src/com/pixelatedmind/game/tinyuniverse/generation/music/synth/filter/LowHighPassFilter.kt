package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

/***
 * Resonance ranges from .5 to 10.
 * Q = 0.5: Creates a very mild resonance effect that is barely noticeable, and does not significantly alter the overall sound of the filter.
 * Q = 1.0: Creates a moderate resonance effect that adds some brightness and presence to the sound, but still preserves the overall character of the filter.
 * Q = 2.0: Creates a more pronounced resonance effect that emphasizes the frequencies near the cutoff frequency, and can make the sound more "nasal" or "honky".
 * Q = 5.0: Creates a strong resonance effect that significantly alters the sound of the filter, and can make it sound "ringing" or "screaming".
 * Q = 10.0: Creates an extremely strong resonance effect that can cause the filter to become unstable or produce feedback-like sounds.
 */
class LowHighPassFilter(var sampleRate : Double, var cutoffFrequency : Double, var resonance : Double, var lowPass : Boolean) {

    private var previousLowPassSample = 0.0

    private var currentHighPassSample = 0.0
    private var previousHighPassSample = 0.0

    fun reset() {
        previousLowPassSample = 0.0

        currentHighPassSample = 0.0
        previousHighPassSample = 0.0
    }

    fun filter(sample : Float) : Float{
        return filter(sample.toDouble()).toFloat()
    }
    fun filter(sample : Double) : Double{
        // Define filter parameters
        val deltaTime = 1.0 / sampleRate // Time step
        val RC = 1.0 / (2 * Math.PI * cutoffFrequency) // Filter time constant

        // Low pass filter
        if(lowPass) {
            val alphaLP: Double = deltaTime / (RC + deltaTime)
            val betaLP: Double = resonance * alphaLP
            val currentLowPassSample = alphaLP * sample + (1 - alphaLP) * (previousLowPassSample + betaLP * (sample - previousLowPassSample) - betaLP * previousLowPassSample)
            previousLowPassSample = currentLowPassSample
            return currentLowPassSample
        }
        // High pass filter
        else {
            val alphaHP: Double = RC / (RC + deltaTime)
            val betaHP: Double = resonance * alphaHP
            currentHighPassSample = alphaHP * (currentHighPassSample + sample - previousHighPassSample)
            previousHighPassSample = currentHighPassSample - betaHP * (currentHighPassSample - previousHighPassSample)
            return currentHighPassSample
        }
    }
}