package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * Fail SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class FailApiException extends ApiException {

    public FailApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_Fail);
    }

}
