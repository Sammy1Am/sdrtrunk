package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * HwError SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class HwErrorApiException extends ApiException {

    public HwErrorApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_HwError);
    }

}
