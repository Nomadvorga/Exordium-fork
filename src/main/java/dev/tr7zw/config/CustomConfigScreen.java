package dev.tr7zw.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class CustomConfigScreen extends OptionsSubScreen {

    protected final Screen lastScreen;
    private OptionsList list;
    private Button done;
    private Button reset;

    public CustomConfigScreen(Screen lastScreen, String title) {
        super(lastScreen, Minecraft.getInstance().options, ComponentProvider.translatable(title));
        this.lastScreen = lastScreen;
    }

    @Override
    public void removed() {
        save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public OptionsList getOptions() {
        return list;
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this);
        this.addWidget(this.list);
        this.createFooter();
        initialize();
    }

    public abstract void initialize();

    public abstract void reset();

    public abstract void save();

    protected void createFooter() {
        done = Button.builder(CommonComponents.GUI_DONE, new OnPress() {
            @Override
            public void onPress(Button button) {
                CustomConfigScreen.this.onClose();
            }
        }).pos(this.width / 2 - 100, this.height - 27).size(200, 20).build();
        this.addRenderableWidget(done);

        reset = Button.builder(Component.translatable("controls.reset"), new OnPress() {
            @Override
            public void onPress(Button button) {
                reset();
                CustomConfigScreen.this.resize(width, height);
            }
        }).pos(this.width / 2 + 110, this.height - 27).size(60, 20).build();
        this.addRenderableWidget(reset);
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        this.list.updateSize(this.width, this.layout);
        reset.setPosition(this.width / 2 + 110, this.height - 27);
        done.setPosition(this.width / 2 - 100, this.height - 27);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        this.list.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    private <T> TooltipSupplier<T> getOptionalTooltip(String translationKey) {
        return new TooltipSupplier<T>() {

            @Override
            public Tooltip apply(T param1t) {
                String key = translationKey + ".tooltip";
                Component comp = Component.translatable(key);
                if (key.equals(comp.getString())) {
                    return null;
                } else {
                    return Tooltip.create(comp);
                }
            }
        };
    }

    public OptionInstance<Boolean> getBooleanOption(String translationKey, Supplier<Boolean> current,
            Consumer<Boolean> update) {
        return OptionInstance.createBoolean(translationKey, getOptionalTooltip(translationKey), current.get(), update);
    }

    public OptionInstance<Boolean> getOnOffOption(String translationKey, Supplier<Boolean> current,
            Consumer<Boolean> update) {
        return getBooleanOption(translationKey, current, update);
    }

    public OptionInstance<Double> getDoubleOption(String translationKey, float min, float max, float steps,
            Supplier<Double> current, Consumer<Double> update) {
        Double sliderValue = ((current.get() - min) / (max - min));
        return new OptionInstance<>(translationKey, getOptionalTooltip(translationKey), (comp, d) -> {
            double value = min + (d * (max - min));
            value = (int) (value / steps);
            value *= steps;
            return comp.copy().append(": " + round(value, 3));
        }, OptionInstance.UnitDouble.INSTANCE, Codec.doubleRange(min, max), sliderValue, (d) -> {
            double value = min + (d * (max - min));
            value = (int) (value / steps);
            value *= steps;
            update.accept(value);
        });
    }

    public OptionInstance<Integer> getIntOption(String translationKey, int min, int max, Supplier<Integer> current,
            Consumer<Integer> update) {
        return new OptionInstance<>(translationKey, getOptionalTooltip(translationKey),
                (comp, d) -> comp.copy().append(": " + d), new OptionInstance.IntRange(min, max), current.get(),
                update::accept);
    }

    @SuppressWarnings("rawtypes")
    public <T extends Enum> OptionInstance<T> getEnumOption(String translationKey, Class<T> targetEnum,
            Supplier<T> current, Consumer<T> update) {
        Map<String, T> mapping = new HashMap<>();
        Arrays.asList(targetEnum.getEnumConstants()).forEach(t -> mapping.put(t.name(), t));
        return new OptionInstance<>(translationKey, getOptionalTooltip(translationKey),
                (comp, t) -> Component.translatable(translationKey + "." + t.name()),
                new OptionInstance.Enum<>(Arrays.asList(targetEnum.getEnumConstants()),
                        Codec.STRING.xmap(mapping::get, Enum::name)),
                current.get(), update);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
