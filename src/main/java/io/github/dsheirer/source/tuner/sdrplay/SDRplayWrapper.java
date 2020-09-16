/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dsheirer.source.tuner.sdrplay;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import io.github.dsheirer.preference.UserPreferences;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.HANDLE;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_CallbackFnsT;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_DeviceT;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_ErrT;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_EventCallback_t;
import io.github.sammy1am.sdrplay.api.SDRPlayAPI.sdrplay_api_StreamCallback_t;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam
 */
public class SDRplayWrapper {

    final static SDRPlayAPI API = SDRPlayAPI.INSTANCE;
    boolean apiOpen = false;
    private final static SDRplayWrapper INSTANCE = new SDRplayWrapper();
    
    private final static int MAX_DEVICES = 16;
    
    public static SDRplayWrapper getInstance() {
        return INSTANCE;
    }
    
    //TODO We need a lot of exception handling here
    public List<SDRPlayTuner> getTuners(UserPreferences userPreferences){
        // Open API if it isn't already
        if (!apiOpen) {
            checkReturnStatus(API.sdrplay_api_Open());
            FloatByReference apiVer = new FloatByReference();
            checkReturnStatus(API.sdrplay_api_ApiVersion(apiVer));
            System.out.println("Opened SDRplay API Ver: " + apiVer.getValue());
        }
        
        // Lock Device API to keep other programs from fighting us
        checkReturnStatus(API.sdrplay_api_LockDeviceApi());
        
        ArrayList<SDRPlayTuner> foundTuners = new ArrayList<>();
        
        try {
        IntByReference numDevices = new IntByReference();
        sdrplay_api_DeviceT[] devices = new sdrplay_api_DeviceT[MAX_DEVICES];
        checkReturnStatus(API.sdrplay_api_GetDevices(devices, numDevices, MAX_DEVICES));
        
        
        for (int d=0;d<numDevices.getValue();d++) {
            sdrplay_api_DeviceT device = devices[d];
            // Select this device for our use
            checkReturnStatus(API.sdrplay_api_SelectDevice(device));
            
            SDRPlayTunerController controller = new SDRPlayTunerController(device);
            SDRPlayTuner tuner = new SDRPlayTuner(controller, userPreferences);
            foundTuners.add(tuner);
        }
        
        } finally {
            // Unlock device API now that we've selected our devices
            checkReturnStatus(API.sdrplay_api_UnlockDeviceApi());
        }
        
        
        return foundTuners;
    }
    
    public void startTuner(HANDLE dev, sdrplay_api_StreamCallback_t streamCBA, sdrplay_api_StreamCallback_t streamCBB, sdrplay_api_EventCallback_t eventCB) {
        final sdrplay_api_CallbackFnsT callbacks = new sdrplay_api_CallbackFnsT(streamCBA, streamCBB, eventCB);
        checkReturnStatus(API.sdrplay_api_Init(dev, callbacks, Pointer.NULL));
    }
    
    public void stopTuner(HANDLE dev) {
        checkReturnStatus(API.sdrplay_api_Uninit(dev));
    }
    
    public void disposeTuner(sdrplay_api_DeviceT device) {
        checkReturnStatus(API.sdrplay_api_Uninit(device.dev));
        checkReturnStatus(API.sdrplay_api_ReleaseDevice(device));
    }
    
    public void closeAPI() {
        //TODO We *maybe* should keep track of all tuners so we can uninit them?  Not sure when this method is called.
        checkReturnStatus(API.sdrplay_api_Close());
        apiOpen = false;
    }
    
    // TODO This should throw specific checked exceptions
    private void checkReturnStatus(SDRPlayAPI.sdrplay_api_ErrT returnStatus) {
        if (returnStatus != sdrplay_api_ErrT.sdrplay_api_Success) {
            throw new RuntimeException(API.sdrplay_api_GetErrorString(returnStatus));
        }
    }
    
}
