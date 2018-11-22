package io.github.anotherme.fontmapping;

import io.github.anotherme.fontmapping.convert.WoffConverter;
import io.github.anotherme.fontmapping.enums.FontType;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * @author lirenhao
 * date: 2018/11/21 下午4:00
 */
public class FontParser {

    private byte[] ttfByte;

    private TrueTypeFont ttf;

    private Map<Integer, Integer> characterCodeToGlyphIdMap;

    private Map<Integer, String> characterCodeToStrMap = new HashMap<>();

    public FontParser(FontType fontType, String fontBase64Str) throws Exception {
        this(fontType, new BASE64Decoder().decodeBuffer(fontBase64Str));
    }

    public FontParser(FontType fontType, byte[] fontBytes) throws Exception {
        this.ttfByte = convert2TTFByte(fontType, fontBytes);

        TTFParser parser = new TTFParser();

        this.ttf = parser.parse(new ByteArrayInputStream(this.ttfByte));

        CmapSubtable cmapSubtable = this.ttf.getCmap().getCmaps()[0];

        Field privateMapField = CmapSubtable.class.getDeclaredField("characterCodeToGlyphId");
        privateMapField.setAccessible(true);//允许访问私有字段
        this.characterCodeToGlyphIdMap = (Map<Integer, Integer>) privateMapField.get(cmapSubtable);
    }

    public byte[] convert2TTFByte(FontType fontType, byte[] fontBytes) throws IOException, DataFormatException {
        switch (fontType) {
            case WOFF:
                WoffConverter woffConverter = new WoffConverter();
                return woffConverter.convertToTTFByteArray(new ByteArrayInputStream(fontBytes));
            default:
                return fontBytes;
        }
    }

    public char[] drawAllFont(String imgFilePath, int width, int height, float fontSize) throws IOException, FontFormatException {
        char[] chars = new char[this.characterCodeToGlyphIdMap.size()];

        int i = 0;
        for (Map.Entry<Integer, Integer> entry : this.characterCodeToGlyphIdMap.entrySet()) {
            chars[i] = (char) entry.getKey().intValue();
            i++;
        }

        drawFontImg(chars, imgFilePath, width, height, fontSize);

        return chars;
    }

    public void drawFontImg(char[] text, String imgFilePath, int width, int height, float fontSize) throws IOException, FontFormatException {
        // 创建BufferedImage对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取Graphics2D
        Graphics2D g2d = image.createGraphics();
        // 画图
        g2d.setBackground(new Color(255, 255, 255));
        g2d.setColor(Color.BLACK);
        g2d.clearRect(0, 0, width, height);
        Font font = Font.createFont(0, new ByteArrayInputStream(this.ttfByte)).deriveFont(fontSize);
        g2d.setFont(font);
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 计算文字长度，计算居中的x点坐标
        FontMetrics fm = g2d.getFontMetrics(font);
        int textWidth = fm.charsWidth(text, 0, text.length);
        int widthX = (width - textWidth) / 2;
        // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
        g2d.drawChars(text, 0, text.length, widthX, height / 2);
        // 释放对象
        g2d.dispose();

        File imgFile = new File(imgFilePath);
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }

        // 保存文件
        ImageIO.write(image, "jpg", new File(imgFilePath));
    }

    public TrueTypeFont getTtf() {
        return ttf;
    }

    public Map<Integer, Integer> getCharacterCodeToGlyphIdMap() {
        return characterCodeToGlyphIdMap;
    }

    public void setCharacterCodeToStrMap(Map<Integer, String> characterCodeToStrMap) {
        this.characterCodeToStrMap = characterCodeToStrMap;
    }

    public Map<Integer, String> getCharacterCodeToStrMap() {
        return characterCodeToStrMap;
    }

    public String putCharacterCodeMap(Integer characterCode, String str) {
        return characterCodeToStrMap.put(characterCode, str);
    }

    public String getStrByCharacterCode(Integer characterCode) {
        return this.characterCodeToStrMap.get(characterCode);
    }

    public String parserCharacterCode(String characterCodeStr) {
        return parserCharacterCode(characterCodeStr.toCharArray());
    }

    public String parserCharacterCode(char[] characterCodes) {
        StringBuilder sb = new StringBuilder();
        for (char c : characterCodes) {
            sb.append(parserCharacterCode(c));
        }
        return sb.toString();
    }

    public String parserCharacterCode(char characterCode) {
        return this.characterCodeToStrMap.get((int) characterCode);
    }
}
