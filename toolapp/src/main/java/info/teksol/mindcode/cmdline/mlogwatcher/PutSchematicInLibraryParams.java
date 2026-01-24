package info.teksol.mindcode.cmdline.mlogwatcher;

public class PutSchematicInLibraryParams implements Params {

    private String schematic;

    private boolean overwrite;

    public String getSchematic() {
        return schematic;
    }

    public void setSchematic(String schematic) {
        this.schematic = schematic;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
