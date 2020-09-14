package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;
import java.util.HashMap;
import java.util.Map;

/**
 * SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public abstract class ApiException extends RuntimeException {

    private final int code;
    private static final Map<Integer, String> MESSAGES = new HashMap<Integer, String>() {
        {
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_Fail, "Other failure mechanism (thread / mutex create)");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_InvalidParam, "NULL pointers");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfRange, "Requested parameters outside of allowed range");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_GainUpdateError, "Previous update has not yet been applied");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_RfUpdateError, "Previous update has not yet been applied");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_FsUpdateError, "Previous update has not yet been applied");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_HwError, "Failed to access device");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AliasingError, "Requested parameters will cause aliasing");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AlreadyInitialised, "API has been initialised previously");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotInitialised, "API has not been initialised");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotEnabled, "The requested change has not been applied");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_HwVerError, "Incorrect device");
            put(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfMemError, "Out of memory");
        }
    };

    public ApiException(int code) {
        super(MESSAGES.get(code));
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
