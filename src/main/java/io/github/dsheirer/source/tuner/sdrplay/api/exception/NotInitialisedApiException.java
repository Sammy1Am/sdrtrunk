package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * NotInitialised SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class NotInitialisedApiException extends ApiException {

    public NotInitialisedApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotInitialised);
    }

}
