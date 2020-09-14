package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * RfUpdateError SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class RfUpdateErrorApiException extends ApiException {

    public RfUpdateErrorApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_RfUpdateError);
    }

}
