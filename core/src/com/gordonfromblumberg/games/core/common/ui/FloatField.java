package com.gordonfromblumberg.games.core.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gordonfromblumberg.games.core.common.utils.FloatConsumer;

public class FloatField extends TextField {
    public FloatField(String text, Skin skin) {
        super(text, skin);
    }

    public static FloatFieldBuilder builder() {
        return new FloatFieldBuilder();
    }

    public static class FloatFieldBuilder {
        private String text;
        private Skin skin;
        private float minValue = -Float.MAX_VALUE, maxValue = Float.MAX_VALUE;
        private FloatConsumer handler;

        public FloatFieldBuilder text(String text) {
            this.text = text;
            return this;
        }

        public FloatFieldBuilder skin(Skin skin) {
            this.skin = skin;
            return this;
        }

        public FloatFieldBuilder minValue(float minValue) {
            this.minValue = minValue;
            return this;
        }

        public FloatFieldBuilder maxValue(float maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public FloatFieldBuilder handler(FloatConsumer handler) {
            this.handler = handler;
            return this;
        }

        public FloatField build() {
            final FloatField floatField = new FloatField(text, skin);
            floatField.setTextFieldListener(new FloatFieldListener(skin, handler, minValue, maxValue));
            return floatField;
        }
    }

    private static class FloatFieldListener implements TextFieldListener {
        private Skin skin;
        private FloatConsumer handler;
        private float minValue, maxValue;

        private FloatFieldListener(Skin skin, FloatConsumer handler, float minValue, float maxValue) {
            this.skin = skin;
            this.handler = handler;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void keyTyped(TextField textField, char c) {
            try {
                float newValue = Float.parseFloat(textField.getText());
                if (newValue < minValue || newValue > maxValue) {
                    trySetError(textField);
                    return;
                }

                handler.accept(newValue);
                textField.setStyle(skin.get(TextFieldStyle.class));
            } catch (NumberFormatException e) {
                trySetError(textField);
            }
        }

        private void trySetError(TextField textField) {
            textField.setStyle(skin.get("error", TextFieldStyle.class));
        }
    }
}
