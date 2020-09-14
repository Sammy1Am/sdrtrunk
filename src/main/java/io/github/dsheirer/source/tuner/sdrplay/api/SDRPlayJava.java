/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dsheirer.source.tuner.sdrplay.api;

import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.source.tuner.sdrplay.SDRPlayTuner;
import io.github.dsheirer.source.tuner.sdrplay.SDRPlayTunerConfiguration;
import io.github.dsheirer.source.tuner.sdrplay.SDRPlayTunerController;
import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary.HANDLE;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.AliasingErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.AlreadyInitialisedApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.ApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.FailApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.FsUpdateErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.GainUpdateErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.HwErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.HwVerErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.InvalidParamApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.NotEnabledApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.NotInitialisedApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.OutOfMemErrorApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.OutOfRangeApiException;
import io.github.dsheirer.source.tuner.sdrplay.api.exception.RfUpdateErrorApiException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heavily based-on/borrowed-from https://github.com/ludovicmartin/sdrplay4j
 * @author Sam
 */
public class SDRPlayJava {
    private final static Logger mLog = LoggerFactory.getLogger(SDRPlayJava.class);
    
    private static final int MAX_DEVICE_COUNT = 16;
    private static SDRPlayJava instance;
    private boolean debugMode = false;
    private final static SDRPlayAPILibrary API = SDRPlayAPILibrary.INSTANCE;

    private SDRPlayJava() {
    }

    /**
     * Get the API Java-wrapped instance.
     *
     * @return API Java-wrapped instance
     */
    public static SDRPlayJava getInstance() {
        if (instance == null) {
            instance = new SDRPlayJava();
        }
        return instance;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        setDebugMode(null, debugMode);
    }
    
    public void setDebugMode(HANDLE handle, boolean debugMode) {
        this.debugMode = debugMode;
        API.sdrplay_api_DebugEnable(handle, debugMode ? 1 : 0);
    }
    
    public void openApi() {
        checkResultCode(API.sdrplay_api_Open());
        mLog.info("Opened SDRPlay API version: "+ getApiVersion());
    }
    
    public void closeApi() {
        checkResultCode(API.sdrplay_api_Close());
    }

    public float getApiVersion() {
        FloatByReference version = new FloatByReference();
        checkResultCode(API.sdrplay_api_ApiVersion(version));
        return version.getValue();
    }
    
    public List<SDRPlayTuner> getTuners(UserPreferences userPreferences) {
        
        IntByReference numDevs = new IntByReference();
        sdrplay_api_DeviceT.ByReference devices = new sdrplay_api_DeviceT.ByReference();
        sdrplay_api_DeviceT[] deviceData = (sdrplay_api_DeviceT[]) devices.toArray(MAX_DEVICE_COUNT);
        checkResultCode(API.sdrplay_api_GetDevices(devices, numDevs, MAX_DEVICE_COUNT));

        List<SDRPlayTuner> result = new ArrayList(numDevs.getValue());
        for (int i = 0; i < numDevs.getValue(); i++) {
            try {
                sdrplay_api_DeviceT deviceDataItem = deviceData[i];
                
                //TODO check hwVer for compatibility? (Do we need to?)
                
                SDRPlayTunerController tc = new SDRPlayTunerController(deviceDataItem);
                SDRPlayTuner t = new SDRPlayTuner(tc, userPreferences);
                
                result.add(t);
            } catch (Exception ex) {
                mLog.warn(ex.getMessage());
            }
        }
        return result;
    }
    
    /**
     * Check an API result code. An APIException is thrown if different from
     * <code>mir_sdr_Success</code>.
     *
     * @param code API result code
     * @throws ApiException
     */
    public static void checkResultCode(int code) throws ApiException {
        switch (code) {
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_Success:
                return;
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_Fail:
                throw new FailApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_InvalidParam:
                throw new InvalidParamApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfRange:
                throw new OutOfRangeApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_GainUpdateError:
                throw new GainUpdateErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_RfUpdateError:
                throw new RfUpdateErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_FsUpdateError:
                throw new FsUpdateErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_HwError:
                throw new HwErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AliasingError:
                throw new AliasingErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AlreadyInitialised:
                throw new AlreadyInitialisedApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotInitialised:
                throw new NotInitialisedApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotEnabled:
                throw new NotEnabledApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_HwVerError:
                throw new HwVerErrorApiException();
            case SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfMemError:
                throw new OutOfMemErrorApiException();
            default:
                throw new RuntimeException("Unknown API result code: " + code);
        }
    }
}
