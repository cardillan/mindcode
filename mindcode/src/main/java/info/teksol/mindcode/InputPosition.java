package info.teksol.mindcode;

public record InputPosition(InputFile inputFile, int line, int charPositionInLine) {

    public String formatForIde() {
        return inputFile.absolutePath() + ":" + line + ":" + charPositionInLine;
    }
}
