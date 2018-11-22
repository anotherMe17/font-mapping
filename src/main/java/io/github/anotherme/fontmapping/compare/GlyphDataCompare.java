package io.github.anotherme.fontmapping.compare;

import org.apache.fontbox.ttf.GlyphData;

/**
 * @author lirenhao
 * date: 2018/11/22 下午2:08
 */
public interface GlyphDataCompare {
    boolean compareGlyphData(GlyphData mappingGlyphData, GlyphData datumGlyphData);
}
