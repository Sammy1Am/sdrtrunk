package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * AliasingError SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class AliasingErrorApiException extends ApiException {

    public AliasingErrorApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_AliasingError);
    }

}
