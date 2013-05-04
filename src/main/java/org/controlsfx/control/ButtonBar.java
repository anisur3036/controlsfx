/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.controlsfx.control;

import impl.org.controlsfx.skin.ButtonBarSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;

import org.controlsfx.dialogs.Action;

import com.sun.javafx.Utils;

/**
 * A ButtonBar is essentially an {@link HBox} for controls extending
 * {@link ButtonBase}, most notably {@link Button}, which have been annotated
 * with a specific type (from the {@link ButtonType} enumeration). By performing
 * this annotation, the ButtonBar is able to place the buttons in their correct
 * relative positions based on either OS-specific locations, or in an application
 * specific application (by setting the 
 * {@link #buttonOrderProperty() button order}).  
 * 
 * <p>The concept and API for this control borrows heavily from the MigLayout
 * button bar functionality.
 */
@SuppressWarnings("restriction")
public final class ButtonBar extends Control {
    
    /**************************************************************************
     * 
     * Static fields
     * 
     **************************************************************************/

    /**
     * The default button ordering on Windows.
     */
    public static final String BUTTON_ORDER_WINDOWS = "L_E+U+FBI_YNOCAH_R";
    
    /**
     * The default button ordering on Mac OS.
     */
    public static final String BUTTON_ORDER_MAC_OS  = "L_HE+U+FBI_NYCOA_R";
    
    /**
     * The default button ordering on Linux (specifically, GNOME).
     */
    public static final String BUTTON_ORDER_LINUX   = "L_HE+UNYACBXIO_R";
    
    
    
    /**************************************************************************
     * 
     * Static enumerations
     * 
     **************************************************************************/
    
    /**
     * An enumeration of all available button types. By designating ever button
     * in a {@link ButtonBar} as one of these types, the buttons will be 
     * appropriately positioned relative to all other buttons in the ButtonBar.
     */
    public static enum ButtonType {
        /**
         * Buttons with this style tag will statically end up on the left end of the bar.
         */
        LEFT("L"),
        
        /**
         * Buttons with this style tag will statically end up on the right end of the bar.
         */
        RIGHT("R"),
        
        /**
         * A tag for the "help" button that normally is supposed to be on the right.
         */
        HELP("H"),
        
        /**
         * A tag for the "help2" button that normally is supposed to be on the left.
         */
        HELP_2("E"),
        
        /**
         * A tag for the "yes" button.
         */
        YES("Y"),
        
        /**
         * A tag for the "no" button.
         */
        NO("N"),
        
        /**
         * A tag for the "next >" or "forward >" button.
         */
        NEXT_FORWARD("X"),
        
        /**
         * A tag for the "< back>" or "< previous" button.
         */
        BACK_PREVIOUS("B"),
        
        /**
         * A tag for the "finish".
         */
        FINISH("I"),
        
        /**
         * A tag for the "apply" button.
         */
        APPLY("A"),
        
        /**
         * A tag for the "cancel" or "close" button.
         */
        CANCEL_CLOSE("C"),
        
        /**
         * A tag for the "ok" or "done" button.
         */
        OK_DONE("O"),
        
        /**
         * All Uncategorized, Other, or "Unknown" buttons. Tag will be "other".
         */
        OTHER("U"),
        
        /**
         * A glue push gap that will take as much space as it can and at least 
         * an "unrelated" gap. (Platform dependent)
         */
        BIG_GAP("+"),
        
        /**
         * An "unrelated" gap. (Platform dependent)
         */
        SMALL_GAP("_");
        
        private final String typeCode;
        private ButtonType(String type) {
            this.typeCode = type;
        }
        
        public String getTypeCode() {
            return typeCode;
        }
    }
    
    /**
     * Sets the given ButtonType on the given button. If this button is
     * subsequently placed in a {@link ButtonBar} it will be placed in the 
     * correct position relative to all other buttons in the bar.
     * 
     * @param button The button to tag with the given type.
     * @param type The type to designate the button as.
     */
    public static void setType(ButtonBase button, ButtonType type) {
        button.getProperties().put(ButtonBarSkin.BUTTON_TYPE_PROPERTY, type);
    }
    
    /**
     * Sets the given ButtonType on the given {@link Action} If this action is
     * subsequently placed in a {@link ButtonBar} it will be placed in the 
     * correct position relative to all other buttons in the bar.
     * 
     * @param action The action to tag with the given type.
     * @param type The type to designate the action as.
     */
    public static void setType(Action action, ButtonType type) {
        action.getProperties().put(ButtonBarSkin.BUTTON_TYPE_PROPERTY, type);
    }
    
    
    
