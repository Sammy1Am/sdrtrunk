package io.github.dsheirer.source.tuner.sdrplay.api.exception;

import io.github.dsheirer.source.tuner.sdrplay.api.SDRPlayAPILibrary;

/**
 * FsUpdateError SDRplay API exception.
 *
 * @author Ludovic MARTIN - contact _A_T_ ludovicmartin.fr
 */
public class FsUpdateErrorApiException extends ApiException {

    public FsUpdateErrorApiException() {
        super(SDRPlayAPILibrary.sdrplay_api_ErrT.sdrplay_api_FsUpdateError);
    }

}
