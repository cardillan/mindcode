package info.teksol.mc.common;

public record Statistics(int nodeCount, int moduleCount, int unoptimized, int optimized, int parseTime, int compileTime,
                         int optimizeTime, int runTime, int passes, int errorCount, int warningCount) {

    public Statistics() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public Statistics add(Statistics other){
        return new Statistics(
                nodeCount + other.nodeCount,
                moduleCount + other.moduleCount,
                unoptimized + other.unoptimized,
                optimized + other.optimized,
                parseTime + other.parseTime,
                compileTime + other.compileTime,
                optimizeTime + other.optimizeTime,
                runTime + other.runTime,
                passes + other.passes,
                errorCount + other.errorCount,
                warningCount + other.warningCount);
    }

    public Statistics add(int errorCount, int warningCount) {
        return new Statistics(nodeCount, moduleCount, unoptimized, optimized, parseTime, compileTime, optimizeTime, runTime, passes,
                this.errorCount + errorCount, this.warningCount + warningCount);
    }
}