    /**************************************************************************
     * 
     * Private fields
     * 
     **************************************************************************/
    
    private ObservableList<ButtonBase> buttons = FXCollections.<ButtonBase>observableArrayList();
    
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/
    
    public ButtonBar() {
        getStyleClass().add("button-bar");
        
        // set the default values 
        if (Utils.isMac()) {
            setButtonOrder(BUTTON_ORDER_MAC_OS);
            setButtonMinWidth(70);
        } else if (Utils.isUnix()) {
            setButtonOrder(BUTTON_ORDER_LINUX);
            setButtonMinWidth(85);
        } else {
            // windows by default
            setButtonOrder(BUTTON_ORDER_WINDOWS);
            setButtonMinWidth(75);
        }
    }
    
    
    
    /**************************************************************************
     * 
     * Public API
     * 
     **************************************************************************/
    
    /**
     * {@inheritDoc}
     */
    @Override protected Skin<?> createDefaultSkin() {
        return new ButtonBarSkin(this);
    }

    /**
     * Placing buttons inside this ObservableList will instruct the ButtonBar
     * to position them relative to each other based on their specified
     * {@link ButtonType}. To set the ButtonType for a button, simply call
     * {@link ButtonBar#setType(ButtonBase, ButtonType)}, passing in the 
     * relevant ButtonType.
     *  
     * @return A list containing all buttons currently in the button bar, and 
     *      allowing for further buttons to be added or removed.
     */
    public ObservableList<ButtonBase> getButtons() {
        return buttons;
    }
    
    /**
     * A convenience method which is a shortcut for code of the following form:
     * 
     * <code>
     * Button detailsButton = createDetailsButton();
     * ButtonBar.setType(detailsButton, ButtonType.HELP_2);
     * buttonBar.getButtons().add(detailsButton);
     * </code>
     * 
     * @param button The button to add to this button bar instance.
     * @param buttonType The type of the button, such that it can be place correctly.
     */
    public void addButton(ButtonBase button, ButtonType buttonType) {
        if (button == null) return;
        
        ButtonBar.setType(button, buttonType);
        getButtons().add(button);
    }
    
    
    /**************************************************************************
     * 
     * Properties
     * 
     **************************************************************************/
    
    // --- Button order
    /**
     * The order for the typical buttons in a standard button bar. It is 
     * one letter per button type, and the applicable options are part of 
     * {@link ButtonType}. Default button orders for operating systems are also 
     * available: {@link #BUTTON_ORDER_WINDOWS}, {@link #BUTTON_ORDER_MAC_OS},
     * {@link #BUTTON_ORDER_LINUX}.
     */
    public final StringProperty buttonOrderProperty() {
        return buttonOrderProperty;
    }
    private final StringProperty buttonOrderProperty = 
            new SimpleStringProperty(this, "buttonOrder");
    
    /**
     * Sets the {@link #buttonOrderProperty() button order}
     * @param buttonOrder The currently set button order, which by default will
     *      be the OS-specific button order.
     */
    public final void setButtonOrder(String buttonOrder) {
        buttonOrderProperty.set(buttonOrder);
    }
    
    /**
     * Returns the current {@link #buttonOrderProperty() button order}.
     * @return The current {@link #buttonOrderProperty() button order}.
     */
    public final String getButtonOrder() {
        return buttonOrderProperty.get();
    }
    
    
    // --- button min width
    public final DoubleProperty buttonMinWidthProperty() {
        return buttonMinWidthProperty;
    }
    private final DoubleProperty buttonMinWidthProperty =
            new SimpleDoubleProperty(this, "buttonMinWidthProperty");
    public final void setButtonMinWidth(double value) {
        buttonMinWidthProperty.set(value);
    }
    public final double getButtonMinWidth() {
        return buttonMinWidthProperty.get();
    }

    // --- button uniform size
    
    public final BooleanProperty buttonUniformSizeProperty() {
        return buttonUniformSizeProperty;
    }
    private final BooleanProperty buttonUniformSizeProperty = new SimpleBooleanProperty(this, "buttonUniformSize", true);
    public final void setButtonUniformSize(boolean value) {
        buttonUniformSizeProperty.set(value);
    }
    
    public final boolean isButtonUniformSize() {
        return buttonUniformSizeProperty.get();
    }
    
    
    
    /**************************************************************************
     * 
     * Implementation
     * 
     **************************************************************************/
    

    
    
    /**************************************************************************
     * 
     * Support classes / enums
     * 
     **************************************************************************/
    
}
