package utils;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;

import java.io.Serializable;

import org.apache.logging.log4j.core.Appender;

@Plugin(
        name = "TextAreaAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class TextAreaAppender extends AbstractAppender {

    private final Layout<Serializable> layout;
    public static TextArea textArea;

    protected TextAreaAppender(String name, Filter filter, Layout<Serializable> layout) {
        super(name, filter, layout, false, null);
        this.layout = layout;
    }

    @PluginFactory
    public static TextAreaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("PatternLayout") Layout<Serializable> layout
    ) {
        return new TextAreaAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        Serializable msg = this.layout.toSerializable(event);
        Platform.runLater(() -> TextAreaAppender.textArea.appendText(msg.toString()));
    }
}