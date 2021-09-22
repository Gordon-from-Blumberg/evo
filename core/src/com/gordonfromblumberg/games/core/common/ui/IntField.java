package com.gordonfromblumberg.games.core.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.util.function.IntConsumer;

public class IntField extends TextField {
    public IntField(String text, Skin skin) {
        super(text, skin);
    }

    public static IntField.IntFieldBuilder builder() {
        return new IntField.IntFieldBuilder();
    }

    public static class IntFieldBuilder {
        private String text;
        private Skin skin;
        private int minValue = Integer.MIN_VALUE, maxValue = Integer.MAX_VALUE;
        private IntConsumer handler;

        public IntFieldBuilder text(String text) {
            this.text = text;
            return this;
        }

        public IntFieldBuilder skin(Skin skin) {
            this.skin = skin;
            return this;
        }

        public IntFieldBuilder minValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public IntFieldBuilder maxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public IntFieldBuilder handler(IntConsumer handler) {
            this.handler = handler;
            return this;
        }

        public IntField build() {
            final IntField intField = new IntField(text, skin);
            intField.setTextFieldListener(new IntFieldListener(skin, handler, minValue, maxValue));
            return intField;
        }
    }

    private static class IntFieldListener implements TextFieldListener {
        private Skin skin;
        private IntConsumer handler;
        private int minValue, maxValue;

        private IntFieldListener(Skin skin, IntConsumer handler, int minValue, int maxValue) {
            this.skin = skin;
            this.handler = handler;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void keyTyped(TextField textField, char c) {
            try {
                int newValue = Integer.parseInt(textField.getText());
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
