package quarry.module;

public final class FeatureModule extends Module {
    public FeatureModule(String name) {
        this(name, false);
    }

    public FeatureModule(String name, boolean enabledByDefault) {
        super(name, enabledByDefault);
    }
}
