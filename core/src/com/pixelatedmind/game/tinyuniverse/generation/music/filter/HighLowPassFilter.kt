package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import kotlin.math.sqrt

class HighLowPassFilter(var frequency : Float, var sampleRate:Int, var passType: PassType,
                        resonanceInput : Float = 1f) {
    private var c : Float = 0f
    private var a1 : Float = 0f
    private var a2 : Float = 0f
    private var a3 : Float = 0f
    private var b1 : Float = 0f
    private var b2 : Float = 0f
    private var resonance : Float = 0f
    private val inputHistory = floatArrayOf(0f, 0f)
    private val outputHistory = floatArrayOf(0f, 0f, 0f)

    init{
        setResonance(resonanceInput)
        initParams()
    }

    @JvmName("setFrequency1")
    fun setFrequency(frequency : Float){
        this.frequency = frequency
        initParams()
    }

    fun setResonance(resonanceNormalized : Float){
        this.resonance = sqrt(2.0).toFloat() * resonanceNormalized
        initParams()
    }

    @JvmName("setSampleRate1")
    fun setSampleRate(sampleRate : Int){
        this.sampleRate = sampleRate
        initParams()
    }

    @JvmName("setPassType1")
    fun setPassType(passType : PassType){
        this.passType = passType
        initParams()
    }

    fun initParams(){
        inputHistory.forEachIndexed{index,value->inputHistory[index] = 0f}
        outputHistory.forEachIndexed{index,value->outputHistory[index] = 0f}
        when (passType)
        {
            PassType.Low-> {
                c = (1.0f / Math.tan(Math.PI * frequency / sampleRate)).toFloat();
                a1 = 1.0f / (1.0f + resonance * c + c * c)
                a2 = 2f * a1
                a3 = a1
                b1 = 2.0f * (1.0f - c * c) * a1
                b2 = (1.0f - resonance * c + c * c) * a1
            }
            PassType.High-> {
                c = Math.tan (Math.PI * frequency / sampleRate).toFloat()
                a1 = 1.0f / (1.0f + resonance * c + c * c)
                a2 = -2f * a1
                a3 = a1
                b1 = 2.0f * (c * c - 1.0f) * a1
                b2 = (1.0f - resonance * c + c * c) * a1
            }
        }
    }

    fun filter(newInput : Float) : Float
    {
        val newOutput = a1 * newInput + a2 * inputHistory[0] + a3 * inputHistory[1] - b1 * outputHistory[0] - b2 * outputHistory[1]

        this.inputHistory[1] = inputHistory[0]
        this.inputHistory[0] = newInput

        this.outputHistory[2] = outputHistory[1]
        this.outputHistory[1] = outputHistory[0]
        this.outputHistory[0] = newOutput
        return newOutput
    }

    fun getValue() : Float
    {
        return this.outputHistory[0]
    }
}

//public class FilterButterworth
//{
//    /// <summary>
//    /// rez amount, from sqrt(2) to ~ 0.1
//    /// </summary>
//    private readonly float resonance;
//
//    private readonly float frequency;
//    private readonly int sampleRate;
//    private readonly PassType passType;
//
//    private readonly float c, a1, a2, a3, b1, b2;
//
//    /// <summary>
//    /// Array of input values, latest are in front
//    /// </summary>
//    private float[] inputHistory = new float[2];
//
//    /// <summary>
//    /// Array of output values, latest are in front
//    /// </summary>
//    private float[] outputHistory = new float[3];
//
//    public FilterButterworth(float frequency, int sampleRate, PassType passType, float resonance)
//    {
//        this.resonance = resonance;
//        this.frequency = frequency;
//        this.sampleRate = sampleRate;
//        this.passType = passType;
//
//        switch (passType)
//        {
//            case PassType.Lowpass:
//            c = 1.0f / (float)Math.Tan(Math.PI * frequency / sampleRate);
//            a1 = 1.0f / (1.0f + resonance * c + c * c);
//            a2 = 2f * a1;
//            a3 = a1;
//            b1 = 2.0f * (1.0f - c * c) * a1;
//            b2 = (1.0f - resonance * c + c * c) * a1;
//            break;
//            case PassType.Highpass:
//            c = (float)Math.Tan(Math.PI * frequency / sampleRate);
//            a1 = 1.0f / (1.0f + resonance * c + c * c);
//            a2 = -2f * a1;
//            a3 = a1;
//            b1 = 2.0f * (c * c - 1.0f) * a1;
//            b2 = (1.0f - resonance * c + c * c) * a1;
//            break;
//        }
//    }
//
//    public enum PassType
//    {
//        Highpass,
//        Lowpass,
//    }
//
//    public void Update(float newInput)
//    {
//        float newOutput = a1 * newInput + a2 * this.inputHistory[0] + a3 * this.inputHistory[1] - b1 * this.outputHistory[0] - b2 * this.outputHistory[1];
//
//        this.inputHistory[1] = this.inputHistory[0];
//        this.inputHistory[0] = newInput;
//
//        this.outputHistory[2] = this.outputHistory[1];
//        this.outputHistory[1] = this.outputHistory[0];
//        this.outputHistory[0] = newOutput;
//    }
//
//    public float Value
//    {
//        get { return this.outputHistory[0]; }
//    }
//}