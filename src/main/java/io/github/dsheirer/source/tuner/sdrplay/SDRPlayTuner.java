/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dsheirer.source.tuner.sdrplay;

import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.source.SourceException;
import io.github.dsheirer.source.tuner.Tuner;
import io.github.dsheirer.source.tuner.TunerClass;
import io.github.dsheirer.source.tuner.TunerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sam
 */
public class SDRPlayTuner extends Tuner {

    private final static Logger mLog = LoggerFactory.getLogger(SDRPlayTuner.class);

    public SDRPlayTuner(SDRPlayTunerController controller, UserPreferences userPreferences)
    {
        super("SDRPlay", controller, userPreferences);
    }

    public SDRPlayTunerController getController()
    {
        return (SDRPlayTunerController)getTunerController();
    }
    
    @Override
    public int getMaximumUSBBitsPerSecond() {
        //12 bits per sample * 8.064 MSPS
        return 96768000;  // This is sort of a rough guess
    }

    @Override
    public String getUniqueID() {
        return getController().getSerialNumber();
    }

    @Override
    public TunerClass getTunerClass() {
        return TunerClass.SDRPLAY_RSP1; //TODO figure out hardware Ids
    }

    @Override
    public TunerType getTunerType() {
        return TunerType.SDRPLAY_GENERIC;
    }

    @Override
    public double getSampleSize() {
        return 12.0; //TODO No idea where to get this from
    }
    
}
