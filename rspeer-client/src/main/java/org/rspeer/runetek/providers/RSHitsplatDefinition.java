package org.rspeer.runetek.providers;

public interface RSHitsplatDefinition extends RSDoublyNode {
    RSSprite getIcon();
	RSSprite getLeftSprite();
	RSSprite getMiddleSprite();
	RSSprite getRightSprite();
	int getComparisonType();
	int getDuration();
	int getFade();
	int getFontId();
	int getIconId();
	int getLeftSpriteId();
	int getMiddleSpriteId();
	int getOffsetX();
	int getOffsetY();
	int getRightSpriteId();
	int getTextColor();
	int getVarpIndex();
	int getVarpbitIndex();
	String getAmount();
	int[] getTransformIds();
}