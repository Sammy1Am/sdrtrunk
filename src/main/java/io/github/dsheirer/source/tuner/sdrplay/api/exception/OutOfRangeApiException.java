package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * OutOfRange SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class OutOfRangeApiException extends ApiException {

    public OutOfRangeApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_OutOfRange);
    }

}
