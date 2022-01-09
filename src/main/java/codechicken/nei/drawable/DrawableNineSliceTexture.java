package codechicken.nei.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

/*
 * Taken from Just Enough Items 1.12 under the MIT License
 *
 * The MIT License (MIT) Copyright (c) 2014-2015 mezz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Breaks a texture into 9 pieces so that it can be scaled to any size.
 * Draws the corners and then repeats any middle textures to fill the remaining area.
 */
public class DrawableNineSliceTexture {
    private final TextureInfo info;
    private final int T_SIZE = 256;

    public DrawableNineSliceTexture(TextureInfo info) {
        this.info = info;
    }

    public void draw(int xOffset, int yOffset, int width, int height) {
        ResourceLocation location = info.getLocation();
        int leftWidth = info.getSliceLeft();
        int rightWidth = info.getSliceRight();
        int topHeight = info.getSliceTop();
        int bottomHeight = info.getSliceBottom();
        int textureWidth = info.getWidth();
        int textureHeight = info.getHeight();

        Minecraft.getMinecraft().renderEngine.bindTexture(location);

        float uMin = (float) xOffset / T_SIZE;
        float uMax = (float) (xOffset + textureWidth) / T_SIZE;
        float vMin = (float) yOffset / T_SIZE;
        float vMax = (float) (yOffset + textureHeight) / T_SIZE;
        float uSize = uMax - uMin;
        float vSize = vMax - vMin;

        float uLeft = uMin + uSize * (leftWidth / (float) textureWidth);
        float uRight = uMax - uSize * (rightWidth / (float) textureWidth);
        float vTop = vMin + vSize * (topHeight / (float) textureHeight);
        float vBottom = vMax - vSize * (bottomHeight / (float) textureHeight);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        // left top
        draw(tessellator, uMin, vMin, uLeft, vTop, xOffset, yOffset, leftWidth, topHeight);
        // left bottom
        draw(tessellator, uMin, vBottom, uLeft, vMax, xOffset, yOffset + height - bottomHeight, leftWidth, bottomHeight);
        // right top
        draw(tessellator, uRight, vMin, uMax, vTop, xOffset + width - rightWidth, yOffset, rightWidth, topHeight);
        // right bottom
        draw(tessellator, uRight, vBottom, uMax, vMax, xOffset + width - rightWidth, yOffset + height - bottomHeight, rightWidth, bottomHeight);

        int middleWidth = textureWidth - leftWidth - rightWidth;
        int middleHeight = textureWidth - topHeight - bottomHeight;
        int tiledMiddleWidth = width - leftWidth - rightWidth;
        int tiledMiddleHeight = height - topHeight - bottomHeight;
        if (tiledMiddleWidth > 0) {
            // top edge
            drawTiled(tessellator, uLeft, vMin, uRight, vTop, xOffset + leftWidth, yOffset, tiledMiddleWidth, topHeight, middleWidth, topHeight);
            // bottom edge
            drawTiled(tessellator, uLeft, vBottom, uRight, vMax, xOffset + leftWidth, yOffset + height - bottomHeight, tiledMiddleWidth, bottomHeight, middleWidth, bottomHeight);
        }
        if (tiledMiddleHeight > 0) {
            // left side
            drawTiled(tessellator, uMin, vTop, uLeft, vBottom, xOffset, yOffset + topHeight, leftWidth, tiledMiddleHeight, leftWidth, middleHeight);
            // right side
            drawTiled(tessellator, uRight, vTop, uMax, vBottom, xOffset + width - rightWidth, yOffset + topHeight, rightWidth, tiledMiddleHeight, rightWidth, middleHeight);
        }
        if (tiledMiddleHeight > 0 && tiledMiddleWidth > 0) {
            // middle area
            drawTiled(tessellator, uLeft, vTop, uRight, vBottom, xOffset + leftWidth, yOffset + topHeight, tiledMiddleWidth, tiledMiddleHeight, middleWidth, middleHeight);
        }

        tessellator.draw();
    }

    private void drawTiled(Tessellator tessellator, float uMin, float vMin, float uMax, float vMax, int xOffset, int yOffset, int tiledWidth, int tiledHeight, int width, int height) {
        int xTileCount = tiledWidth / width;
        int xRemainder = tiledWidth - (xTileCount * width);
        int yTileCount = tiledHeight / height;
        int yRemainder = tiledHeight - (yTileCount * height);

        int yStart = yOffset + tiledHeight;

        float uSize = uMax - uMin;
        float vSize = vMax - vMin;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int tileWidth = (xTile == xTileCount) ? xRemainder : width;
                int tileHeight = (yTile == yTileCount) ? yRemainder : height;
                int x = xOffset + (xTile * width);
                int y = yStart - ((yTile + 1) * height);
                if (tileWidth > 0 && tileHeight > 0) {
                    int maskRight = width - tileWidth;
                    int maskTop = height - tileHeight;
                    float uOffset = (maskRight / (float) width) * uSize;
                    float vOffset = (maskTop / (float) height) * vSize;

                    draw(tessellator, uMin, vMin + vOffset, uMax - uOffset, vMax, x, y + maskTop, tileWidth, tileHeight);
                }
            }
        }
    }

    private static void draw(Tessellator tessellator, float minU, double minV, float maxU, float maxV, int xOffset, int yOffset, int width, int height) {
        tessellator.addVertexWithUV(xOffset, yOffset + height, 0, minU, minV);
        tessellator.addVertexWithUV(xOffset + width, yOffset + height, 0, maxU, maxV);
        tessellator.addVertexWithUV(xOffset + width, yOffset, 0, maxU, minV);
        tessellator.addVertexWithUV(xOffset, yOffset, 0, minU, minV);
    }
}
