package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * OutOfMemError SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class OutOfMemErrorApiException extends ApiException {

    public OutOfMemErrorApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfMemError);
    }

}
