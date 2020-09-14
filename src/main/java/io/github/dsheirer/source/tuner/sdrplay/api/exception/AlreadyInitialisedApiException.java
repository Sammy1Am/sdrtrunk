package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * AlreadyInitialised SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class AlreadyInitialisedApiException extends ApiException {

    public AlreadyInitialisedApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AlreadyInitialised);
    }

}
