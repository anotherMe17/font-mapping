package io.github.anotherme.fontmapping.compare;

import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphDescription;

/**
 * @author lirenhao
 * date: 2018/11/22 下午2:09
 */
public class DefaultGlyphDataCompare implements GlyphDataCompare {

    private int DEVIATION;

    public DefaultGlyphDataCompare() {
        this(150);
    }

    public DefaultGlyphDataCompare(int deviation) {
        this.DEVIATION = deviation;
    }

    @Override
    public boolean compareGlyphData(GlyphData mappingGlyphData, GlyphData datumGlyphData) {
        if (datumGlyphData.getXMaximum() != mappingGlyphData.getXMaximum() || datumGlyphData.getXMinimum() != mappingGlyphData.getXMinimum()
                || datumGlyphData.getYMaximum() != mappingGlyphData.getYMaximum() || datumGlyphData.getYMinimum() != mappingGlyphData.getYMinimum()) {
            return false;
        }

        GlyphDescription datumDescription = datumGlyphData.getDescription();
        GlyphDescription mappingDescription = mappingGlyphData.getDescription();

        if (datumDescription.getContourCount() != mappingDescription.getContourCount()
                || datumDescription.getPointCount() != mappingDescription.getPointCount()) {
            return false;
        }

        for (int j = 0; j < datumDescription.getContourCount(); j++) {
            if (datumDescription.getEndPtOfContours(j) != mappingDescription.getEndPtOfContours(j)) {
                return false;
            }
        }

        for (int j = 0; j < datumDescription.getPointCount(); j++) {
            if (datumDescription.getFlags(j) != mappingDescription.getFlags(j)) {
                return false;
            }
        }

//        for (int j = 0; j < datumDescription.getPointCount(); j++) {
//            if (datumDescription.getXCoordinate(j) + DEVIATION < mappingDescription.getXCoordinate(j)
//                    || datumDescription.getXCoordinate(j) - DEVIATION > mappingDescription.getXCoordinate(j)) {
//                return false;
//            }
//
//            if (datumDescription.getYCoordinate(j) + DEVIATION < mappingDescription.getYCoordinate(j)
//                    || datumDescription.getYCoordinate(j) - DEVIATION > mappingDescription.getYCoordinate(j)) {
//                return false;
//            }
//        }

        return true;
    }
}
