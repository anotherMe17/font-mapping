package io.github.anotherme.fontmapping;

import io.github.anotherme.fontmapping.compare.DefaultGlyphDataCompare;
import io.github.anotherme.fontmapping.compare.GlyphDataCompare;
import io.github.anotherme.fontmapping.exception.FontParserException;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;

import java.io.IOException;
import java.util.Map;

/**
 * @author lirenhao
 * date: 2018/11/22 上午10:50
 */
public class FontMapping {

    public static void mappingFont(FontParser datumFontParser, FontParser mappingFontParser) throws FontParserException, IOException {
        mappingFont(datumFontParser, mappingFontParser, new DefaultGlyphDataCompare());
    }

    public static void mappingFont(FontParser datumFontParser, FontParser mappingFontParser, GlyphDataCompare compare) throws FontParserException, IOException {
        if (datumFontParser.getCharacterCodeToStrMap() == null || datumFontParser.getCharacterCodeToStrMap().size() < 1) {
            throw new FontParserException("datum font characterCodeToStrMap is empty");
        }

        GlyphTable mappingGlyphTable = mappingFontParser.getTtf().getGlyph();
        GlyphTable datumGlyphTable = datumFontParser.getTtf().getGlyph();

        for (Map.Entry<Integer, Integer> mappingEntry : mappingFontParser.getCharacterCodeToGlyphIdMap().entrySet()) {
            GlyphData mappingGlyphData = mappingGlyphTable.getGlyph(mappingEntry.getValue());
            if (mappingGlyphData == null) {
                continue;
            }

            boolean isEqual = false;
            int characterCode = 0;

            for (Map.Entry<Integer, Integer> datumEntry : datumFontParser.getCharacterCodeToGlyphIdMap().entrySet()) {
                GlyphData datumGlyphData = datumGlyphTable.getGlyph(datumEntry.getValue());

                if (compare.compareGlyphData(mappingGlyphData, datumGlyphData)) {
                    isEqual = true;
                    characterCode = datumEntry.getKey();
                    break;
                }
            }

            if (isEqual) {
                mappingFontParser.putCharacterCodeMap(mappingEntry.getKey(), datumFontParser.getStrByCharacterCode(characterCode));
            }
        }
    }

}
