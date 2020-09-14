package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * NotEnabled SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class NotEnabledApiException extends ApiException {

    public NotEnabledApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_NotEnabled);
    }

}
