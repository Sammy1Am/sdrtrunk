/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dsheirer.source.tuner.sdrplay;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ShortByReference;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableBufferBroadcaster;
import io.github.dsheirer.sample.buffer.ReusableComplexBuffer;
import io.github.dsheirer.sample.buffer.ReusableComplexBufferQueue;
import io.github.dsheirer.source.SourceException;
import io.github.dsheirer.source.tuner.TunerController;
import io.github.dsheirer.source.tuner.configuration.TunerConfiguration;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_DeviceT;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_EventCallback_t;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_StreamCallback_t;
import io.github.sammy1am.sdrplay.api.StreamConsumer;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sam
 */
public class SDRPlayTunerController extends TunerController {
    private final static Logger mLog = LoggerFactory.getLogger(SDRPlayTunerController.class);
    
    public static final long MIN_FREQUENCY = 10000000l;
    public static final long MAX_FREQUENCY = 6000000000l;
    public static final long DEFAULT_FREQUENCY = 101100000;
    public static final double USABLE_BANDWIDTH_PERCENT = 0.90;
    public static final int DC_SPIKE_AVOID_BUFFER = 5000;
    
    private static final SDRplayWrapper wrapper = SDRplayWrapper.getInstance();
    
    private ReusableComplexBufferQueue mReusableComplexBufferQueue = new ReusableComplexBufferQueue("SDRplayTunerController");
    
    private final StreamListener streamListenerA = new StreamListener(mReusableBufferBroadcaster, mReusableComplexBufferQueue);
    private final StreamListener streamListenerB = new StreamListener(mReusableBufferBroadcaster, mReusableComplexBufferQueue);
    private final EventListener eventListener = new EventListener();
    
    private byte mHWVer;
    private String mSerialNumber;
    private sdrplay_api_DeviceT mDevice;

    public SDRPlayTunerController(sdrplay_api_DeviceT deviceData)
    {
        super(MIN_FREQUENCY, MAX_FREQUENCY, DC_SPIKE_AVOID_BUFFER, USABLE_BANDWIDTH_PERCENT);

        mHWVer = deviceData.hwVer;
        mSerialNumber = new String(deviceData.SerNo, Charset.forName("UTF-8")).trim();
        mDevice = deviceData;
    }
    
    @Override
    public void dispose() {
        wrapper.disposeTuner(mDevice);
    }

    @Override
    public int getBufferSampleCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void apply(TunerConfiguration config) throws SourceException {
        if (config instanceof SDRPlayTunerConfiguration) {
            SDRPlayTunerConfiguration sdrPlayConfig = (SDRPlayTunerConfiguration) config;

//            //Convert legacy sample rate setting to new sample rates
//            if(!hackRFConfig.getSampleRate().isValidSampleRate())
//            {
//                mLog.warn("Changing legacy HackRF Sample Rates Setting [" + hackRFConfig.getSampleRate().name() + "] to current valid setting");
//                hackRFConfig.setSampleRate(HackRFSampleRate.RATE_5_0);
//            }
//
//            try
//            {
//                setSampleRate(hackRFConfig.getSampleRate());
//                setFrequencyCorrection(hackRFConfig.getFrequencyCorrection());
//                setAmplifierEnabled(hackRFConfig.getAmplifierEnabled());
//                setLNAGain(hackRFConfig.getLNAGain());
//                setVGAGain(hackRFConfig.getVGAGain());
//                setFrequency(getFrequency());
//            }
//            catch(UsbException e)
//            {
//                throw new SourceException("Error while applying tuner "
//                    + "configuration", e);
//            }
//
//            try
//            {
//                setFrequency(hackRFConfig.getFrequency());
//            }
//            catch(SourceException se)
//            {
//                //Do nothing, we couldn't set the frequency
//            }
        } else {
            throw new IllegalArgumentException("Invalid tuner configuration "
                    + "type [" + config.getClass() + "]");
        }
    }

    @Override
    public long getTunedFrequency() throws SourceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTunedFrequency(long frequency) throws SourceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getCurrentSampleRate() throws SourceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getSerialNumber() {
        return mSerialNumber;
    }
    
    /**
     * Adds the IQ buffer listener and automatically starts buffer transfer processing, if not already started.
     */
    @Override
    public void addBufferListener(Listener<ReusableComplexBuffer> listener)
    {
        if(!hasBufferListeners()) {
            wrapper.startTuner(mDevice.dev, streamListenerA, streamListenerB, eventListener);
        }
        super.addBufferListener(listener);
    }

    /**
     * Removes the IQ buffer listener and stops buffer transfer processing if there are no more listeners.
     */
    @Override
    public void removeBufferListener(Listener<ReusableComplexBuffer> listener)
    {
        super.removeBufferListener(listener);

        if(!hasBufferListeners())
        {
            wrapper.stopTuner(mDevice.dev);
        }
    }
    
    private class StreamListener extends StreamConsumer {

        private final ReusableBufferBroadcaster rbb;
        private final ReusableComplexBufferQueue mReusableComplexBufferQueue;
        
        public StreamListener(ReusableBufferBroadcaster<ReusableComplexBuffer> broadcaster, ReusableComplexBufferQueue bufferQueue) {
            rbb = broadcaster;
            mReusableComplexBufferQueue = bufferQueue;
        }
        
        @Override
        public void consume(short[] iSamples, short[] qSamples, SDRPlayAPI.sdrplay_api_StreamCbParamsT params, int numSamples, int reset, Pointer cbContext) {
            mLog.debug("Got samples: " + numSamples);
            mLog.debug(String.valueOf((float)iSamples[0]));
            ReusableComplexBuffer buffer = mReusableComplexBufferQueue.getBuffer(numSamples*2);
            
            float[] primitiveFloatBuffer = new float[numSamples*2]; // TODO Not the most efficient, ideall we'd have a ReusableBuffer with separate I/Q buffers
            
            for (int s=0;s<numSamples;s++) {
                primitiveFloatBuffer[s*2] = (float)iSamples[s];
                primitiveFloatBuffer[(s*2)+1] = (float)qSamples[s];
            }
            
            buffer.reloadFrom(FloatBuffer.wrap(primitiveFloatBuffer), System.currentTimeMillis());
            
            rbb.broadcast(buffer);
        }
        
    }
    
    private class EventListener implements sdrplay_api_EventCallback_t {
        
        public EventListener() {

        }
        
        @Override
        public void apply(SDRPlayAPI.sdrplay_api_EventT eventId, SDRPlayAPI.sdrplay_api_TunerSelectT tuner, SDRPlayAPI.sdrplay_api_EventParamsT params, Pointer cbContext) {
            System.out.println("Event: " + eventId);
        }

    }
}
