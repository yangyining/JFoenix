/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.skins;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class JFXRadioButtonSkin extends RadioButtonSkin {

	private boolean invalid = true;
	private double padding = 15;
	private double contWidth, contHeight;
	private double maxHeight, radioRadius = 8, minRadius = 0;
	private final JFXRippler rippler;

	private Circle radio, dot;

	private Color unSelectedColor = Color.valueOf("#5A5A5A");
	private Color selectedColor = Color.valueOf("#0F9D58");

	private Timeline timeline;

	private final AnchorPane container = new AnchorPane();
	private double labelOffset = -1;

	public JFXRadioButtonSkin(RadioButton control) {
		super(control);

		radio = new Circle(radioRadius);
		radio.setStrokeWidth(2);
		radio.setFill(Color.TRANSPARENT);
		radio.getStyleClass().setAll("radio");

		dot = new Circle();
		dot.setRadius(minRadius);
		dot.setFill(selectedColor);
		dot.getStyleClass().setAll("dot");

		StackPane boxContainer = new StackPane();
		boxContainer.getChildren().addAll(radio, dot);
		boxContainer.setPadding(new Insets(padding));
		rippler = new JFXRippler(boxContainer, RipplerMask.CIRCLE);
		container.getChildren().add(rippler);
		AnchorPane.setRightAnchor(rippler, labelOffset);
		updateChildren();
		
	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (radio != null) {
			removeRadio();
			getChildren().add(container);
		}
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		final RadioButton radioButton = getSkinnable();
		contWidth = snapSize(container.prefWidth(-1)) + (invalid? 2 : 0);
		contHeight = snapSize(container.prefHeight(-1)) + (invalid? 2 : 0);
		final double computeWidth = Math.min(radioButton.prefWidth(-1), radioButton.minWidth(-1)) + labelOffset + 2 * padding;
		final double labelWidth = Math.min(computeWidth - contWidth, w - snapSize(contWidth)) + labelOffset + 2 * padding;
		final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
		maxHeight = Math.max(contHeight, labelHeight);
		final double xOffset = computeXOffset(w, labelWidth + contWidth, radioButton.getAlignment().getHpos()) + x;
		final double yOffset = computeYOffset(h, maxHeight, radioButton.getAlignment().getVpos()) + x;

		if (invalid) {
			initializeComponents(x, y, w, h);
			invalid = false;
		}
		
        layoutLabelInArea(xOffset + contWidth, yOffset, labelWidth, maxHeight,  radioButton.getAlignment());
        container.resize(snapSize(contWidth), snapSize(contHeight));		
        positionInArea(container, xOffset, yOffset, contWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());
	}

	private void initializeComponents(final double x, final double y, final double w, final double h) {
		radio.setStroke(unSelectedColor);
		getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
			rippler.setRipplerFill(newVal ? unSelectedColor : selectedColor);
			timeline.setRate(newVal ? 1 : -1);
			timeline.play();
		});
		rippler.setRipplerFill(getSkinnable().isSelected() ? unSelectedColor : selectedColor);
		timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(dot.radiusProperty(), minRadius, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(200), new KeyValue(dot.radiusProperty(),
				radioRadius + radio.getStrokeWidth() / 2, Interpolator.EASE_BOTH)));
		rippler.setRipplerFill(getSkinnable().isSelected() ? unSelectedColor : selectedColor);
		timeline.setRate(getSkinnable().isSelected() ? 1 : -1);
		timeline.play();
	}

	private void removeRadio() {
		for (int i = 0; i < getChildren().size(); i++) {
			if ("radio".equals(getChildren().get(i).getStyleClass().get(0))) {
				getChildren().remove(i);
			}
		}
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(radio.minWidth(-1)) + labelOffset + 2 * padding;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(radio.prefWidth(-1)) + labelOffset + 2 * padding;
	}
	
	static double computeXOffset(double width, double contentWidth, HPos hpos) {
		switch(hpos) {
		case LEFT:
			return 0;
		case CENTER:
			return (width - contentWidth) / 2;
		case RIGHT:
			return width - contentWidth;
		}
		return 0;
	}

	static double  computeYOffset(double height, double contentHeight, VPos vpos) {

		switch(vpos) {
		case TOP:
			return 0;
		case CENTER:
			return (height - contentHeight) / 2;
		case BOTTOM:
			return height - contentHeight;
		default:
			return 0;
		}
	}
	

}