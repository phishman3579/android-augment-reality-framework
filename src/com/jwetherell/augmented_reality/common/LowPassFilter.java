package com.jwetherell.augmented_reality.common;

/**
 * This class implements a low-pass filter. A low-pass filter is an electronic filter that passes low-frequency signals 
 * but attenuates (reduces the amplitude of) signals with frequencies higher than the cutoff frequency. The actual amount 
 * of attenuation for each frequency varies from filter to filter. It is sometimes called a high-cut filter, or treble cut 
 * filter when used in audio applications.
 * 
 * @author Justin Wetherell (phishman3579@gmail.com)
 */
public class LowPassFilter {
    
    /*
     * Time smoothing constant for low-pass filter
     * 0 ≤ α ≤ 1 ; a smaller value basically means more smoothing
     * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    private static final float ALPHA = 0.333f;
    
    private LowPassFilter() { }
    
    /**
     * Filter the given input against the previous values and return a low-pass filtered result.
     * 
     * @param input float array to smooth.
     * @param prev float array representing the previous values.
     * @return float array smoothed with a low-pass filter.
     */
    public static float[] filter(float[] input, float[] prev) {
        if (input==null || prev==null) 
            throw new NullPointerException("input and prev float arrays must be non-NULL");
        if (input.length!=prev.length) 
            throw new IllegalArgumentException("input and prev must be the same length");

        for ( int i=0; i<input.length; i++ ) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
        }
        return prev;
    }
}
