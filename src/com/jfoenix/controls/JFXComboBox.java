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

package com.jfoenix.controls;

import com.jfoenix.converters.base.NodeConverter;
import com.jfoenix.skins.JFXComboBoxListViewSkin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class JFXComboBox<T> extends ComboBox<T> {

	
	private static final String DEFAULT_STYLE_CLASS = "jfx-combo-box";
	
	public JFXComboBox() {
		super();
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> listView) {
				return new JFXListCell<T>();
			}
		});
		this.setConverter(new StringConverter<T>() {
			@Override
			public String toString(T object) {
				if(object == null) return null;
				if(object instanceof Label) return ((Label)object).getText();
				return object.toString();				
			}

			@SuppressWarnings("unchecked")
			@Override
			public T fromString(String string) {
				return (T) string;
			}
		});
	}
	
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXComboBoxListViewSkin<T>(this);
    }

    /***************************************************************************
	 *                                                                         *
	 * Node Converter Propertie                                                *
	 *                                                                         *
	 **************************************************************************/
    private TextField textField;
    /**
     * JFX text field is set as the editor for the ComboBox.
     * The editor is null if the ComboBox is not
     * {@link #editableProperty() editable}. 
     */
    private ReadOnlyObjectWrapper<TextField> jfxEditor;
    public final TextField getJFXEditor() { 
        return jfxEditorProperty().get(); 
    }
    public final ReadOnlyObjectProperty<TextField> jfxEditorProperty() { 
        if (jfxEditor == null) {
        	jfxEditor = new ReadOnlyObjectWrapper<TextField>(this, "editor");
        	// TODO: solve focus issue after selection
            textField = new JFXTextField();
            jfxEditor.set(textField);
        }
        return jfxEditor.getReadOnlyProperty(); 
    }
    
    
	/***************************************************************************
	 *                                                                         *
	 * Node Converter Propertie                                                *
	 *                                                                         *
	 **************************************************************************/
    
    /**
     * Converts the user-typed input (when the ComboBox is 
     * {@link #editableProperty() editable}) to an object of type T, such that 
     * the input may be retrieved via the  {@link #valueProperty() value} property.
     */
    public ObjectProperty<NodeConverter<T>> nodeConverterProperty() { return nodeConverter; }
    private ObjectProperty<NodeConverter<T>> nodeConverter =  new SimpleObjectProperty<NodeConverter<T>>(this, "nodeConverter", JFXComboBox.<T>defaultNodeConverter());
    public final void setNodeConverter(NodeConverter<T> value) { nodeConverterProperty().set(value); }
    public final NodeConverter<T> getNodeConverter() {return nodeConverterProperty().get(); }
            
    private static <T> NodeConverter<T> defaultNodeConverter() {
        return new NodeConverter<T>() {
			@Override public Node toNode(T object) {
				if(object == null) return null;
				StackPane selectedValueContainer = new StackPane();
				selectedValueContainer.getStyleClass().add("combo-box-selected-value-container");
				selectedValueContainer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
				Label selectedValueLabel;
				if(object instanceof Label) selectedValueLabel = new Label(((Label)object).getText());	
				else selectedValueLabel = new Label(object.toString());
				selectedValueLabel.setTextFill(Color.BLACK);
				selectedValueContainer.getChildren().add(selectedValueLabel);
				StackPane.setAlignment(selectedValueLabel, Pos.CENTER_LEFT);
				StackPane.setMargin(selectedValueLabel, new Insets(0,0,0,5));
				return selectedValueContainer;
			}
			@SuppressWarnings("unchecked")
			@Override public T fromNode(Node node) {
				return (T) node;
			}
			@Override
			public String toString(T object) {
				if(object == null) return null;
				if(object instanceof Label) return ((Label)object).getText();
				return object.toString();
			}
        };
    }
    
    
}
