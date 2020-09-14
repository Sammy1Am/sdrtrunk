package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * InvalidParam SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class InvalidParamApiException extends ApiException {

    public InvalidParamApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_InvalidParam);
    }

}
